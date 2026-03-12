<template>
  <!-- AI助手悬浮按钮 - 仅在登录状态下显示 -->
  <Teleport to="body">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 scale-75 translate-y-4"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-75"
    >
      <button
        v-if="shouldShowFloatButton && !isChatOpen"
        @click="openChat"
        class="fixed bottom-6 right-6 z-50 group"
        aria-label="打开投小智"
      >
        <!-- 按钮主体 -->
        <div class="relative">
          <!-- 光晕效果 -->
          <div class="absolute inset-0 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 blur-lg opacity-60 group-hover:opacity-80 transition-opacity duration-300 animate-pulse"></div>
          
          <!-- 主按钮 -->
          <div class="relative w-14 h-14 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white shadow-2xl transform group-hover:scale-110 transition-transform duration-300 border-2 border-white/20">
            <span class="text-2xl font-bold">智</span>
          </div>
          
          <!-- 通知红点 -->
          <div v-if="hasNotification" class="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full border-2 border-white dark:border-gray-900 animate-bounce"></div>
        </div>
        
        <!-- 提示文字 -->
        <div class="absolute right-full mr-3 top-1/2 -translate-y-1/2 whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none">
          <div class="bg-gray-900 dark:bg-gray-800 text-white text-sm px-3 py-1.5 rounded-lg shadow-lg">
            投小智
            <div class="absolute right-0 top-1/2 -translate-y-1/2 translate-x-1/2 w-2 h-2 bg-gray-900 dark:bg-gray-800 rotate-45"></div>
          </div>
        </div>
      </button>
    </Transition>

    <!-- AI助手对话框 -->
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="opacity-0 scale-95 translate-y-4"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95 translate-y-4"
    >
      <div
        v-if="isChatOpen"
        class="fixed bottom-24 right-6 z-50 w-[420px] h-[600px] max-h-[calc(100vh-120px)] bg-surface-light dark:bg-surface-dark rounded-2xl shadow-2xl border border-border-light dark:border-border-dark overflow-hidden flex flex-col"
      >
        <!-- 头部 -->
        <div class="flex items-center justify-between px-4 py-3 border-b border-border-light dark:border-border-dark bg-surface-light dark:bg-surface-dark">
          <div class="flex items-center gap-2">
            <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm">
              智
            </div>
            <div>
              <h3 class="text-sm font-bold text-text-light dark:text-text-dark">投小智</h3>
              <p class="text-[10px] text-subtext-light dark:text-subtext-dark">你的AI员工</p>
            </div>
          </div>
          <div class="flex items-center gap-1">
            <button
              @click="expandToFull"
              class="p-1.5 text-subtext-light hover:text-text-light dark:text-subtext-dark dark:hover:text-text-dark transition-colors rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800"
              title="展开全屏"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
              </svg>
            </button>
            <button
              @click="closeChat"
              class="p-1.5 text-subtext-light hover:text-text-light dark:text-subtext-dark dark:hover:text-text-dark transition-colors rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800"
              title="关闭"
            >
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>
        
        <!-- 聊天内容 -->
        <div class="flex-1 overflow-hidden">
          <AIChatContainer
            title="投小智"
            subtitle="你的AI员工"
            class-name="!h-full"
            :show-header="false"
          />
        </div>
      </div>
    </Transition>

    <!-- 点击外部关闭 -->
    <div
      v-if="isChatOpen"
      class="fixed inset-0 z-40"
      @click="closeChat"
    ></div>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/src/stores/auth';
import AIChatContainer from '@/src/components/ai/AIChatContainer.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isChatOpen = ref(false);
const hasNotification = ref(false);

// 判断是否在 AI 助手页面
const isOnAIAssistantPage = computed(() => {
  return route.path === '/ai-assistant';
});

// 是否显示悬浮按钮
const shouldShowFloatButton = computed(() => {
  return authStore.isAuthenticated && !isOnAIAssistantPage.value;
});

const openChat = () => {
  isChatOpen.value = true;
  hasNotification.value = false;
};

const closeChat = () => {
  isChatOpen.value = false;
};

const expandToFull = () => {
  closeChat();
  router.push('/ai-assistant');
};
</script>
