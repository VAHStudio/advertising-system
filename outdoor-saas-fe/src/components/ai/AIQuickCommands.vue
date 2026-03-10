<template>
  <div v-if="commands.length > 0" class="px-6 py-4 border-b border-border-light dark:border-border-dark">
    <p class="text-xs text-subtext-light dark:text-subtext-dark mb-3">快捷指令</p>
    <div class="flex flex-wrap gap-2">
      <button
        v-for="cmd in commands"
        :key="cmd.command"
        @click="$emit('select', cmd.command)"
        :disabled="disabled"
        class="px-3 py-1.5 bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-lg text-sm hover:border-primary hover:text-primary transition-colors disabled:opacity-50"
      >
        <span class="mr-1">{{ cmd.icon }}</span>
        {{ cmd.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { QuickCommand } from './types';

interface Props {
  commands: QuickCommand[];
  disabled?: boolean;
}

defineProps<Props>();
defineEmits(['select']);
</script>
