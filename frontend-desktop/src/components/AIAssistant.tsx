import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  ArrowLeft, 
  MoreVertical,
  Mic,
  Send,
  Plus,
  Bot,
  Loader2,
  CheckCircle,
  MapPin,
  Calendar,
  Building2,
  X
} from 'lucide-react';
import { 
  sendAgentMessage, 
  startConversation,
  AgentChatResponse, 
  AgentResponseType,
  Action 
} from '../services/agentService';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:16000/api';

// Agent ID到名称的映射
const agentIdToName: Record<string, string> = {
  'agent_sales': '销售AI助理',
  'agent_media': '媒介AI助理',
  'agent_engineering': '工程AI助理',
  'agent_finance': '财务AI助理',
};

// Agent名称到ID的映射
const agentNameToId: Record<string, string> = {
  '销售AI助理': 'agent_sales',
  '媒介AI助理': 'agent_media',
  '工程AI助理': 'agent_engineering',
  '财务AI助理': 'agent_finance',
};

// 根据agent ID获取欢迎消息
const getWelcomeMessage = (agentId: string): string => {
  const messages: Record<string, string> = {
    'agent_sales': '您好！我是销售AI助理。我可以帮您创建广告方案、管理客户、分析投放效果等。\n\n例如您可以对我说：\n• "帮我建个可口可乐的3月份广告方案，选择10个空闲道闸"\n• "查询南京有哪些可用社区"',
    'agent_media': '您好！我是媒介AI助理。我可以帮您管理点位资源、查询库存、优化资源配置等。\n\n例如您可以对我说：\n• "查询玄武区有哪些空闲点位"\n• "帮我安排10个道闸广告位"',
    'agent_engineering': '您好！我是工程AI助理。我可以帮您管理工程任务、安排维护、验收设备等。\n\n例如您可以对我说：\n• "安排明天去检修设备"\n• "查询最近的维护计划"',
    'agent_finance': '您好！我是财务AI助理。我可以帮您管理预算、处理发票、分析成本等。\n\n例如您可以对我说：\n• "查询本月预算使用情况"\n• "帮我审批几张发票"',
  };
  return messages[agentId] || messages['agent_sales'];
};

// 获取快捷建议按钮
const getQuickSuggestions = (agentId: string): Array<{label: string, text: string}> => {
  const suggestions: Record<string, Array<{label: string, text: string}>> = {
    'agent_sales': [
      { label: '📋 帮我建个方案', text: '帮我创建一个宝马汽车南京道闸广告投放方案，50个道闸，4月投放' },
      { label: '🔍 查询空闲点位', text: '查询南京有哪些空闲道闸点位' },
      { label: '📊 查看已创建方案', text: '查看我最近创建的广告方案' },
    ],
    'agent_media': [
      { label: '📍 查询点位库存', text: '查询玄武区有哪些空闲点位' },
      { label: '📈 资源分配建议', text: '帮我优化一下资源配置' },
      { label: '📋 查看上下刊计划', text: '查看本月的上下刊计划' },
    ],
    'agent_engineering': [
      { label: '🔧 创建维护任务', text: '安排明天去检修设备' },
      { label: '📅 查看维护计划', text: '查询最近的维护计划' },
      { label: '✅ 工程验收', text: '进行工程验收' },
    ],
    'agent_finance': [
      { label: '💰 查询预算', text: '查询本月预算使用情况' },
      { label: '🧾 处理发票', text: '帮我审批几张发票' },
      { label: '📊 成本分析', text: '分析一下本月的成本情况' },
    ],
  };
  return suggestions[agentId] || suggestions['agent_sales'];
};

