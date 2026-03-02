/**
 * AI Agents 服务 - 与后端 AI Agents API 交互
 * 支持四个独立智能体、长记忆、文件上传等功能
 */

const API_BASE_URL = '/api';

// 获取当前用户ID（实际项目中应从认证系统获取）
const getCurrentUserId = (): string => {
  return localStorage.getItem('userId') || 'user_001';
};

// 智能体类型定义
export interface Agent {
  id: string;
  name: string;
  role: string;
  title: string;
  avatar: string;
  description: string;
  status: 'online' | 'offline' | 'busy';
  welcome_message: string;
}

// 对话类型定义
export interface Conversation {
  id: string;
  user_id: string;
  agent_id: string;
  agent_name: string;
  agent_avatar: string;
  title: string;
  context: Record<string, any>;
  status: 'active' | 'archived' | 'deleted';
  message_count: number;
  last_message_at: string;
  created_at: string;
}

// 消息类型定义
export interface Message {
  id: string;
  conversation_id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  content_type: 'text' | 'image' | 'file' | 'card' | 'action';
  metadata?: Record<string, any>;
  created_at: string;
}

// 附件类型定义
export interface Attachment {
  id: string;
  file_name: string;
  file_type: string;
  file_size: number;
  status: 'uploading' | 'processing' | 'ready' | 'error';
  extracted_text?: string;
  summary?: string;
  created_at: string;
}

/**
 * 获取所有智能体列表
 */
export const getAgents = async (): Promise<Agent[]> => {
  try {
    const response = await fetch(`${API_BASE_URL}/agents/list`);
    
    // 检查响应是否是JSON
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('API返回非JSON格式，可能服务未启动');
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message || '获取智能体列表失败');
  } catch (error) {
    console.log('API不可用，使用默认数据:', error);
    // 返回默认智能体数据（容错处理）
    return getDefaultAgents();
  }
};

/**
 * 获取单个智能体详情
 */
export const getAgent = async (agentId: string): Promise<Agent | null> => {
  try {
    const response = await fetch(`${API_BASE_URL}/agents/${agentId}`);
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    }
    return null;
  } catch (error) {
    console.error('获取智能体详情失败:', error);
    return null;
  }
};

/**
 * 开始新对话
 */
export const startConversation = async (agentId: string): Promise<{ conversation: Conversation; messages: Message[] }> => {
  try {
    const response = await fetch(`${API_BASE_URL}/agents/conversations/start`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        user_id: getCurrentUserId(),
        agent_id: agentId,
      }),
    });
    
    // 检查响应是否是JSON
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('API返回非JSON格式，可能服务未启动');
    }
    
    const result = await response.json();
    
    if (result.code === 200 || (result.data && result.data.conversation)) {
      // 保存当前对话ID
      localStorage.setItem('currentConversationId', result.data.conversation.id);
      return result.data;
    }
    throw new Error(result.message || '创建对话失败');
  } catch (error) {
    console.log('API不可用，创建本地对话:', error);
    // 创建本地对话（容错处理）
    return createLocalConversation(agentId);
  }
};

/**
 * 获取对话历史和消息
 */
export const getConversationHistory = async (conversationId: string): Promise<{ conversation: Conversation; messages: Message[] }> => {
  try {
    const response = await fetch(
      `${API_BASE_URL}/agents/conversations/${conversationId}?user_id=${getCurrentUserId()}`
    );
    
    // 检查响应是否是JSON
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('API返回非JSON格式，可能服务未启动');
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message || '获取对话历史失败');
  } catch (error) {
    console.log('API不可用，使用本地数据:', error);
    // 从本地存储获取（容错处理）
    return getLocalConversationHistory(conversationId);
  }
};

/**
 * 获取用户的所有对话
 */
export const getUserConversations = async (agentId?: string): Promise<Conversation[]> => {
  try {
    // 构建查询参数
    const params = new URLSearchParams();
    if (agentId) {
      params.append('agent_id', agentId);
    }
    const queryString = params.toString();
    const url = `${API_BASE_URL}/agents/conversations/user/${getCurrentUserId()}${queryString ? '?' + queryString : ''}`;
    
    const response = await fetch(url);
    
    // 检查响应是否是JSON
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('Invalid response format');
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message || '获取对话列表失败');
  } catch (error) {
    console.log('API不可用，使用本地存储:', error);
    // 从本地存储获取（容错处理）
    return getLocalUserConversations(agentId);
  }
};

/**
 * 发送消息
 */
