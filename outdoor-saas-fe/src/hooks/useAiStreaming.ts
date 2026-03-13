import { ref } from 'vue';
import type { Ref } from 'vue';
import { 
  createEventSource, 
  saveConversationId, 
  getCurrentUserId, 
  saveUserId,
  getDefaultAiMode,
  setDefaultAiMode
} from '@/src/services/aiAssistantService';
import type { SseEvent, AiMode } from '@/src/types/aiAssistant';
import type { ChatMessage, ToolCallInfo } from './useAiStreaming.types';

export interface UseAiStreamingOptions {
  mode?: AiMode;
  onNavigation?: (nav: any) => void;
  onToolCall?: (tool: ToolCallInfo) => void;
  onError?: (error: string) => void;
}

export interface UseAiStreamingReturn {
  messages: ChatMessage[];
  isStreaming: boolean;
  currentTool: ToolCallInfo | null;
  currentConversationId: string | null;
  sendMessage: (content: string, conversationId?: string) => void;
  stopStreaming: () => void;
  clearMessages: () => void;
  setConversationId: (id: string | null) => void;
}

/**
 * 解析消息内容，提取 <think> 标签
 * @param content 原始内容
 * @returns { thinking: 思考内容, formal: 正式回复 }
 */
const parseThinkingContent = (content: string): { thinking: string; formal: string } => {
  const thinkMatch = content.match(/<think>([\s\S]*?)<\/think>/);
  
  if (thinkMatch) {
    // 提取 <think> 标签内的内容
    const thinking = thinkMatch[1].trim();
    // 移除 <think> 标签，保留其他内容
    const formal = content.replace(/<think>[\s\S]*?<\/think>/, '').trim();
    return { thinking, formal };
  }
  
  // 没有 <think> 标签，全部作为正式回复
  return { thinking: '', formal: content };
};

/**
 * AI 助手流式对话 Composable (Vue 3 版本)
 */
