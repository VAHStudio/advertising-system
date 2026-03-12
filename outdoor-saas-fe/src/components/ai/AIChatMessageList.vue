<template>
  <div class="space-y-4">
    <template v-if="messages.length === 0">
      <slot name="empty" />
    </template>
    
    <template v-else>
      <div
        v-for="message in messages"
        :key="message.id"
        :class="['flex gap-3', message.role === 'user' ? 'flex-row-reverse' : '']"
      >
        <!-- Avatar -->
        <div
          :class="[
            'w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0',
            message.role === 'user'
              ? 'bg-primary text-white'
              : 'bg-gradient-to-br from-blue-500 to-purple-600 text-white'
          ]"
        >
          <span v-if="message.role === 'user'">👤</span>
          <span v-else class="text-sm font-bold">智</span>
        </div>

        <!-- Message Content -->
        <div class="max-w-[85%] flex flex-col gap-2">
          <!-- 思考过程折叠区域 - 仅对助手消息显示 -->
          <div
            v-if="message.role === 'assistant' && (message.thinking || message.thinkingTime !== undefined)"
            class="flex flex-col"
          >
            <button
              @click="toggleThinking(message.id)"
              class="flex items-center gap-2 px-3 py-2 rounded-lg bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors group"
            >
              <span class="text-xs text-gray-500 dark:text-gray-400">
                <span v-if="message.isStreaming && !message.content" class="flex items-center gap-1">
                  <span class="w-1.5 h-1.5 bg-blue-500 rounded-full animate-pulse"></span>
                  深度思考中...
                </span>
                <span v-else>
                  已深度思考({{ formatThinkingTime(message.thinkingTime || 0) }})
                </span>
              </span>
              <svg
                :class="['w-3 h-3 text-gray-400 transition-transform duration-200', isExpanded(message.id) ? 'rotate-180' : '']"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
              </svg>
            </button>
            
            <!-- 思考过程内容 -->
            <div
              v-show="isExpanded(message.id)"
              class="mt-2 px-3 py-2 rounded-lg bg-gray-50 dark:bg-gray-900/50 border border-gray-200 dark:border-gray-700"
            >
              <p class="text-xs text-gray-500 dark:text-gray-400 leading-relaxed whitespace-pre-wrap">
                {{ message.thinking || '正在思考中...' }}
              </p>
            </div>
          </div>

          <!-- 正式回复内容 -->
          <div
            v-if="message.content"
            :class="[
              'rounded-2xl px-4 py-3',
              message.role === 'user'
                ? 'bg-primary text-white'
                : 'bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark'
            ]"
          >
            <p class="text-sm whitespace-pre-wrap leading-relaxed">{{ message.content }}</p>
            <div v-if="message.isStreaming && message.content" class="mt-2 flex gap-1">
              <span class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 0ms" />
              <span class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 150ms" />
              <span class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 300ms" />
            </div>
          </div>
        </div>
      </div>

      <!-- Current Tool Indicator -->
      <div v-if="currentTool" class="flex gap-3">
        <div class="w-8 h-8 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 text-white flex items-center justify-center flex-shrink-0 text-sm font-bold">
          智
        </div>
        <div class="bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-2xl px-4 py-3">
          <p class="text-sm text-subtext-light dark:text-subtext-dark flex items-center gap-2">
            <span class="w-2 h-2 bg-blue-500 rounded-full animate-pulse"></span>
            正在{{ currentTool.name }}...
          </p>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { ChatMessage, ToolCallInfo } from '@/src/hooks/useAiStreaming.types';

interface Props {
  messages: ChatMessage[];
  currentTool?: ToolCallInfo | null;
}

defineProps<Props>();

// 思考过程展开状态管理
const expandedThinking = ref<Set<string>>(new Set());

const toggleThinking = (messageId: string) => {
  if (expandedThinking.value.has(messageId)) {
    expandedThinking.value.delete(messageId);
  } else {
    expandedThinking.value.add(messageId);
  }
};

const isExpanded = (messageId: string) => {
  return expandedThinking.value.has(messageId);
};

// 格式化思考时间
const formatThinkingTime = (seconds: number) => {
  if (seconds < 1) {
    return `${(seconds * 1000).toFixed(0)}ms`;
  }
  return `${seconds.toFixed(1)}s`;
};
</script>
