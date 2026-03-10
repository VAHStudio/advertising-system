<template>
  <div class="px-6 py-4 border-t border-border-light dark:border-border-dark bg-surface-light dark:bg-surface-dark">
    <div class="flex items-center gap-3">
      <div class="flex-1 relative">
        <input
          :value="modelValue"
          @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
          @keyup.enter="$emit('send')"
          type="text"
          :placeholder="placeholder"
          :disabled="isStreaming"
          class="w-full px-4 py-3 bg-background-light dark:bg-background-dark border border-border-light dark:border-border-dark rounded-xl focus:outline-none focus:border-primary text-text-light dark:text-text-dark placeholder-subtext-light dark:placeholder-subtext-dark"
        />
      </div>
      <button
        v-if="isStreaming"
        @click="$emit('stop')"
        class="px-4 py-3 bg-red-500 hover:bg-red-600 text-white rounded-xl transition-colors"
      >
        <Icon name="square" :size="18" />
      </button>
      <button
        v-else
        @click="$emit('send')"
        :disabled="!modelValue?.trim()"
        class="px-4 py-3 bg-primary hover:bg-primary/90 text-white rounded-xl transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <Icon name="send" :size="18" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import Icon from '@/src/components/Icon.vue';

interface Props {
  modelValue: string;
  placeholder?: string;
  isStreaming?: boolean;
}

defineProps<Props>();
defineEmits(['update:modelValue', 'send', 'stop']);
</script>