export function useAiStreaming(options: UseAiStreamingOptions = {}) {
  const messages = ref<ChatMessage[]>([]);
  const isStreaming = ref(false);
  const currentTool = ref<ToolCallInfo | null>(null);
  const currentConversationId = ref<string | null>(null);
  const currentMode = ref<AiMode>(options.mode || getDefaultAiMode());

  // Refs for internal state
  let eventSource: EventSource | null = null;
  let assistantMessageId = '';
  let pendingMessage: string | null = null;
  
  // 思考过程相关状态
  let thinkingStartTime: number | null = null;
  let accumulatedContent = ''; // 累积原始内容用于提取 think
  
  // 设置当前会话ID
  const setConversationId = (id: string | null) => {
    currentConversationId.value = id;
    if (id) {
      saveConversationId(id);
    }
  };

  // 设置AI模式
  const setMode = (mode: AiMode) => {
    currentMode.value = mode;
    setDefaultAiMode(mode);
    console.log('[AI] Mode switched to:', mode);
  };

  const sendMessage = (content: string, conversationId?: string) => {
    if (isStreaming.value) {
      console.log('[AI] Cannot send while streaming');
      return;
    }

    console.log('[AI] Sending message:', content);

    // Create message IDs
    const userMessageId = Date.now().toString();
    assistantMessageId = (Date.now() + 1).toString();

    // Reset thinking state
    thinkingStartTime = null;
    accumulatedContent = '';

    // Add messages to state
    messages.value = [
      ...messages.value,
      { id: userMessageId, role: 'user', content },
      { id: assistantMessageId, role: 'assistant', content: '', isStreaming: true },
    ];

    // Store pending message
    pendingMessage = content;
    isStreaming.value = true;

    // Close existing connection
    if (eventSource) {
      eventSource.close();
    }

    // 获取当前用户ID
    const userId = getCurrentUserId();
    if (userId) {
      saveUserId(userId);
    }

    // Create new EventSource
    console.log('[AI] Creating EventSource with mode:', currentMode.value);
    eventSource = createEventSource({ 
      message: content, 
      userId: userId || undefined,
      conversationId,
      mode: currentMode.value
    });
    console.log('[AI] EventSource created, URL:', eventSource.url);

    // Event handlers
    eventSource.onopen = () => {
      console.log('[AI] EventSource opened');
    };

    // 处理所有类型的事件（使用通用处理器）
    const handleEvent = (event: MessageEvent) => {
      console.log('[AI] ========== SSE Event ==========');
      console.log('[AI] Event type:', event.type);
      console.log('[AI] Event data:', event.data);
      console.log('[AI] Last event ID:', event.lastEventId);
      console.log('[AI] =================================');
      
      if (!event.data) {
        console.log('[AI] Empty event data, skipping');
        return;
      }

      let data: SseEvent;
      try {
        data = JSON.parse(event.data) as SseEvent;
      } catch (e) {
        console.error('[AI] Parse error:', e);
        console.error('[AI] Failed to parse:', event.data);
        return;
      }

      // 从事件数据中获取会话ID
      if (data.conversationId) {
        console.log('[AI] Saving conversation ID from data:', data.conversationId);
        saveConversationId(data.conversationId);
        currentConversationId.value = data.conversationId;
      } else if (event.lastEventId) {
        // 备选：从 event.lastEventId 获取
        console.log('[AI] Saving conversation ID from lastEventId:', event.lastEventId);
        saveConversationId(event.lastEventId);
        currentConversationId.value = event.lastEventId;
      }

      handleSseData(data);
    };

    // 监听所有可能的事件类型
    eventSource.addEventListener('agent_message', handleEvent);
    eventSource.addEventListener('agent_thought', handleEvent);
    eventSource.addEventListener('message_end', handleEvent);
    eventSource.addEventListener('tool_call', handleEvent);
    eventSource.addEventListener('error', handleEvent);
    eventSource.addEventListener('end', handleEvent);
    
    // 也监听默认的 message 事件
    eventSource.onmessage = handleEvent;

    eventSource.onerror = (error: Event) => {
      console.error('[AI] EventSource error:', error);
      options.onError?.('连接错误，请重试');
      cleanup();
    };
  };

  const handleSseData = (data: SseEvent) => {
    const targetId = assistantMessageId;
    
    // 打印完整数据用于调试
    console.log('[AI] Full SSE data:', JSON.stringify(data, null, 2));
    
    // 检查所有可能包含内容的字段（包括嵌套）
    let content = data.content || data.answer || '';
    
    // 检查嵌套结构
    if (!content && (data as any).data) {
      content = (data as any).data.content || (data as any).data.answer || (data as any).data.text || '';
    }
    
    // 检查其他可能的字段名
    if (!content) {
      content = (data as any).text || (data as any).message || (data as any).body || '';
    }

    console.log('[AI] Extracted content:', JSON.stringify(content).substring(0, 100));
    console.log('[AI] Content length:', content.length);

    switch (data.type) {
      case 'agent_thought':
        // 思考过程 - 只记录开始时间，内容不直接显示
        if (!thinkingStartTime) {
          thinkingStartTime = Date.now();
        }
        
        // 累积思考内容
        accumulatedContent += content;
        
        // 计算思考时间
        const thinkingTime = thinkingStartTime ? (Date.now() - thinkingStartTime) / 1000 : 0;
        
        // 尝试提取 think 标签（如果是流式返回的，可能还不完整）
        const parsed = parseThinkingContent(accumulatedContent);
        
        // 更新消息
        messages.value = messages.value.map((msg) =>
          msg.id === targetId
            ? { 
                ...msg, 
                thinking: parsed.thinking || accumulatedContent, // 如果还没闭合，显示已接收的部分
                thinkingTime,
                content: parsed.formal,
                isStreaming: true 
              }
            : msg
        );
        break;

      case 'agent_message':
      case 'message':
        // 正式回复内容
        if (!thinkingStartTime) {
          thinkingStartTime = Date.now();
        }
        
        // 累积内容
        accumulatedContent += content;
        
        // 解析 <think> 标签
        const parsedContent = parseThinkingContent(accumulatedContent);
        
        // 计算思考时间
        const currentThinkingTime = thinkingStartTime ? (Date.now() - thinkingStartTime) / 1000 : 0;
        
        messages.value = messages.value.map((msg) =>
          msg.id === targetId
            ? { 
                ...msg, 
                content: parsedContent.formal,
                thinking: parsedContent.thinking || msg.thinking,
                thinkingTime: currentThinkingTime,
                isStreaming: true 
              }
            : msg
        );
        break;

      case 'tool_call':
      case 'tool_calls':
        if (data.toolCall) {
          const toolInfo: ToolCallInfo = { name: data.toolCall.toolName || 'unknown', params: {} };
          currentTool.value = toolInfo;
          options.onToolCall?.(toolInfo);
          messages.value = messages.value.map((msg) =>
            msg.id === targetId
              ? { ...msg, toolCalls: [...(msg.toolCalls || []), toolInfo] }
              : msg
          );
        }
        break;

      case 'tool_response':
      case 'tool_responses':
        currentTool.value = null;
        if (data.navigation) {
          options.onNavigation?.(data.navigation);
        }
        break;

      case 'error':
        options.onError?.(data.error || '未知错误');
        messages.value = messages.value.map((msg) =>
          msg.id === targetId ? { ...msg, isStreaming: false } : msg
        );
        cleanup();
        break;

      case 'end':
      case 'workflow_finished':
      case 'message_end':
        console.log('[AI] Stream ended');
        
        // 最终解析一次内容
        const finalParsed = parseThinkingContent(accumulatedContent);
        const finalThinkingTime = thinkingStartTime ? (Date.now() - thinkingStartTime) / 1000 : 0;
        
        messages.value = messages.value.map((msg) =>
          msg.id === targetId 
            ? { 
                ...msg, 
                isStreaming: false,
                content: finalParsed.formal || msg.content,
                thinking: finalParsed.thinking || msg.thinking,
                thinkingTime: finalThinkingTime
              } 
            : msg
        );
        cleanup();
        break;

      default:
        console.log('[AI] Unknown event type:', data.type);
    }
  };

  const cleanup = () => {
    console.log('[AI] Cleanup');
    isStreaming.value = false;
    currentTool.value = null;
    eventSource?.close();
    eventSource = null;
    assistantMessageId = '';
    thinkingStartTime = null;
    accumulatedContent = '';
  };

  const stopStreaming = () => {
    const targetId = assistantMessageId;
    const finalThinkingTime = thinkingStartTime ? (Date.now() - thinkingStartTime) / 1000 : 0;
    
    // 最终解析
    const finalParsed = parseThinkingContent(accumulatedContent);
    
    messages.value = messages.value.map((msg) =>
      msg.id === targetId 
        ? { 
            ...msg, 
            isStreaming: false, 
            thinkingTime: finalThinkingTime,
            content: finalParsed.formal || msg.content,
            thinking: finalParsed.thinking || msg.thinking
          } 
        : msg
    );
    cleanup();
  };

  const clearMessages = () => {
    messages.value = [];
    // 清除对话ID，下次将创建新会话
    localStorage.removeItem('ai_conversation_id');
  };

  return {
    messages,
    isStreaming,
    currentTool,
    currentConversationId,
    currentMode,
    sendMessage,
    stopStreaming,
    clearMessages,
    setConversationId,
    setMode,
  };
}

export default useAiStreaming;
