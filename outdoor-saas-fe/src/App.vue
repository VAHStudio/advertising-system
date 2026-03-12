<template>
  <div class="min-h-screen bg-background-light dark:bg-background-dark transition-colors duration-200">
    <router-view />
    <!-- 投小智悬浮按钮 - 全局显示 -->
    <AIAssistantFloatButton />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useAuthStore } from '@/src/stores/auth';
import AIAssistantFloatButton from '@/src/components/ai/AIAssistantFloatButton.vue';

const authStore = useAuthStore();

onMounted(() => {
  authStore.initAuth();
});
</script>
