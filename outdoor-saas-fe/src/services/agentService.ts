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
    headers: getAuthHeaders(),
    body: JSON.stringify(apiRequest),
  });

  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
      throw new Error('登录已过期，请重新登录');
    }
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
  const response = await fetch(`${API_BASE_URL}/agents/list`, {
    headers: getAuthHeaders(),
  });
  
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
      throw new Error('登录已过期，请重新登录');
    }
    throw new Error(`请求失败: ${response.status}`);
  }
  
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取智能体列表失败');
};

/**
 * 开始新对话
 */
export const startConversation = async (userId: string, agentId: string) => {
  const token = localStorage.getItem('access_token');
  
  const response = await fetch(`${API_BASE_URL}/agents/conversations/start`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : '',
    },
    body: JSON.stringify({ user_id: userId, agent_id: agentId }),
  });
  
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
      throw new Error('登录已过期，请重新登录');
    }
    throw new Error(`请求失败: ${response.status}`);
  }
  
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }

  throw new Error(result.message || '开始对话失败');
};

/**
 * AI Agent 服务对象 - 统一接口
 */
// 辅助函数：获取请求头
const getAuthHeaders = (): Record<string, string> => {
  const token = localStorage.getItem('access_token');
  return {
    'Content-Type': 'application/json',
    ...(token && { 'Authorization': `Bearer ${token}` }),
  };
};

// 辅助函数：处理响应
const handleResponse = async (response: Response) => {
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      localStorage.removeItem('access_token');
      localStorage.removeItem('user_info');
      window.location.href = '/login';
      throw new Error('登录已过期，请重新登录');
    }
    throw new Error(`请求失败: ${response.status}`);
  }
  
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '请求失败');
};

export const agentService = {
  chat: sendAgentMessage,
  getAgents: getAgentList,
  startConversation: (params: { agentId: string; userId: string }) =>
    startConversation(params.userId, params.agentId),
  getConversation: (conversationId: string) => getConversationHistory(conversationId, 'user_001'),
  getUserConversations: (userId: string) => getUserConversations(userId),
  nlpSelect: async (params: { query: string }) => {
    const response = await fetch(`${API_BASE_URL}/agent/v2/nlp-select`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(params),
    });
    return handleResponse(response);
  },
  getCommunityAvailability: async (params: any) => {
    const response = await fetch(`${API_BASE_URL}/agent/v2/community-availability`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(params),
    });
    return handleResponse(response);
  },
  checkConflicts: async (params: any) => {
    const response = await fetch(`${API_BASE_URL}/agent/v2/check-conflicts`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(params),
    });
    return handleResponse(response);
  },
  getCities: async () => {
    const response = await fetch(`${API_BASE_URL}/agents/cities`, {
      headers: getAuthHeaders(),
    });
    const result = await response.json();
    return result.data || [];
  },
  createSmartPlan: async (params: any) => {
    const response = await fetch(`${API_BASE_URL}/agents/plan/create-smart`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(params),
    });
    return handleResponse(response);
  },
};

export default agentService;

/**
 * 获取对话历史
 */
export const getConversationHistory = async (conversationId: string, userId: string) => {
  const response = await fetch(`${API_BASE_URL}/agents/conversations/${conversationId}?user_id=${userId}`, {
    headers: getAuthHeaders(),
  });
  
  return handleResponse(response);
};

/**
 * 获取用户的所有对话
 */
export const getUserConversations = async (userId: string, agentId?: string) => {
  const url = agentId 
    ? `${API_BASE_URL}/agents/conversations/user/${userId}?agent_id=${agentId}`
    : `${API_BASE_URL}/agents/conversations/user/${userId}`;
    
  const response = await fetch(url, {
    headers: getAuthHeaders(),
  });
  
  return handleResponse(response);
};