const assistants = [
  { 
    id: 'agent_sales', 
    name: '销售AI助理', 
    title: '销售方案专家',
    icon: Bot, 
    desc: '创建广告方案、管理客户、分析投放效果', 
    color: 'bg-blue-500',
    welcome: '您好！我是销售AI助理。我可以帮您创建广告方案、管理客户、分析投放效果等。'
  },
  { 
    id: 'agent_media', 
    name: '媒介AI助理', 
    title: '媒介资源专家',
    icon: MapPin, 
    desc: '点位推荐、库存查询、资源配置优化', 
    color: 'bg-purple-500',
    welcome: '您好！我是媒介AI助理。我可以帮您管理点位资源、查询库存、优化资源配置等。'
  },
  { 
    id: 'agent_engineering', 
    name: '工程AI助理', 
    title: '工程维护专家',
    icon: Building2, 
    desc: '上刊进度、设备维护、维修指导', 
    color: 'bg-orange-500',
    welcome: '您好！我是工程AI助理。我可以帮您管理工程任务、安排维护、验收设备等。'
  },
  { 
    id: 'agent_finance', 
    name: '财务AI助理', 
    title: '财务分析专家',
    icon: CheckCircle, 
    desc: '预算管理、发票处理、成本分析', 
    color: 'bg-emerald-500',
    welcome: '您好！我是财务AI助理。我可以帮您管理预算、处理发票、分析成本等。'
  },
];

interface Message {
  id: string;
  role: 'user' | 'ai';
  type: string;
  content: string;
  data?: any;
  actions?: Action[];
  timestamp: Date;
}

interface PaginationInfo {
  total: number;
  pageNum: number;
  pageSize: number;
  totalPages: number;
  hasMore: boolean;
}

