/**
 * Agent 服务 - PC端
 * 支持完整的智能体对话功能
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:16000/api';

export interface AgentChatRequest {
  message: string;
  sessionId?: string;
  agentId?: string;
  selectedValue?: string;
}

export interface Action {
  label: string;
  value: string;
  type: 'primary' | 'secondary' | 'danger';
}

export interface AgentChatResponse {
  type: string;
  message: string;
  sessionId: string;
  step?: string;
  data?: Record<string, any>;
  actions?: Action[];
}

// 响应类型常量
export const AgentResponseType = {
  TEXT: 'text',
  ERROR: 'error',
  CITY_SELECTION: 'city_selection',
  DATE_SELECTION: 'date_selection',
  POINT_SELECTION: 'point_selection',
  PLAN_CREATED: 'plan_created',
  LOADING: 'loading',
} as const;

/**
 * 发送消息到 Agent
 * 使用 /api/agents/chat 端点
 */
export const sendAgentMessage = async (
  request: AgentChatRequest
): Promise<AgentChatResponse> => {
  const apiRequest: any = {
    conversation_id: request.sessionId,
    user_id: 'user_001',
    agent_id: request.agentId || 'agent_sales',
  };

  if (request.selectedValue) {
    apiRequest.selected_value = request.selectedValue;
  } else {
    apiRequest.message = request.message;
  }

  const response = await fetch(`${API_BASE_URL}/agents/chat`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(apiRequest),
  });

  if (!response.ok) {
    throw new Error('Agent 请求失败');
  }

  const result = await response.json();
  
  if (result.code !== 200) {
    throw new Error(result.message || '请求失败');
  }

  const data = result.data;
  
  return {
    type: data?.type || AgentResponseType.TEXT,
    message: data?.message || '无响应',
    sessionId: data?.sessionId || request.sessionId || '',
    step: data?.step,
    data: data?.data,
    actions: data?.actions,
  };
};

/**
 * 获取智能体列表
 */
export const getAgentList = async () => {
  const response = await fetch(`${API_BASE_URL}/agents/list`);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  return [];
};

/**
 * 开始新对话
 */
export const startConversation = async (userId: string, agentId: string) => {
  const response = await fetch(`${API_BASE_URL}/agents/conversations/start`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ user_id: userId, agent_id: agentId }),
  });
  
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }

  return [];
};

/**
 * AI Agent 服务对象 - 统一接口
 */
export const agentService = {
  chat: sendAgentMessage,
  getAgents: getAgentList,
  startConversation: (params: { agentId: string; userId: string }) =>
    startConversation(params.userId, params.agentId),
  getConversation: (conversationId: string) => getConversationHistory(conversationId, 'user_001'),
  getUserConversations: (userId: string) => getUserConversations(userId),
  nlpSelect: (params: { query: string }) =>
    fetch(`${API_BASE_URL}/agent/v2/nlp-select`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    }).then(r => r.json()).then(r => r.data),
  getCommunityAvailability: (params: any) =>
    fetch(`${API_BASE_URL}/agent/v2/community-availability`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    }).then(r => r.json()).then(r => r.data),
  checkConflicts: (params: any) =>
    fetch(`${API_BASE_URL}/agent/v2/check-conflicts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    }).then(r => r.json()).then(r => r.data),
  getCities: () =>
    fetch(`${API_BASE_URL}/agents/cities`).then(r => r.json()).then(r => r.data || []),
  createSmartPlan: (params: any) =>
    fetch(`${API_BASE_URL}/agents/plan/create-smart`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(params),
    }).then(r => r.json()).then(r => r.data),
};

export default agentService;

/**
 * 获取对话历史
 */
export const getConversationHistory = async (conversationId: string, userId: string) => {
  const response = await fetch(`${API_BASE_URL}/agents/conversations/${conversationId}?user_id=${userId}`);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取对话历史失败');
};

/**
 * 获取用户的所有对话
 */
export const getUserConversations = async (userId: string, agentId?: string) => {
  const url = agentId 
    ? `${API_BASE_URL}/agents/conversations/user/${userId}?agent_id=${agentId}`
    : `${API_BASE_URL}/agents/conversations/user/${userId}`;
    
  const response = await fetch(url);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  return [];
};
