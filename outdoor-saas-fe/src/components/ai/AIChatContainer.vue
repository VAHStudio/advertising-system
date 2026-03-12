<template>
  <div :class="['h-full flex flex-col bg-background-light dark:bg-background-dark', className]">
    <!-- Header -->
    <AIChatHeader
      v-if="showHeader"
      :title="title"
      :subtitle="subtitle"
      @clear="streamingState.clearMessages"
      :disabled="streamingState.isStreaming.value"
    />

    <!-- Quick Commands -->
    <AIQuickCommands
      v-if="!hasMessages"
      :commands="quickCommands"
      @select="handleQuickCommand"
      :disabled="streamingState.isStreaming.value"
    />

    <!-- Message List -->
    <div class="flex-1 overflow-y-auto px-6 py-6">
      <AIChatMessageList
        :messages="streamingState.messages.value"
        :current-tool="streamingState.currentTool.value"
      >
        <template #empty>
          <div class="flex flex-col items-center justify-center h-full text-center">
            <div class="w-20 h-20 mb-6 bg-primary/10 rounded-full flex items-center justify-center">
              <span class="text-4xl">🤖</span>
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

    <!-- Input -->
    <AIChatInput
      v-model="input"
      @send="handleSend"
      @stop="streamingState.stopStreaming"
      :is-streaming="streamingState.isStreaming.value"
      :placeholder="placeholder"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { useAiStreaming } from '@/src/hooks/useAiStreaming';
import type { NavigationAction, QuickCommand } from '@/src/components/ai/types';
import AIChatHeader from './AIChatHeader.vue';
import AIChatMessageList from './AIChatMessageList.vue';
import AIChatInput from './AIChatInput.vue';
import AIQuickCommands from './AIQuickCommands.vue';

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
    { label: '查询所有社区', command: '查询所有社区', icon: '🏢' },
    { label: '查询所有方案', command: '查询所有方案', icon: '📋' },
    { label: '查询所有道闸', command: '查询所有道闸', icon: '🚧' },
    { label: '查询所有框架', command: '查询所有框架', icon: '🖼️' },
    { label: '创建一个社区', command: '帮我创建一个社区', icon: '➕' },
    { label: '创建一个方案', command: '帮我创建一个方案', icon: '➕' },
  ],
});

const router = useRouter();
const input = ref('');
const messagesEndRef = ref<HTMLDivElement>();

// Navigation handler
const handleNavigation = (nav: NavigationAction) => {
  if (nav.message) {
    showToast('info', nav.message);
  }
  if (nav.action === 'navigate' && nav.target) {
    router.push(nav.target);
  }
  if (nav.toast) {
    showToast(nav.toast.type, nav.toast.message, nav.toast.duration);
  }
};

// AI Streaming hook
const streamingState = useAiStreaming({
  onNavigation: handleNavigation,
  onError: (error: string) => showToast('error', error),
});

// Debug: 监视 messages 变化
watch(streamingState.messages, () => {
  console.log('[AIChatContainer] messages changed:', streamingState.messages.value.length, 'items');
  streamingState.messages.value.forEach((m, i) => {
    console.log(`  [${i}] ${m.role}: "${m.content}" (len=${m.content?.length || 0})`);
  });
  nextTick(() => {
    messagesEndRef.value?.scrollIntoView({ behavior: 'smooth' });
  });
}, { deep: true });

const hasMessages = computed(() => streamingState.messages.value.length > 0);

// Send message handler
const handleSend = () => {
  if (!input.value.trim() || streamingState.isStreaming.value) return;
  streamingState.sendMessage(input.value);
  input.value = '';
};

// Quick command handler
const handleQuickCommand = (command: string) => {
  if (streamingState.isStreaming.value) return;
  streamingState.sendMessage(command);
};
</script>