export default function AIAssistant() {
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);
  const [selectedAgent, setSelectedAgent] = useState<string | null>(null);
  const [selectedAgentId, setSelectedAgentId] = useState<string>('agent_sales');
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [sessionId, setSessionId] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [currentStep, setCurrentStep] = useState('intent');
  const [isInitialized, setIsInitialized] = useState(false);
  const [pagination, setPagination] = useState<PaginationInfo>({
    total: 0,
    pageNum: 1,
    pageSize: 10,
    totalPages: 0,
    hasMore: false,
  });
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const messagesContainerRef = useRef<HTMLDivElement>(null);
  const observerRef = useRef<IntersectionObserver | null>(null);
  const loadMoreTriggerRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (isOpen && selectedAgent && !isInitialized) {
      initializeChat();
    }
  }, [isOpen, selectedAgent]);

  const initializeChat = async () => {
    if (isInitialized) return;

    const agentId = selectedAgentId || 'agent_sales';
    const conversationId = localStorage.getItem('currentConversationId');

    if (conversationId) {
      setSessionId(conversationId);
      // 加载第一页历史消息（默认10条）
      await loadConversationHistory(conversationId, 1, 10);
    } else {
      // 新对话，显示欢迎消息
      setMessages([
        {
          id: 'welcome',
          role: 'ai',
          type: AgentResponseType.TEXT,
          content: getWelcomeMessage(agentId),
          timestamp: new Date(),
        },
      ]);
      setIsInitialized(true);
    }
  };

  const loadConversationHistory = async (conversationId: string, pageNum: number = 1, pageSize: number = 10, append: boolean = false) => {
    try {
      if (pageNum === 1) {
        setIsLoading(true);
      } else {
        setIsLoadingMore(true);
      }

      const response = await fetch(`${API_BASE_URL}/agents/conversations/${conversationId}?user_id=user_001&pageNum=${pageNum}&pageSize=${pageSize}`);
      const result = await response.json();

      if (result.code === 200 && result.data) {
        const { messages, pagination: paginationData } = result.data;

        const formattedMessages = messages.map((msg: any) => ({
          id: msg.id,
          role: msg.role === 'assistant' ? 'ai' : 'user',
          type: AgentResponseType.TEXT,
          content: msg.content,
          actions: msg.metadata?.actions,
          timestamp: new Date(msg.created_at || msg.createdAt),
        }));

        if (append) {
          // 加载更多：将新消息添加到前面
          setMessages(prev => [...formattedMessages, ...prev]);
        } else {
          // 首次加载：替换所有消息
          setMessages(formattedMessages);
        }

        // 更新分页信息
        setPagination(paginationData);

        // 恢复上下文状态
        const context = result.data.conversation?.context || {};
        if (context.step) {
          setCurrentStep(context.step);
        }
      }
    } catch (error) {
      console.error('加载对话历史失败:', error);
      if (!append) {
        setMessages([
          {
            id: 'welcome',
            role: 'ai',
            type: AgentResponseType.TEXT,
            content: getWelcomeMessage(selectedAgentId),
            timestamp: new Date(),
          },
        ]);
      }
    } finally {
      setIsLoading(false);
      setIsLoadingMore(false);
      setIsInitialized(true);
    }
  };

  // 加载更多历史消息
  const loadMoreMessages = async () => {
    if (isLoadingMore || !pagination.hasMore || !sessionId) return;

    const nextPage = pagination.pageNum + 1;
    await loadConversationHistory(sessionId, nextPage, pagination.pageSize, true);
  };

  // 设置 Intersection Observer 监听滚动加载
  useEffect(() => {
    if (!isInitialized || !pagination.hasMore) return;

    const options = {
      root: messagesContainerRef.current,
      rootMargin: '50px 0px 0px 0px',
      threshold: 0.1,
    };

    observerRef.current = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && pagination.hasMore && !isLoadingMore) {
          loadMoreMessages();
        }
      });
    }, options);

    if (loadMoreTriggerRef.current) {
      observerRef.current.observe(loadMoreTriggerRef.current);
    }

    return () => {
      if (observerRef.current) {
        observerRef.current.disconnect();
      }
    };
  }, [isInitialized, pagination.hasMore, isLoadingMore, sessionId, pagination.pageNum]);

  const handleOpen = () => {
    setIsOpen(true);
  };

  const handleClose = () => {
    setIsOpen(false);
    setTimeout(() => {
      setSelectedAgent(null);
      setSelectedAgentId('agent_sales');
      setMessages([]);
      setSessionId('');
      setIsInitialized(false);
      setCurrentStep('intent');
      setPagination({
        total: 0,
        pageNum: 1,
        pageSize: 10,
        totalPages: 0,
        hasMore: false,
      });
      if (observerRef.current) {
        observerRef.current.disconnect();
        observerRef.current = null;
      }
    }, 300);
  };

  const selectAssistant = (agentId: string) => {
    const agent = assistants.find(a => a.id === agentId);
    setSelectedAgent(agent?.name || null);
    setSelectedAgentId(agentId);
  };

  const goBackToSelection = () => {
    setSelectedAgent(null);
    setSelectedAgentId('agent_sales');
    setMessages([]);
    setSessionId('');
    setIsInitialized(false);
    setCurrentStep('intent');
    setPagination({
      total: 0,
      pageNum: 1,
      pageSize: 10,
      totalPages: 0,
      hasMore: false,
    });
    if (observerRef.current) {
      observerRef.current.disconnect();
      observerRef.current = null;
    }
  };

  const handleSendMessage = async (messageText?: string) => {
    const textToSend = messageText || inputMessage;
    if (!textToSend.trim() || isLoading) return;

    const userMessage: Message = {
      id: `user-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      role: 'user',
      type: 'text',
      content: textToSend,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputMessage('');
    setIsLoading(true);

    try {
      let currentSessionId = sessionId;
      if (!currentSessionId) {
        const convResult = await startConversation('user_001', selectedAgentId);
        currentSessionId = convResult.conversation.id;
        setSessionId(currentSessionId);
        localStorage.setItem('currentConversationId', currentSessionId);
      }
      
      const response = await sendAgentMessage({
        message: textToSend,
        sessionId: currentSessionId,
        agentId: selectedAgentId,
      });

      if (response.sessionId) {
        setSessionId(response.sessionId);
      }
      if (response.step) {
        setCurrentStep(response.step);
      }

      const aiMessage: Message = {
        id: `ai-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
        role: 'ai',
        type: response.type,
        content: response.message,
        data: response.data,
        actions: response.actions,
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error('发送消息失败:', error);
      const errorMessage: Message = {
        id: `error-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
        role: 'ai',
        type: AgentResponseType.ERROR,
        content: '抱歉，我遇到了一些问题。请稍后重试。',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleActionClick = async (action: Action, messageData?: any) => {
    if (isLoading) return;

    // 处理查看方案详情按钮 - 跳转到智能方案页面
    if (action.label === '查看方案详情' || action.value === '/campaign-detail') {
      // 从消息数据中提取方案信息
      const planData = messageData?.plan || messageData;
      if (planData?.id || planData?.planNo) {
        // 关闭AI助手弹窗
        handleClose();
        // 跳转到智能方案页面，可以通过state传递方案信息
        navigate('/plans', { 
          state: { 
            highlightPlanId: planData?.id,
            highlightPlanNo: planData?.planNo,
            autoOpenDetail: true
          }
        });
        return;
      }
    }

    const userMessage: Message = {
      id: `user-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      role: 'user',
      type: 'text',
      content: `选择了: ${action.label}`,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setIsLoading(true);

    try {
      const response = await sendAgentMessage({
        message: '',
        sessionId: sessionId,
        selectedValue: action.value,
        agentId: selectedAgentId,
      });

      if (response.sessionId) {
        setSessionId(response.sessionId);
      }
      if (response.step) {
        setCurrentStep(response.step);
      }

      const aiMessage: Message = {
        id: `ai-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
        role: 'ai',
        type: response.type,
        content: response.message,
        data: response.data,
        actions: response.actions,
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error('处理操作失败:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // 渲染消息内容
  const renderMessageContent = (message: Message) => {
    switch (message.type) {
      case AgentResponseType.CITY_SELECTION:
        return (
          <div className="space-y-3">
            <p className="whitespace-pre-line">{message.content}</p>
            {message.data?.cities && (
              <div className="grid grid-cols-2 gap-2 mt-3">
                {message.data.cities.map((city: string) => (
                  <button
                    key={city}
                    onClick={() => handleActionClick({ label: city, value: city, type: 'primary' })}
                    className="flex items-center justify-center gap-2 p-3 bg-primary/10 hover:bg-primary/20 text-primary rounded-lg transition-colors text-sm"
                  >
                    <MapPin size={14} />
                    {city}
                  </button>
                ))}
              </div>
            )}
          </div>
        );

      case AgentResponseType.DATE_SELECTION:
        return (
          <div className="space-y-3">
            <p className="whitespace-pre-line">{message.content}</p>
            <div className="flex flex-wrap gap-2 mt-3">
              {message.actions?.map((action) => (
                <button
                  key={action.value}
                  onClick={() => handleActionClick(action, message.data)}
                  className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm ${
                    action.type === 'primary'
                      ? 'bg-primary text-white hover:bg-primary/90'
                      : 'bg-slate-100 dark:bg-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-200'
                  }`}
                >
                  <Calendar size={14} />
                  {action.label}
                </button>
              ))}
            </div>
          </div>
        );

      case AgentResponseType.POINT_SELECTION:
        return (
          <div className="space-y-3">
            <p className="whitespace-pre-line">{message.content}</p>
            
            {message.data?.selectionResult?.communityStats && (
              <div className="bg-slate-50 dark:bg-slate-800/50 rounded-lg p-3 mt-3">
                <h4 className="text-sm font-semibold mb-2">点位分布</h4>
                <div className="space-y-1">
                  {message.data.selectionResult.communityStats.map((stat: any) => (
                    <div key={stat.communityId} className="flex justify-between text-sm">
                      <span className="flex items-center gap-1">
                        <Building2 size={12} className="text-slate-400" />
                        {stat.communityName}
                      </span>
                      <span className="font-medium">{stat.selectedCount}个</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {message.data?.totalAvailable !== undefined && (
              <div className="text-xs text-slate-500 mt-2">
                可用点位: {message.data.totalAvailable}个
              </div>
            )}

            <div className="flex flex-wrap gap-2 mt-4">
              {message.actions?.map((action) => (
                <button
                  key={action.value}
                  onClick={() => handleActionClick(action, message.data)}
                  className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm ${
                    action.type === 'primary'
                      ? 'bg-primary text-white hover:bg-primary/90'
                      : action.type === 'danger'
                      ? 'bg-red-100 text-red-600 hover:bg-red-200'
                      : 'bg-slate-100 dark:bg-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-200'
                  }`}
                >
                  {action.value === 'confirm' && <CheckCircle size={14} />}
                  {action.label}
                </button>
              ))}
            </div>
          </div>
        );

      case AgentResponseType.PLAN_CREATED:
        return (
          <div className="space-y-3">
            <div className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-4">
              <p className="whitespace-pre-line text-green-800 dark:text-green-200">{message.content}</p>
            </div>
            
            {message.actions && (
              <div className="flex flex-wrap gap-2 mt-3">
                {message.actions.map((action) => (
                  <button
                    key={action.value}
                    onClick={() => handleActionClick(action, message.data)}
                    className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm ${
                      action.type === 'primary'
                        ? 'bg-primary text-white hover:bg-primary/90'
                        : 'bg-slate-100 dark:bg-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-200'
                    }`}
                  >
                    {action.label}
                  </button>
                ))}
              </div>
            )}
          </div>
        );

      default:
        return (
          <div className="space-y-3">
            <p className="whitespace-pre-line">{message.content}</p>
            
            {message.actions && message.actions.length > 0 && (
              <div className="flex flex-wrap gap-2 mt-3">
                {message.actions.map((action) => (
                  <button
                    key={action.value}
                    onClick={() => handleActionClick(action, message.data)}
                    className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors text-sm ${
                      action.type === 'primary'
                        ? 'bg-primary text-white hover:bg-primary/90 shadow-sm'
                        : action.type === 'danger'
                        ? 'bg-red-50 text-red-600 hover:bg-red-100 border border-red-200'
                        : 'bg-slate-100 dark:bg-slate-700 text-slate-700 dark:text-slate-300 hover:bg-slate-200'
                    }`}
                  >
                    {action.type === 'primary' && <CheckCircle size={14} />}
                    {action.label}
                  </button>
                ))}
              </div>
            )}
          </div>
        );
    }
  };

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* Chat Window */}
      <motion.div 
        initial={false}
        animate={{
          scale: isOpen ? 1 : 0.5,
          opacity: isOpen ? 1 : 0,
          y: isOpen ? 0 : 32,
        }}
        transition={{ duration: 0.3, ease: [0.4, 0, 0.2, 1] }}
        className={`absolute bottom-0 right-0 w-80 sm:w-96 bg-white dark:bg-surface-dark rounded-2xl shadow-2xl border border-border-light dark:border-border-dark overflow-hidden flex flex-col origin-bottom-right ${
          isOpen ? 'pointer-events-auto' : 'pointer-events-none'
        }`}
        style={{ height: '500px', maxHeight: 'calc(100vh - 120px)' }}
      >
        {!selectedAgent ? (
          // Assistant Selection
          <div className="flex flex-col h-full">
            <div className="p-4 bg-primary text-white flex justify-between items-center">
              <h3 className="font-bold text-lg">选择 AI 助理</h3>
              <button onClick={handleClose} className="text-white/80 hover:text-white transition-colors">
                <X size={20} />
              </button>
            </div>
            <div className="p-4 flex-1 overflow-y-auto bg-slate-50 dark:bg-[#0B1120] custom-scrollbar">
              <p className="text-sm text-slate-500 dark:text-slate-400 mb-4">请选择您需要咨询的专业领域助理：</p>
              <div className="space-y-3">
                {assistants.map(assistant => {
                  const IconComponent = assistant.icon;
                  return (
                    <button
                      key={assistant.id}
                      onClick={() => selectAssistant(assistant.id)}
                      className="w-full bg-white dark:bg-slate-800 p-4 rounded-xl border border-border-light dark:border-border-dark hover:border-primary dark:hover:border-primary hover:shadow-md transition-all flex items-center gap-4 group text-left"
                    >
                      <div className={`w-12 h-12 rounded-full ${assistant.color} flex items-center justify-center text-white flex-shrink-0 group-hover:scale-110 transition-transform`}>
                        <IconComponent size={24} />
                      </div>
                      <div>
                        <h4 className="font-bold text-slate-900 dark:text-white group-hover:text-primary transition-colors">{assistant.name}</h4>
                        <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">{assistant.desc}</p>
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>
          </div>
        ) : (
          // Chat Interface
          <div className="flex flex-col h-full">
            <div className="p-4 bg-primary text-white flex justify-between items-center shadow-md z-10">
              <div className="flex items-center gap-3">
                <button onClick={goBackToSelection} className="text-white/80 hover:text-white transition-colors flex items-center">
                  <ArrowLeft size={20} />
                </button>
                <div className="flex items-center gap-2">
                  <div className={`w-8 h-8 rounded-full ${assistants.find(a => a.id === selectedAgentId)?.color} flex items-center justify-center text-white border-2 border-white/20`}>
                    {(() => {
                      const assistant = assistants.find(a => a.id === selectedAgentId);
                      const IconComponent = assistant?.icon || Bot;
                      return <IconComponent size={16} />;
                    })()}
                  </div>
                  <div>
                    <h3 className="font-bold">{selectedAgent}</h3>
                    <div className="flex items-center gap-1">
                      <span className="relative flex h-1.5 w-1.5">
                        <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                        <span className="relative inline-flex rounded-full h-1.5 w-1.5 bg-emerald-500"></span>
                      </span>
                      <span className="text-[10px] text-emerald-300">在线</span>
                    </div>
                  </div>
                </div>
              </div>
              <button onClick={handleClose} className="text-white/80 hover:text-white transition-colors">
                <X size={20} />
              </button>
            </div>
            
            <div ref={messagesContainerRef} className="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50 dark:bg-[#0B1120] custom-scrollbar">
              {/* 加载更多触发器 */}
              {pagination.hasMore && (
                <div
                  ref={loadMoreTriggerRef}
                  className="flex justify-center py-2"
                >
                  {isLoadingMore ? (
                    <div className="flex items-center gap-2 text-slate-400 text-sm">
                      <Loader2 size={14} className="animate-spin" />
                      <span>加载中...</span>
                    </div>
                  ) : (
                    <button
                      onClick={loadMoreMessages}
                      className="text-xs text-slate-400 hover:text-slate-600 dark:hover:text-slate-300 transition-colors"
                    >
                      点击加载更多历史消息
                    </button>
                  )}
                </div>
              )}

              <AnimatePresence mode="popLayout">
                {messages.map((message) => (
                  <motion.div
                    key={message.id}
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    className={`flex items-end gap-2 ${
                      message.role === 'user' ? 'justify-end' : 'justify-start'
                    }`}
                  >
                    {message.role === 'ai' && (
                      <div className="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center border border-primary/30 text-primary shrink-0">
                        <Bot size={16} />
                      </div>
                    )}
                    
                    <div className={`flex flex-col gap-1 max-w-[80%] ${
                      message.role === 'user' ? 'items-end' : 'items-start'
                    }`}>
                      <div className={`px-4 py-3 rounded-2xl text-sm shadow-sm ${
                        message.role === 'user'
                          ? 'bg-primary text-white rounded-tr-sm'
                          : 'bg-white dark:bg-slate-800 text-slate-900 dark:text-white rounded-tl-sm border border-slate-200 dark:border-transparent'
                      }`}>
                        {renderMessageContent(message)}
                      </div>
                      <span className="text-[10px] text-slate-400">
                        {message.timestamp.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}
                      </span>
                    </div>

                    {message.role === 'user' && (
                      <div className="w-8 h-8 rounded-full bg-slate-300 shrink-0 overflow-hidden">
                        <img 
                          src="https://picsum.photos/seed/user/100/100" 
                          alt="Avatar" 
                          className="w-full h-full object-cover" 
                        />
                      </div>
                    )}
                  </motion.div>
                ))}
              </AnimatePresence>

              {isLoading && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="flex items-center gap-2 text-slate-400"
                >
                  <Loader2 size={16} className="animate-spin" />
                  <span className="text-sm">AI 思考中...</span>
                </motion.div>
              )}

              <div ref={messagesEndRef} />
            </div>
            
            <div className="p-3 bg-white dark:bg-surface-dark border-t border-border-light dark:border-border-dark">
              {/* 快捷建议按钮 */}
              <div className="mb-3 flex flex-wrap gap-2">
                {getQuickSuggestions(selectedAgentId).map((suggestion, index) => (
                  <button
                    key={index}
                    onClick={() => handleSendMessage(suggestion.text)}
                    disabled={isLoading}
                    className="px-2.5 py-1 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-600 dark:text-slate-400 text-xs rounded-full transition-colors"
                  >
                    {suggestion.label}
                  </button>
                ))}
              </div>

              <div className="flex items-center gap-2 bg-slate-100 dark:bg-slate-800 rounded-full p-1 pr-2">
                <button className="flex items-center justify-center w-8 h-8 rounded-full text-slate-400 hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">
                  <Plus size={18} />
                </button>
                <input 
                  type="text" 
                  value={inputMessage}
                  onChange={(e) => setInputMessage(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                  placeholder="输入您的问题..." 
                  disabled={isLoading}
                  className="flex-1 bg-transparent border-none focus:ring-0 text-sm px-2 py-2 text-slate-900 dark:text-white placeholder-slate-500 outline-none"
                />
                <button 
                  onClick={() => handleSendMessage()}
                  disabled={!inputMessage.trim() || isLoading}
                  className="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
                >
                  <Send size={14} />
                </button>
              </div>
            </div>
          </div>
        )}
      </motion.div>

      {/* Floating Button */}
      <motion.button 
        onClick={handleOpen}
        initial={false}
        animate={{
          scale: isOpen ? 0.5 : 1,
          opacity: isOpen ? 0 : 1,
        }}
        transition={{ duration: 0.3, ease: [0.4, 0, 0.2, 1] }}
        className={`absolute bottom-0 right-0 group flex items-center gap-3 bg-white dark:bg-surface-dark p-3 rounded-2xl shadow-lg border border-border-light dark:border-border-dark hover:border-primary dark:hover:border-primary hover:shadow-xl transition-all duration-300 ease-out origin-bottom-right ${
          isOpen ? 'pointer-events-none' : 'hover:-translate-y-1'
        }`}
      >
        <div className="w-12 h-12 rounded-xl bg-[#0ea5e9] flex items-center justify-center text-white overflow-hidden shadow-inner relative flex-shrink-0">
          <svg viewBox="0 0 100 100" className="w-full h-full text-white fill-current">
            <polygon points="12,18 88,35 88,62 12,55" />
            <polygon points="31,56 35,56 35,80 31,80" />
            <polygon points="74,60 77,60 76,80 73,80" />
            <path d="M 8,81 Q 50,79 92,81 Z" />
          </svg>
        </div>
        <div className="flex flex-col justify-center min-w-[80px]">
          <div className="flex justify-center gap-1.5 w-full font-bold text-lg leading-none text-slate-700 dark:text-slate-200 group-hover:text-primary transition-colors">
            <span>投</span>
            <span>小</span>
            <span>智</span>
          </div>
          <span className="text-[10px] text-slate-400 font-medium mt-1.5 whitespace-nowrap text-center">永达传媒AI员工</span>
        </div>
      </motion.button>
    </div>
  );
}
