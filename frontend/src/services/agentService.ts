/**
 * Agent 服务 - 简化版
 * 只保留核心功能
 */

export interface AgentChatRequest {
  message: string;
  sessionId?: string;
  agentId?: string;
  selectedValue?: string;
}

export interface AgentChatResponse {
  type: string;
  message: string;
  sessionId: string;
  data?: Record<string, any>;
}

// 响应类型常量 - 简化
export const AgentResponseType = {
  TEXT: 'text',
  ERROR: 'error',
} as const;

/**
 * 发送消息到 Agent
 * 使用 /api/agents/chat 端点
 */
export const sendAgentMessage = async (
  request: AgentChatRequest
): Promise<AgentChatResponse> => {
  const apiRequest = {
    conversation_id: request.sessionId,
    user_id: 'user_001',
    message: request.message,
  };

  const response = await fetch('/api/agents/chat', {
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

  return {
    type: AgentResponseType.TEXT,
    message: result.data?.message?.content || '无响应',
    sessionId: request.sessionId || result.data?.conversation?.id || '',
    data: result.data,
  };
};

/**
 * 获取智能体列表
 */
export const getAgentList = async () => {
  const response = await fetch('/api/agents/list');
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
  const response = await fetch('/api/agents/conversations/start', {
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
  
  throw new Error(result.message || '创建对话失败');
};

/**
 * 获取对话历史
 */
export const getConversationHistory = async (conversationId: string, userId: string) => {
  const response = await fetch(`/api/agents/conversations/${conversationId}?user_id=${userId}`);
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
    ? `/api/agents/conversations/user/${userId}?agent_id=${agentId}`
    : `/api/agents/conversations/user/${userId}`;
    
  const response = await fetch(url);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  return [];
};
