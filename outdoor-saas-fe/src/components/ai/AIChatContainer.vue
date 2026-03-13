<template>
  <div :class="['h-full flex', className]">
    <!-- 左侧历史会话侧边栏 -->
    <div 
      :class="[
        'transition-all duration-300 border-r border-border-light dark:border-border-dark bg-surface-light dark:bg-surface-dark',
        showSidebar ? 'w-80' : 'w-0 overflow-hidden'
      ]"
    >
      <ConversationHistoryList
        v-if="showSidebar"
        :mode="streamingState.currentMode.value"
        :conversations="historyState.conversations.value"
        :current-conversation="historyState.currentConversation.value"
        :is-loading="historyState.isLoading.value"
        :is-loading-more="historyState.isLoadingMore.value"
        :has-more="historyState.hasMoreConversations.value"
        @select="handleSelectConversation"
        @create="handleCreateConversation"
        @load-more="historyState.loadMoreConversations"
      />
    </div>

    <!-- 主聊天区域 -->
    <div class="flex-1 flex flex-col bg-background-light dark:bg-background-dark min-w-0">
      <!-- Header -->
      <AIChatHeader
        v-if="showHeader"
        :title="title"
        :subtitle="subtitle"
        :current-mode="streamingState.currentMode.value"
        :disabled="streamingState.isStreaming.value"
        @clear="streamingState.clearMessages"
        @change-mode="handleModeChange"
      >
        <template #actions>
          <!-- 历史会话切换按钮 -->
          <button
            @click="showSidebar = !showSidebar"
            :class="[
              'p-2 rounded-lg transition-colors',
              showSidebar 
                ? 'bg-blue-100 dark:bg-blue-900/30 text-blue-600' 
                : 'hover:bg-gray-100 dark:hover:bg-gray-800 text-text-light dark:text-text-dark'
            ]"
            :title="showSidebar ? '隐藏历史' : '显示历史'"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M3 3v18h18"/>
              <path d="M15 9l-5 5-5-5"/>
            </svg>
          </button>
        </template>
      </AIChatHeader>

      <!-- 模式切换提示 -->
      <div
        v-if="showModeChangeTip"
        class="px-6 py-2 bg-blue-50 dark:bg-blue-900/20 border-b border-border-light dark:border-border-dark"
      >
        <p class="text-sm text-blue-600 dark:text-blue-400 text-center">
          已切换到 {{ streamingState.currentMode.value === 'DIFY' ? 'Dify' : '智能体' }} 模式
          <button
            @click="showModeChangeTip = false"
            class="ml-2 text-blue-400 hover:text-blue-600"
          >
            ×
          </button>
        </p>
      </div>

      <!-- 当前会话信息 -->
      <div 
        v-if="historyState.currentConversation.value"
        class="px-4 py-2 bg-gray-50 dark:bg-gray-800/50 border-b border-border-light dark:border-border-dark flex items-center justify-between"
      >
        <div class="flex items-center gap-2 min-w-0">
          <span class="text-sm font-medium text-text-light dark:text-text-dark truncate">
            {{ historyState.currentConversation.value.title || '新对话' }}
          </span>
          <span class="text-xs px-2 py-0.5 rounded-full bg-blue-100 dark:bg-blue-900/30 text-blue-600">
            {{ historyState.currentConversation.value.mode === 'DIFY' ? 'Dify' : '智能体' }}
          </span>
        </div>
        <div class="flex items-center gap-1">
          <button
            @click="startRenameConversation"
            class="p-1.5 hover:bg-gray-200 dark:hover:bg-gray-700 rounded text-gray-500"
            title="重命名"
          >
            ✏️
          </button>
          <button
            @click="confirmDeleteConversation"
            class="p-1.5 hover:bg-red-100 dark:hover:bg-red-900/30 rounded text-red-500"
            title="删除"
          >
            🗑️
          </button>
        </div>
      </div>

      <!-- Quick Commands -->
      <AIQuickCommands
        v-if="!hasMessages && !historyState.currentConversation.value"
        :commands="quickCommands"
        @select="handleQuickCommand"
        :disabled="streamingState.isStreaming.value"
      />

      <!-- Message List -->
      <div 
        ref="messageContainerRef"
        class="flex-1 overflow-y-auto px-6 py-6"
        @scroll="handleMessageScroll"
      >
        <!-- 加载更多历史消息 -->
        <div v-if="historyState.isLoadingMore.value" class="text-center py-4">
          <div class="inline-block animate-spin rounded-full h-5 w-5 border-b-2 border-blue-500"></div>
          <span class="ml-2 text-sm text-gray-500">加载历史消息...</span>
        </div>
        
        <div v-if="!historyState.hasMoreMessages.value && messages.length > 0" 
             class="text-center py-4 text-gray-400 text-sm">
          ── 以上是历史消息 ──
        </div>

        <AIChatMessageList
          :messages="messages"
          :current-tool="streamingState.currentTool.value"
          @action="handleAction"
        >
          <template #empty>
            <div class="flex flex-col items-center justify-center h-full text-center">
              <div class="w-20 h-20 mb-6 bg-gradient-to-br from-blue-500/20 to-purple-600/20 rounded-full flex items-center justify-center">
                <span class="text-4xl font-bold text-transparent bg-clip-text bg-gradient-to-br from-blue-500 to-purple-600">智</span>
              </div>
              <h2 class="text-xl font-medium text-text-light dark:text-text-dark mb-3">
                你好，我是投小智
              </h2>
              <p class="text-sm text-subtext-light dark:text-subtext-dark max-w-md">
                您的专属AI员工，可以帮您查询和管理社区、道闸、框架、方案等信息。
                <br />
                点击上方快捷指令或输入您想了解的内容。
              </p>
            </div>
          </template>
        </AIChatMessageList>
        <div ref="messagesEndRef" />
      </div>

      <!-- Suggested Topics -->
      <SuggestedTopics
        v-if="lastMessage && !streamingState.isStreaming.value"
        :suggestions="suggestedTopics"
        @select="handleSuggestion"
      />

      <!-- Input -->
      <AIChatInput
        v-model="input"
        @send="handleSend"
        @stop="streamingState.stopStreaming"
        :is-streaming="streamingState.isStreaming.value"
        :placeholder="placeholder"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAiStreaming } from '@/src/hooks/useAiStreaming';
