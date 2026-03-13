<template>
  <div class="h-full flex flex-col bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-700">
    <!-- 头部 -->
    <div class="p-4 border-b border-gray-200 dark:border-gray-700">
      <h2 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">
        {{ mode === 'DIFY' ? 'Dify 历史' : '智能体历史' }}
      </h2>
      <button
        @click="$emit('create')"
        class="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg flex items-center justify-center gap-2 transition-colors"
      >
        <span>+</span>
        <span>新建对话</span>
      </button>
    </div>

    <!-- 会话列表 -->
    <div ref="listRef" class="flex-1 overflow-y-auto p-2 space-y-1" @scroll="handleScroll">
      <div
        v-for="conv in conversations"
        :key="conv.conversationId"
        @click="$emit('select', conv)"
        :class="[
          'p-3 rounded-lg cursor-pointer transition-all',
          currentConversation?.conversationId === conv.conversationId
            ? 'bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800'
            : 'hover:bg-gray-50 dark:hover:bg-gray-800 border border-transparent',
        ]"
      >
        <div class="flex items-center gap-2 mb-1">
          <span class="text-sm">💬</span>
          <h3 class="font-medium text-gray-900 dark:text-white text-sm truncate flex-1">
            {{ conv.title || '新对话' }}
          </h3>
        </div>
        <p class="text-xs text-gray-500 dark:text-gray-400 truncate mb-1">
          {{ conv.lastMessagePreview || '暂无消息' }}
        </p>
        <div class="flex items-center justify-between text-xs text-gray-400">
          <span>{{ conv.messageCount || 0 }} 条</span>
          <span>{{ formatTime(conv.lastMessageAt) }}</span>
        </div>
      </div>

      <!-- 加载更多 -->
      <div v-if="isLoadingMore" class="text-center py-4">
        <div class="inline-block animate-spin rounded-full h-5 w-5 border-b-2 border-blue-500"></div>
      </div>

      <!-- 空状态 -->
      <div v-if="conversations.length === 0 && !isLoading" class="text-center py-8 text-gray-400">
        <p>暂无历史对话</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { Conversation, AiMode } from '@/src/types/aiAssistant';

interface Props {
  mode: AiMode;
  conversations: Conversation[];
  currentConversation: Conversation | null;
  isLoading: boolean;
  isLoadingMore: boolean;
  hasMore: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  select: [conversation: Conversation];
  create: [];
  loadMore: [];
}>();

const listRef = ref<HTMLDivElement>();

const handleScroll = () => {
  if (!listRef.value) return;
  const { scrollTop, scrollHeight, clientHeight } = listRef.value;
  // 滚动到底部附近时加载更多
  if (scrollHeight - scrollTop - clientHeight < 50 && props.hasMore && !props.isLoadingMore) {
    emit('loadMore');
  }
};

const formatTime = (time: string) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  if (diff < 60000) return '刚刚';
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`;
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`;
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`;

  return date.toLocaleDateString('zh-CN');
};
</script>