export const sendMessage = async (
  conversationId: string, 
  message: string, 
  attachments?: string[]
): Promise<{ message: Message; conversation: Conversation }> => {
  try {
    const response = await fetch(`${API_BASE_URL}/agents/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        conversation_id: conversationId,
        user_id: getCurrentUserId(),
        message,
        attachments,
      }),
    });
    
    // 检查响应是否是JSON
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('API返回非JSON格式，可能服务未启动');
    }
    
    const result = await response.json();
    
    if (result.code === 200 || (result.data && result.data.message)) {
      // 保存到本地存储（实现长记忆）
      saveMessageToLocal(conversationId, result.data.message);
      return result.data;
    }
    throw new Error(result.message || '发送消息失败');
  } catch (error) {
    console.log('API不可用，使用模拟回复:', error);
    // 模拟回复（容错处理）
    return createMockReply(conversationId, message);
  }
};

/**
 * 上传文件
 */
export const uploadFile = async (conversationId: string, file: File): Promise<Attachment> => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('conversation_id', conversationId);
    formData.append('user_id', getCurrentUserId());
    
    const response = await fetch(`${API_BASE_URL}/agents/upload`, {
      method: 'POST',
      body: formData,
    });
    
    // 检查响应是否是JSON
    const contentType = response.headers.get('content-type');
    if (!contentType || !contentType.includes('application/json')) {
      throw new Error('API返回非JSON格式，可能服务未启动');
    }
    
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message || '上传文件失败');
  } catch (error) {
    console.log('API不可用，使用本地文件存储:', error);
    // 创建本地附件（容错处理）
    return createLocalAttachment(conversationId, file);
  }
};

/**
 * 归档对话
 */
export const archiveConversation = async (conversationId: string): Promise<void> => {
  try {
    await fetch(`${API_BASE_URL}/agents/conversations/${conversationId}/archive`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ user_id: getCurrentUserId() }),
    });
  } catch (error) {
    console.error('归档对话失败:', error);
  }
};

// ========== 本地存储辅助函数（用于长记忆和容错） ==========

const LOCAL_STORAGE_KEY = 'ai_agents_conversations';
const LOCAL_MESSAGES_KEY = 'ai_agents_messages';

function getDefaultAgents(): Agent[] {
  return [
    {
      id: 'agent_sales',
      name: '销售AI助理',
      role: 'sales',
      title: '销售方案专家',
      avatar: 'https://picsum.photos/seed/agent1/100/100',
      description: '专注于广告销售方案设计与客户管理的AI助手',
      status: 'online',
      welcome_message: `您好！我是销售AI助理，擅长广告销售方案设计。我可以帮您：
• 创建和优化广告投放方案
• 分析客户需求并推荐媒体资源
• 查询客户历史合作记录
• 生成报价单和合同草案
• 跟踪方案执行进度

请问有什么可以帮您的吗？`,
    },
    {
      id: 'agent_media',
      name: '媒介AI助理',
      role: 'media',
      title: '媒介资源专家',
      avatar: 'https://picsum.photos/seed/agent2/100/100',
      description: '专注于媒体资源管理和点位优化的AI助手',
      status: 'online',
      welcome_message: `您好！我是媒介AI助理，负责媒体资源管理。我可以帮您：
• 查询各区域广告位库存情况
• 优化资源配置和点位分配
• 分析点位历史投放效果
• 管理点位上下刊计划
• 协调销售与工程团队资源需求

请问有什么可以帮您的吗？`,
    },
    {
      id: 'agent_engineering',
      name: '工程AI助理',
      role: 'engineering',
      title: '工程维护专家',
      avatar: 'https://picsum.photos/seed/agent3/100/100',
      description: '专注于设备安装维护和设备管理的AI助手',
      status: 'offline',
      welcome_message: `您好！我是工程AI助理，负责设备安装和维护。我可以帮您：
• 制定设备维护计划和排期
• 安排上下刊作业任务
• 处理设备故障报修
• 进行工程验收和质量检查
• 管理维护团队工作调度

请问有什么可以帮您的吗？`,
    },
    {
      id: 'agent_finance',
      name: '财务AI助理',
      role: 'finance',
      title: '财务分析专家',
      avatar: 'https://picsum.photos/seed/agent4/100/100',
      description: '专注于财务管理和预算分析的AI助手',
      status: 'online',
      welcome_message: `您好！我是财务AI助理，负责财务管理和分析。我可以帮您：
• 管理和监控部门预算执行情况
• 审核和处理发票报销
• 分析投放成本和投资回报
• 生成财务报表和数据看板
• 协助合同财务条款审核

请问有什么可以帮您的吗？`,
    },
  ];
}

function createLocalConversation(agentId: string): { conversation: Conversation; messages: Message[] } {
  const agent = getDefaultAgents().find(a => a.id === agentId);
  const conversationId = `local_${Date.now()}`;
  
  const conversation: Conversation = {
    id: conversationId,
    user_id: getCurrentUserId(),
    agent_id: agentId,
    agent_name: agent?.name || 'AI助理',
    agent_avatar: agent?.avatar || '',
    title: `与${agent?.name || 'AI助理'}的对话`,
    context: {},
    status: 'active',
    message_count: 1,
    last_message_at: new Date().toISOString(),
    created_at: new Date().toISOString(),
  };
  
  const welcomeMessage: Message = {
    id: `msg_${Date.now()}`,
    conversation_id: conversationId,
    role: 'assistant',
    content: agent?.welcome_message || '您好！有什么可以帮您的吗？',
    content_type: 'text',
    created_at: new Date().toISOString(),
  };
  
  // 保存到本地存储
  saveConversationToLocal(conversation);
  saveMessageToLocal(conversationId, welcomeMessage);
  
  return { conversation, messages: [welcomeMessage] };
}

function saveConversationToLocal(conversation: Conversation): void {
  const conversations = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY) || '[]');
  const index = conversations.findIndex((c: Conversation) => c.id === conversation.id);
  
  if (index >= 0) {
    conversations[index] = conversation;
  } else {
    conversations.push(conversation);
  }
  
  localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(conversations));
}

function saveMessageToLocal(conversationId: string, message: Message): void {
  const messages = JSON.parse(localStorage.getItem(LOCAL_MESSAGES_KEY) || '{}');
  
  if (!messages[conversationId]) {
    messages[conversationId] = [];
  }
  
  messages[conversationId].push(message);
  localStorage.setItem(LOCAL_MESSAGES_KEY, JSON.stringify(messages));
  
  // 更新对话的消息计数和最后消息时间
  const conversations = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY) || '[]');
  const conversation = conversations.find((c: Conversation) => c.id === conversationId);
  if (conversation) {
    conversation.message_count = messages[conversationId].length;
    conversation.last_message_at = message.created_at;
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(conversations));
  }
}

function getLocalConversationHistory(conversationId: string): { conversation: Conversation; messages: Message[] } {
  const conversations = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY) || '[]');
  const conversation = conversations.find((c: Conversation) => c.id === conversationId);
  
  const messages = JSON.parse(localStorage.getItem(LOCAL_MESSAGES_KEY) || '{}');
  const conversationMessages = messages[conversationId] || [];
  
  if (!conversation) {
    throw new Error('Conversation not found');
  }
  
  return { conversation, messages: conversationMessages };
}

function getLocalUserConversations(agentId?: string): Conversation[] {
  const conversations = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY) || '[]');
  
  let result = conversations.filter((c: Conversation) => 
    c.user_id === getCurrentUserId() && c.status === 'active'
  );
  
  if (agentId) {
    result = result.filter((c: Conversation) => c.agent_id === agentId);
  }
  
  // 按最后消息时间排序
  result.sort((a: Conversation, b: Conversation) => 
    new Date(b.last_message_at).getTime() - new Date(a.last_message_at).getTime()
  );
  
  return result;
}

function createMockReply(conversationId: string, userMessage: string): { message: Message; conversation: Conversation } {
  // 模拟AI回复
  const mockReply: Message = {
    id: `msg_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    conversation_id: conversationId,
    role: 'assistant',
    content: `收到您的消息："${userMessage}"\n\n我是您的AI助理，正在处理您的请求。在实际生产环境中，我会调用大模型API来生成智能回复。`,
    content_type: 'text',
    created_at: new Date().toISOString(),
  };
  
  saveMessageToLocal(conversationId, mockReply);
  
  const conversations = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY) || '[]');
  const conversation = conversations.find((c: Conversation) => c.id === conversationId);
  
  return { message: mockReply, conversation };
}

