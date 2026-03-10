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
              : 'bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark'
          ]"
        >
          <span v-if="message.role === 'user'">👤</span>
          <span v-else>🤖</span>
        </div>

        <!-- Message Content -->
        <div
          :class="[
            'max-w-[80%] rounded-2xl px-4 py-3',
            message.role === 'user'
              ? 'bg-primary text-white'
              : 'bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark'
          ]"
        >
          <p class="text-sm whitespace-pre-wrap">{{ message.content }}</p>
          <div v-if="message.isStreaming" class="mt-2 flex gap-1">
            <span class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 0ms" />
            <span class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 150ms" />
            <span class="w-2 h-2 bg-current rounded-full animate-bounce" style="animation-delay: 300ms" />
          </div>
        </div>
      </div>

      <!-- Current Tool Indicator -->
      <div v-if="currentTool" class="flex gap-3">
        <div class="w-8 h-8 rounded-full bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark flex items-center justify-center flex-shrink-0">
          <span>🔧</span>
        </div>
        <div class="bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-2xl px-4 py-3">
          <p class="text-sm text-subtext-light dark:text-subtext-dark">
            正在{{ currentTool.name }}...
          </p>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { ChatMessage, ToolCallInfo } from '@/src/hooks/useAiStreaming.types';

interface Props {
  messages: ChatMessage[];
  currentTool?: ToolCallInfo | null;
}

defineProps<Props>();
</script>
