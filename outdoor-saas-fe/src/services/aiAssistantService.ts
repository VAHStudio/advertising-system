import { API_BASE_URL, getToken } from './api';
import type { ChatRequest, SseEvent } from '../types/aiAssistant';

/**
 * 创建 SSE EventSource 连接
 * EventSource 不支持自定义 headers，通过 URL 参数传递 token
 * 
 * @param request 聊天请求
 * @returns EventSource 实例
 */
export function createEventSource(request: ChatRequest): EventSource {
  const params = new URLSearchParams();
  params.append('message', request.message);
  
  // 添加认证 token（SSE 通过 query param 传递）
  const token = getToken();
  if (token) {
    params.append('token', token);
  }
  
  if (request.conversationId) {
    params.append('conversationId', request.conversationId);
  }

  const url = `${API_BASE_URL}/ai-assistant/stream?${params.toString()}`;
  return new EventSource(url);
}

/**
 * 获取当前对话 ID（从 localStorage）
 */
export function getConversationId(): string | null {
  return localStorage.getItem('ai_conversation_id');
}

/**
 * 保存对话 ID
 */
export function saveConversationId(id: string): void {
  localStorage.setItem('ai_conversation_id', id);
}

/**
 * 清除对话 ID
 */
export function clearConversationId(): void {
  localStorage.removeItem('ai_conversation_id');
}

/**
 * 解析 SSE 事件数据
 */
export function parseSseEvent(data: string): SseEvent | null {
  try {
    return JSON.parse(data) as SseEvent;
  } catch {
    return null;
  }
}

export default {
  createEventSource,
  getConversationId,
  saveConversationId,
  clearConversationId,
  parseSseEvent,
};
