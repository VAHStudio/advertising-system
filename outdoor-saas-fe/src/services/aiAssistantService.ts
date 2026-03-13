import { API_BASE_URL, getToken } from './api';
import type { ChatRequest, SseEvent, Conversation, ConversationMessage, AiMode } from '../types/aiAssistant';

// AI模式存储键
const AI_MODE_KEY = 'ai_mode';

/**
 * 获取默认AI模式
 */
export function getDefaultAiMode(): AiMode {
  return (localStorage.getItem(AI_MODE_KEY) as AiMode) || 'DIFY';
}

/**
 * 设置默认AI模式
 */
export function setDefaultAiMode(mode: AiMode): void {
  localStorage.setItem(AI_MODE_KEY, mode);
}

/**
 * 从 token 中解析用户 ID
 * @param token JWT token
 * @returns 用户 ID
 */
export function getUserIdFromToken(token: string | null): string | null {
  if (!token) return null;
  try {
    // JWT token 格式: header.payload.signature
    const payload = token.split('.')[1];
    if (!payload) return null;
    
    // Base64 解码
    const decoded = JSON.parse(atob(payload));
    return decoded.userId || decoded.sub || null;
  } catch {
    return null;
  }
}

/**
 * 获取当前用户 ID
 * 优先从 token 解析，其次从 localStorage 获取
 */
export function getCurrentUserId(): string | null {
  const token = getToken();
  const userId = getUserIdFromToken(token);
  if (userId) return userId;
  
  // 备选：从 localStorage 获取
  return localStorage.getItem('ai_user_id');
}

/**
 * 保存用户 ID
 */
export function saveUserId(userId: string): void {
  localStorage.setItem('ai_user_id', userId);
}

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
  
  // 添加 AI 模式（默认 DIFY）
  const mode = request.mode || getDefaultAiMode();
  params.append('mode', mode);
  
  // 添加认证 token（SSE 通过 query param 传递）
  const token = getToken();
  if (token) {
    params.append('token', token);
  }
  
  // 添加用户 ID（用于关联对话历史）
  const userId = request.userId || getCurrentUserId();
  if (userId) {
    params.append('userId', userId);
  }
  
  // 添加会话 ID（可选，不传则自动关联用户当前活跃会话）
  if (request.conversationId) {
    params.append('conversationId', request.conversationId);
  }

  const url = `${API_BASE_URL}/ai-assistant/stream?${params.toString()}`;
  console.log('[AI Service] Creating EventSource with mode:', mode, 'URL:', url);
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

/**
 * 按模式获取用户的会话列表（分页）
 */
export async function getConversations(
  mode: AiMode,
  page: number = 0,
  userId?: string
): Promise<{ conversations: Conversation[]; hasMore: boolean }> {
  const params = new URLSearchParams();
  params.append('mode', mode);
  params.append('page', String(page));
  if (userId) {
    params.append('userId', userId);
  }

  const url = `${API_BASE_URL}/ai-assistant/conversations?${params.toString()}`;
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${getToken() || ''}`,
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch conversations: ${response.status}`);
  }

  const result = await response.json();
  const conversations: Conversation[] = result.data || [];

  // 判断是否还有更多（返回数量等于每页大小则认为有更多）
  const hasMore = conversations.length === 10;

  return { conversations, hasMore };
}

/**
 * 创建指定模式的新会话
 */
export async function createConversation(
  mode: AiMode,
  userId?: string
): Promise<{ conversationId: string; mode: AiMode; userId: string }> {
  const params = new URLSearchParams();
  params.append('mode', mode);
  if (userId) {
    params.append('userId', userId);
  }

  const url = `${API_BASE_URL}/ai-assistant/conversations?${params.toString()}`;
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${getToken() || ''}`,
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to create conversation: ${response.status}`);
  }

  const result = await response.json();
  return result.data;
}

/**
 * 获取会话消息历史（分页）
 */
export async function getConversationMessages(
  conversationId: string,
  page: number = 0,
  userId?: string
): Promise<{ messages: ConversationMessage[]; hasMore: boolean }> {
  const params = new URLSearchParams();
  params.append('page', String(page));
  if (userId) {
    params.append('userId', userId);
  }

  const url = `${API_BASE_URL}/ai-assistant/conversations/${conversationId}/messages?${params.toString()}`;
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${getToken() || ''}`,
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to fetch messages: ${response.status}`);
  }

  const result = await response.json();
  const messages: ConversationMessage[] = result.data || [];

  // 转换为时间正序（数据库返回倒序）
  messages.reverse();

  // 判断是否还有更多
  const hasMore = messages.length === 10;

  return { messages, hasMore };
}

/**
 * 更新会话标题
 */
export async function updateConversationTitle(
  conversationId: string,
  title: string,
  userId?: string
): Promise<void> {
  const params = new URLSearchParams();
  params.append('title', title);
  if (userId) {
    params.append('userId', userId);
  }

  const url = `${API_BASE_URL}/ai-assistant/conversations/${conversationId}/title?${params.toString()}`;
  const response = await fetch(url, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${getToken() || ''}`,
    },
  });

  if (!response.ok) {
    throw new Error(`Failed to update title: ${response.status}`);
  }
}

/**
 * 删除会话
 */
export async function deleteConversation(conversationId: string, userId?: string): Promise<void> {
  const params = new URLSearchParams();
  if (userId) {
    params.append('userId', userId);
  }
  
  const url = `${API_BASE_URL}/ai-assistant/conversations/${conversationId}?${params.toString()}`;
  const response = await fetch(url, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${getToken() || ''}`,
    },
  });
  
  if (!response.ok) {
    throw new Error(`Failed to delete conversation: ${response.status}`);
  }
}

/**
 * 获取Custom Agent会话历史
 * 
 * @param sessionId 会话ID
 * @param mode AI模式（默认为CUSTOM）
 * @returns 会话历史消息列表
 */
export async function getSessionHistory(
  sessionId: string, 
  mode: AiMode = 'CUSTOM'
): Promise<{ sessionId: string; mode: string; messages: any[] }> {
  const params = new URLSearchParams();
  params.append('mode', mode);
  
  const url = `${API_BASE_URL}/ai-assistant/history/${sessionId}?${params.toString()}`;
  const response = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${getToken() || ''}`,
    },
  });
  
  if (!response.ok) {
    throw new Error(`Failed to fetch session history: ${response.status}`);
  }
  
  const result = await response.json();
  return result.data;
}

export default {
  createEventSource,
  getConversationId,
  saveConversationId,
  clearConversationId,
  parseSseEvent,
  getUserIdFromToken,
  getCurrentUserId,
  saveUserId,
  getConversations,
  createConversation,
  deleteConversation,
  getConversationMessages,
  updateConversationTitle,
  getSessionHistory,
  getDefaultAiMode,
  setDefaultAiMode,
};
