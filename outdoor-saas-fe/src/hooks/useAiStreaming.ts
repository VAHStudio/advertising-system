import { ref } from 'vue';
import type { Ref } from 'vue';
import { createEventSource, saveConversationId } from '@/src/services/aiAssistantService';
import type { SseEvent } from '@/src/types/aiAssistant';
import type { ChatMessage, ToolCallInfo } from './useAiStreaming.types';

export interface UseAiStreamingOptions {
  onNavigation?: (nav: any) => void;
  onToolCall?: (tool: ToolCallInfo) => void;
  onError?: (error: string) => void;
}

export interface UseAiStreamingReturn {
  messages: ChatMessage[];
  isStreaming: boolean;
  currentTool: ToolCallInfo | null;
  sendMessage: (content: string) => void;
  stopStreaming: () => void;
  clearMessages: () => void;
}

/**
 * AI 助手流式对话 Composable (Vue 3 版本)
 */
export function useAiStreaming(options: UseAiStreamingOptions = {}) {
  const messages = ref<ChatMessage[]>([]);
  const isStreaming = ref(false);
  const currentTool = ref<ToolCallInfo | null>(null);

  // Refs for internal state
  let eventSource: EventSource | null = null;
  let assistantMessageId = '';
  let pendingMessage: string | null = null;

  const sendMessage = (content: string) => {
    if (isStreaming.value) {
      console.log('[AI] Cannot send while streaming');
      return;
    }

    console.log('[AI] Sending message:', content);

    // Create message IDs
    const userMessageId = Date.now().toString();
    assistantMessageId = (Date.now() + 1).toString();

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

    // Create new EventSource
    console.log('[AI] Creating EventSource...');
    eventSource = createEventSource({ message: content });
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

      if (event.lastEventId) {
        console.log('[AI] Saving conversation ID:', event.lastEventId);
        saveConversationId(event.lastEventId);
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
      case 'agent_message':
      case 'message':
      case 'agent_thought':
        // 更新消息内容（即使是空字符串也更新，以触发UI刷新）
        console.log('[AI] Updating message, targetId:', targetId, 'content:', content);
        console.log('[AI] Current messages:', messages.value.map(m => ({ id: m.id, role: m.role, contentLen: m.content.length })));
        messages.value = messages.value.map((msg) =>
          msg.id === targetId
            ? { ...msg, content: msg.content + content, isStreaming: true }
            : msg
        );
        console.log('[AI] Updated messages:', messages.value.map(m => ({ id: m.id, role: m.role, contentLen: m.content.length, isStreaming: m.isStreaming })));
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
        messages.value = messages.value.map((msg) =>
          msg.id === targetId ? { ...msg, isStreaming: false } : msg
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
  };

  const stopStreaming = () => {
    const targetId = assistantMessageId;
    messages.value = messages.value.map((msg) =>
      msg.id === targetId ? { ...msg, isStreaming: false } : msg
    );
    cleanup();
  };

  const clearMessages = () => {
    messages.value = [];
  };

  return {
    messages,
    isStreaming,
    currentTool,
    sendMessage,
    stopStreaming,
    clearMessages,
  };
}

export default useAiStreaming;
