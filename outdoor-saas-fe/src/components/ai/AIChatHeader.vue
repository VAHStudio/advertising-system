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
    </div>
    <button
      v-if="!disabled"
      @click="$emit('clear')"
      class="p-2 text-subtext-light hover:text-text-light dark:text-subtext-dark dark:hover:text-text-dark transition-colors"
      title="清空对话"
    >
      <Icon name="refresh" :size="18" />
    </button>
  </div>
</template>

<script setup lang="ts">
import Icon from '@/src/components/Icon.vue';

interface Props {
  title: string;
  subtitle: string;
  disabled?: boolean;
}

defineProps<Props>();
defineEmits(['clear']);
</script>