import { useConversationHistory } from '@/src/composables/useConversationHistory';
import type { NavigationAction, QuickCommand } from '@/src/components/ai/types';
import type { ActionButton, ChatMessage } from '@/src/hooks/useAiStreaming.types';
import type { AiMode, Conversation } from '@/src/types/aiAssistant';
import AIChatHeader from './AIChatHeader.vue';
import AIChatMessageList from './AIChatMessageList.vue';
import AIChatInput from './AIChatInput.vue';
import AIQuickCommands from './AIQuickCommands.vue';
import SuggestedTopics from './SuggestedTopics.vue';
import ConversationHistoryList from './ConversationHistoryList.vue';

const showToast = (type: string, message: string, duration: number = 3000) => {
  const toast = document.createElement('div');
  toast.className = `fixed top-4 right-4 px-4 py-2 rounded-lg shadow-lg z-50 transition-opacity duration-300 ${
    type === 'success' ? 'bg-green-500 text-white' :
    type === 'error' ? 'bg-red-500 text-white' :
    type === 'warning' ? 'bg-yellow-500 text-white' :
    'bg-blue-500 text-white'
  }`;
  toast.textContent = message;
  document.body.appendChild(toast);
  
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => document.body.removeChild(toast), 300);
  }, duration);
};

interface Props {
  title?: string;
  subtitle?: string;
  quickCommands?: QuickCommand[];
  placeholder?: string;
  className?: string;
  showHeader?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  title: '投小智',
  subtitle: '你的AI员工',
  placeholder: '输入指令，例如：查询所有社区...',
  className: '',
  showHeader: true,
  quickCommands: () => [
    { label: '查今天跟进', command: '今天有哪些客户需要跟进', icon: '📞' },
    { label: '查本月业绩', command: '查看本月业绩完成情况', icon: '💰' },
    { label: '查逾期应收', command: '查看有哪些逾期未收款', icon: '⚠️' },
    { label: '新建方案', command: '帮我新建一个投放方案', icon: '📝' },
    { label: '查空位资源', command: '查询杭州5月份有哪些空位', icon: '📍' },
    { label: '查上刊进度', command: '查看在投方案的上刊进度', icon: '📊' },
    { label: '安排上刊任务', command: '安排明天的上刊任务', icon: '🚚' },
    { label: '报修登记', command: '我要登记一个点位故障', icon: '🔧' },
  ],
});