// 本地附件存储键
const LOCAL_ATTACHMENTS_KEY = 'ai_agents_attachments';

function createLocalAttachment(conversationId: string, file: File): Attachment {
  const attachmentId = `att_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  
  const attachment: Attachment = {
    id: attachmentId,
    file_name: file.name,
    file_type: file.type || 'application/octet-stream',
    file_size: file.size,
    status: 'ready',
    created_at: new Date().toISOString(),
  };
  
  // 保存到本地存储
  const attachments = JSON.parse(localStorage.getItem(LOCAL_ATTACHMENTS_KEY) || '{}');
  if (!attachments[conversationId]) {
    attachments[conversationId] = [];
  }
  attachments[conversationId].push(attachment);
  localStorage.setItem(LOCAL_ATTACHMENTS_KEY, JSON.stringify(attachments));
  
  // 尝试读取文件内容（如果是文本文件）
  if (file.type.startsWith('text/') || file.name.endsWith('.txt')) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const content = e.target?.result as string;
      attachment.extracted_text = content;
      // 更新存储
      const updatedAttachments = JSON.parse(localStorage.getItem(LOCAL_ATTACHMENTS_KEY) || '{}');
      const convAttachments = updatedAttachments[conversationId] || [];
      const idx = convAttachments.findIndex((a: Attachment) => a.id === attachmentId);
      if (idx >= 0) {
        convAttachments[idx] = attachment;
        localStorage.setItem(LOCAL_ATTACHMENTS_KEY, JSON.stringify(updatedAttachments));
      }
    };
    reader.readAsText(file);
  }
  
  return attachment;
}
