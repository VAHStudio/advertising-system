import { ref, computed, watch, onMounted } from 'vue';
import type { Ref } from 'vue';
import type { Conversation, ConversationMessage, AiMode } from '@/src/types/aiAssistant';
import {
  getConversations,
  createConversation,
  getConversationMessages,
  updateConversationTitle,
  deleteConversation,
} from '@/src/services/aiAssistantService';

const PAGE_SIZE = 10;

export function useConversationHistory(mode: Ref<AiMode>) {
  // 状态
  const conversations = ref<Conversation[]>([]);
  const currentConversation = ref<Conversation | null>(null);
  const messages = ref<ConversationMessage[]>([]);
  const isLoading = ref(false);
  const isLoadingMore = ref(false);
  const hasMoreConversations = ref(true);
  const hasMoreMessages = ref(true);

  // 分页状态
  const conversationPage = ref(0);
  const messagePage = ref(0);

  // 计算属性
  const sortedConversations = computed(() => {
    return [...conversations.value].sort(
      (a, b) => new Date(b.lastMessageAt).getTime() - new Date(a.lastMessageAt).getTime()
    );
  });

  // 加载会话列表
  const loadConversations = async (reset = false) => {
    if (reset) {
      conversationPage.value = 0;
      conversations.value = [];
      hasMoreConversations.value = true;
    }

    if (!hasMoreConversations.value && !reset) return;

    isLoading.value = true;
    try {
      const { conversations: newConvs, hasMore } = await getConversations(
        mode.value,
        conversationPage.value
      );

      if (reset) {
        conversations.value = newConvs;
      } else {
        conversations.value.push(...newConvs);
      }

      hasMoreConversations.value = hasMore;

      if (hasMore) {
        conversationPage.value++;
      }
    } catch (error) {
      console.error('Failed to load conversations:', error);
    } finally {
      isLoading.value = false;
    }
  };

  // 加载更多会话
  const loadMoreConversations = async () => {
    if (isLoadingMore.value || !hasMoreConversations.value) return;
    isLoadingMore.value = true;
    await loadConversations(false);
    isLoadingMore.value = false;
  };

  // 切换会话
  const switchConversation = async (conversation: Conversation) => {
    currentConversation.value = conversation;
    messagePage.value = 0;
    messages.value = [];
    hasMoreMessages.value = true;

    await loadMessages();

    localStorage.setItem('ai_last_conversation_id', conversation.conversationId);
    localStorage.setItem('ai_last_mode', conversation.mode);
  };

  // 加载消息历史
  const loadMessages = async (reset = false) => {
    if (!currentConversation.value) return;

    if (reset) {
      messagePage.value = 0;
      messages.value = [];
      hasMoreMessages.value = true;
    }

    if (!hasMoreMessages.value && !reset) return;

    isLoading.value = reset;

    try {
      const { messages: newMsgs, hasMore } = await getConversationMessages(
        currentConversation.value.conversationId,
        messagePage.value
      );

      // prepend 到消息列表（因为分页加载历史消息）
      if (reset) {
        messages.value = newMsgs;
      } else {
        messages.value.unshift(...newMsgs);
      }

      hasMoreMessages.value = hasMore;

      if (hasMore) {
        messagePage.value++;
      }
    } catch (error) {
      console.error('Failed to load messages:', error);
    } finally {
      isLoading.value = false;
    }
  };

  // 加载更多消息
  const loadMoreMessages = async () => {
    if (isLoadingMore.value || !hasMoreMessages.value) return;
    isLoadingMore.value = true;
    await loadMessages(false);
    isLoadingMore.value = false;
  };

  // 创建新会话
  const createNewConversation = async () => {
    try {
      const { conversationId } = await createConversation(mode.value);

      const newConversation: Conversation = {
        id: Date.now(),
        conversationId,
        mode: mode.value,
        title: '新对话',
        status: 1,
        messageCount: 0,
        userId: '',
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
        lastMessageAt: new Date().toISOString(),
      };

      conversations.value.unshift(newConversation);
      await switchConversation(newConversation);

      return newConversation;
    } catch (error) {
      console.error('Failed to create conversation:', error);
      throw error;
    }
  };

  // 删除会话
  const deleteConv = async (conversationId: string) => {
    try {
      await deleteConversation(conversationId);

      const index = conversations.value.findIndex((c) => c.conversationId === conversationId);
      if (index > -1) {
        conversations.value.splice(index, 1);
      }

      if (currentConversation.value?.conversationId === conversationId) {
        if (conversations.value.length > 0) {
          await switchConversation(conversations.value[0]);
        } else {
          currentConversation.value = null;
          messages.value = [];
        }
      }
    } catch (error) {
      console.error('Failed to delete conversation:', error);
      throw error;
    }
  };

  // 更新标题
  const updateTitle = async (conversationId: string, title: string) => {
    try {
      await updateConversationTitle(conversationId, title);

      const conversation = conversations.value.find((c) => c.conversationId === conversationId);
      if (conversation) {
        conversation.title = title;
      }
      if (currentConversation.value?.conversationId === conversationId) {
        currentConversation.value.title = title;
      }
    } catch (error) {
      console.error('Failed to update title:', error);
      throw error;
    }
  };

  // 监听模式变化
  watch(mode, () => {
    loadConversations(true);
  });

  // 初始化
  onMounted(() => {
    loadConversations(true);
  });

  return {
    conversations: sortedConversations,
    currentConversation,
    messages,
    isLoading,
    isLoadingMore,
    hasMoreConversations,
    hasMoreMessages,
    loadConversations,
    loadMoreConversations,
    switchConversation,
    loadMessages,
    loadMoreMessages,
    createNewConversation,
    deleteConversation: deleteConv,
    updateTitle,
  };
}