const router = useRouter();
const input = ref('');
const messagesEndRef = ref<HTMLDivElement>();
const messageContainerRef = ref<HTMLDivElement>();
const showSidebar = ref(true);
const showModeChangeTip = ref(false);

// 当前AI模式（用于historyState和streamingState共享）
const currentMode = ref<AiMode>('DIFY');

// AI Streaming hook（必须先定义）
const streamingState = useAiStreaming({
  onNavigation: handleNavigation,
  onError: (error: string) => showToast('error', error),
});

// 同步 streamingState 的模式到 currentMode
watch(() => streamingState.currentMode.value, (newMode) => {
  currentMode.value = newMode;
}, { immediate: true });

// 历史会话管理（使用 currentMode）
const historyState = useConversationHistory(currentMode);

// 组合消息：历史消息 + 当前流消息
const messages = computed((): ChatMessage[] => {
  // 转换历史消息格式
  const historyMsgs: ChatMessage[] = historyState.messages.value.map(msg => ({
    id: String(msg.id),
    role: msg.role,
    content: msg.content,
    thinking: msg.thinking,
    isStreaming: false,
  }));
  
  // 合并当前流消息
  return [...historyMsgs, ...streamingState.messages.value];
});

const hasMessages = computed(() => messages.value.length > 0);

// 获取最后一条助手消息
const lastMessage = computed(() => {
  const msgs = messages.value;
  for (let i = msgs.length - 1; i >= 0; i--) {
    if (msgs[i].role === 'assistant' && !msgs[i].isStreaming) {
      return msgs[i];
    }
  }
  return null;
});

// 建议话题
const suggestedTopics = computed(() => {
  const topics: string[] = [];
  const msg = lastMessage.value;
  if (!msg) return topics;
  
  const content = msg.content || '';
  
  if (content.includes('方案')) topics.push('查看方案详情', '复制此方案', '锁定点位避免被抢');
  if (content.includes('客户')) topics.push('查看客户联系方式', '记录本次沟通', '查看客户历史方案');
  if (content.includes('资源') || content.includes('点位') || content.includes('社区') || content.includes('空位')) {
    topics.push('这些资源的价格是多少', '帮我筛选高端社区', '查这些点位的档期');
  }
  if (content.includes('上刊') || content.includes('执行') || content.includes('任务')) {
    topics.push('查看上刊完成率', '安排工程人员', '查看上刊照片');
  }
  if (content.includes('款') || content.includes('钱') || content.includes('收') || content.includes('财务')) {
    topics.push('查看应收款明细', '生成对账单', '查看客户回款记录');
  }
  if (content.includes('故障') || content.includes('坏') || content.includes('维修') || content.includes('报修')) {
    topics.push('查看维修进度', '联系维修人员', '评估对客户的影响');
  }
  if (content.includes('业绩') || content.includes('销售') || content.includes('完成率')) {
    topics.push('查看本月回款情况', '查看在跟商机', '查看团队排名');
  }
  
  if (topics.length === 0) {
    topics.push('我今天有哪些跟进任务', '本月业绩完成情况', '有哪些逾期未收款', '查询杭州空位资源');
  }
  
  return topics.slice(0, 4);
});

// Navigation handler
const validRoutes = ['/', '/plans', '/plan', '/communities', '/community', '/frames', '/frame', '/barrier-gates', '/barrier-gate', '/plan-communities', '/plan-frames', '/plan-barriers', '/ai-assistant'];

function handleNavigation(nav: NavigationAction) {
  if (nav.message) showToast('info', nav.message);
  
  if (nav.action === 'navigate' && nav.target) {
    if (nav.target.startsWith('http') || nav.target.startsWith('//')) {
      showToast('error', '不支持跳转到外部链接');
      return;
    }
    
    const isValidRoute = validRoutes.some(route => nav.target.startsWith(route));
    if (!isValidRoute) {
      showToast('error', '无效的页面跳转');
      return;
    }
    
    const routeLocation: { path: string; query?: Record<string, string> } = { path: nav.target };
    if (nav.params && Object.keys(nav.params).length > 0) {
      routeLocation.query = {};
      for (const [key, value] of Object.entries(nav.params)) {
        if (value !== null && value !== undefined) routeLocation.query[key] = String(value);
      }
    }
    
    router.push(routeLocation);
  }
  
  if (nav.toast) showToast(nav.toast.type, nav.toast.message, nav.toast.duration);
}

