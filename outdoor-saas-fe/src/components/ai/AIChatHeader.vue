<template>
  <div class="flex items-center justify-between px-6 py-4 border-b border-border-light dark:border-border-dark bg-surface-light dark:bg-surface-dark">
    <div class="flex items-center gap-3">
      <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-lg shadow-lg">
        智
      </div>
      <div>
        <h2 class="text-lg font-bold text-text-light dark:text-text-dark">{{ title }}</h2>
        <p class="text-xs text-subtext-light dark:text-subtext-dark">{{ subtitle }}</p>
      </div>
      
      <!-- AI模式切换 -->
      <div class="flex items-center gap-2 ml-4">
        <span class="text-xs text-subtext-light dark:text-subtext-dark">AI模式:</span>
        <div class="flex bg-background-light dark:bg-background-dark rounded-lg p-1">
          <button
            @click="$emit('changeMode', 'DIFY')"
            :class="[
              'px-3 py-1 text-xs rounded-md transition-colors',
              currentMode === 'DIFY'
                ? 'bg-blue-500 text-white'
                : 'text-text-light dark:text-text-dark hover:bg-surface-light dark:hover:bg-surface-dark'
            ]"
            :disabled="disabled"
            title="使用Dify平台"
          >
            Dify
          </button>
          <button
            @click="$emit('changeMode', 'CUSTOM')"
            :class="[
              'px-3 py-1 text-xs rounded-md transition-colors',
              currentMode === 'CUSTOM'
                ? 'bg-green-500 text-white'
                : 'text-text-light dark:text-text-dark hover:bg-surface-light dark:hover:bg-surface-dark'
            ]"
            :disabled="disabled"
            title="使用本地智能体"
          >
            智能体
          </button>
        </div>
        
        <!-- 模式说明Tooltip -->
        <div class="relative group">
          <button 
            class="p-1 text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark"
            title="模式说明"
          >
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </button>
          <div class="absolute left-0 top-full mt-2 w-64 p-3 bg-surface-light dark:bg-surface-dark rounded-lg shadow-lg opacity-0 group-hover:opacity-100 transition-opacity z-10 pointer-events-none">
            <p class="text-xs text-text-light dark:text-text-dark mb-2">
              <strong>Dify模式:</strong> 使用Dify平台，适合标准对话流程
            </p>
            <p class="text-xs text-text-light dark:text-text-dark">
              <strong>智能体模式:</strong> 使用本地Agent，支持复杂工具调用和深度业务集成
            </p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="flex items-center gap-2">
      <button
        v-if="!disabled"
        @click="$emit('clear')"
        class="p-2 text-subtext-light hover:text-text-light dark:text-subtext-dark dark:hover:text-text-dark transition-colors"
        title="清空对话"
      >
        <Icon name="refresh" :size="18" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AiMode } from '@/src/types/aiAssistant';
import Icon from '@/src/components/Icon.vue';

interface Props {
  title: string;
  subtitle: string;
  currentMode?: AiMode;
  disabled?: boolean;
}

withDefaults(defineProps<Props>(), {
  currentMode: 'DIFY'
});

defineEmits<{
  clear: [];
  changeMode: [mode: AiMode];
}>();
</script>
