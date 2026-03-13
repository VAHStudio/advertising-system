/**
 * AI 助手类型定义
 */

// SSE 事件类型
// Dify 可能返回的事件类型：agent_message, message, tool_call, tool_response, error, end, etc.
export interface SseEvent {
  type: string;
  content?: string;
  answer?: string;  // Dify 原始字段名
  delta?: boolean;
  status?: string;
  toolCall?: ToolCallInfo;
  toolResponse?: ToolResponseInfo;
  tool_response?: any;  // Dify 原始字段
  navigation?: NavigationAction;
  error?: string;
  conversationId?: string;  // 会话ID，用于关联对话历史
}

// 工具调用信息
export interface ToolCallInfo {
  id: string;
  toolName: string;
}

// 工具响应信息
export interface ToolResponseInfo {
  toolCallId: string;
  toolName: string;
  success: boolean;
}

// 导航动作
export interface NavigationAction {
  action: 'navigate' | 'refresh' | 'open_modal' | 'none';
  target: string;
  params?: Record<string, unknown>;
  message?: string;
  toast?: ToastMessage;
}

// Toast 消息
export interface ToastMessage {
  type: 'success' | 'info' | 'warning' | 'error';
  message: string;
  duration?: number;
}

// 聊天消息
export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  isStreaming?: boolean;
  toolCalls?: ToolCallInfo[];
}

// AI 助手响应
export interface AiAssistantResponse {
  message: string;
  navigation?: NavigationAction;
  conversationId?: string;
}

// AI模式
export type AiMode = 'DIFY' | 'CUSTOM';

// 聊天请求
export interface ChatRequest {
  message: string;
  userId?: string;
  conversationId?: string;
  mode?: AiMode;
}

// 会话信息
export interface Conversation {
  id: number;
  userId: string;
  conversationId: string;
  title?: string;
  status: number;
  createdAt: string;
  updatedAt: string;
  lastMessageAt: string;
}