// 处理模式切换
const handleModeChange = async (mode: AiMode) => {
  if (streamingState.isStreaming.value) {
    showToast('warning', '对话进行中，请稍后再切换模式');
    return;
  }

  streamingState.setMode(mode);
  showModeChangeTip.value = true;
  setTimeout(() => showModeChangeTip.value = false, 3000);
  
  // 清空当前消息
  streamingState.clearMessages();
};

// 选择历史会话
const handleSelectConversation = async (conversation: Conversation) => {
  if (streamingState.isStreaming.value) {
    showToast('warning', '对话进行中，请稍后再切换');
    return;
  }
  
  await historyState.switchConversation(conversation);
  
  // 如果模式不匹配，自动切换
  if (streamingState.currentMode.value !== conversation.mode) {
    streamingState.setMode(conversation.mode);
  }
};

// 创建新会话
const handleCreateConversation = async () => {
  if (streamingState.isStreaming.value) {
    showToast('warning', '对话进行中，请稍后再创建');
    return;
  }
  
  try {
    await historyState.createNewConversation();
    streamingState.clearMessages();
    showToast('success', '新会话已创建');
  } catch (error) {
    showToast('error', '创建会话失败');
  }
};

// 重命名会话
const startRenameConversation = () => {
  const conv = historyState.currentConversation.value;
  if (!conv) return;
  
  const newTitle = prompt('请输入新标题:', conv.title || '新对话');
  if (newTitle && newTitle.trim()) {
    historyState.updateTitle(conv.conversationId, newTitle.trim());
  }
};

// 删除会话确认
const confirmDeleteConversation = () => {
  const conv = historyState.currentConversation.value;
  if (!conv) return;
  
  if (confirm('确定要删除这个会话吗？此操作不可恢复。')) {
    historyState.deleteConversation(conv.conversationId);
    streamingState.clearMessages();
  }
};

// 消息区域滚动加载更多
const handleMessageScroll = () => {
  if (!messageContainerRef.value) return;
  const { scrollTop } = messageContainerRef.value;
  if (scrollTop < 50 && !historyState.isLoadingMore.value) {
    historyState.loadMoreMessages();
  }
};

// 发送消息
const handleSend = () => {
  if (!input.value.trim() || streamingState.isStreaming.value) return;
  
  if (!historyState.currentConversation.value) {
    showToast('error', '请先选择或创建一个会话');
    return;
  }
  
  streamingState.sendMessage(input.value, historyState.currentConversation.value.conversationId);
  input.value = '';
};

// 快速指令
const handleQuickCommand = (command: string) => {
  if (streamingState.isStreaming.value) return;
  streamingState.sendMessage(command);
};

// 建议话题
const handleSuggestion = (suggestion: string) => {
  streamingState.sendMessage(suggestion);
};

// 处理按钮点击
const handleAction = (action: ActionButton) => {
  switch (action.action) {
    case 'navigate':
      if (action.payload?.target) router.push(action.payload.target);
      break;
    case 'confirm':
      if (action.payload?.message) streamingState.sendMessage(action.payload.message);
      break;
    case 'create':
    case 'update':
    case 'delete':
      showToast('success', action.payload?.successMessage || '操作成功');
      break;
  }
};

// 监听消息变化，自动滚动到底部
watch(messages, () => {
  nextTick(() => {
    messagesEndRef.value?.scrollIntoView({ behavior: 'smooth' });
  });
}, { deep: true });

// 初始化
onMounted(async () => {
  // 加载当前模式的会话列表
  await historyState.loadConversations(true);
  
  // 如果有会话，自动切换到最新的
  if (historyState.conversations.value.length > 0) {
    await historyState.switchConversation(historyState.conversations.value[0]);
  } else {
    // 没有会话则创建新会话
    await historyState.createNewConversation();
  }
});
</script>
