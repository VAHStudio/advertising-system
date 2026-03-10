<template>
  <header class="h-16 bg-surface-light dark:bg-surface-dark/80 backdrop-blur-md sticky top-0 z-10 border-b border-gray-200 dark:border-gray-700 px-6 lg:px-8 flex items-center justify-between flex-shrink-0">
    <div>
      <h2 class="text-xl font-display font-bold text-text-light dark:text-text-dark">{{ title }}</h2>
      <p v-if="subtitle" class="text-xs text-subtext-light dark:text-subtext-dark">{{ subtitle }}</p>
    </div>
    <div class="flex items-center gap-4">
      <slot />
      <div class="hidden md:flex items-center gap-2 border-l border-gray-200 dark:border-gray-700 pl-4 ml-2">
        <button class="relative p-2 text-subtext-light dark:text-subtext-dark hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors">
          <Icon name="notifications" :size="20" />
          <span class="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-surface-dark"></span>
        </button>
        <button 
          class="p-2 text-subtext-light dark:text-subtext-dark hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors flex items-center justify-center w-10 h-10" 
          @click="toggleDarkMode"
        >
          <Icon :name="isDarkMode ? 'light_mode' : 'dark_mode'" :size="20" />
        </button>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Icon from './Icon.vue';

interface Props {
  title: string;
  subtitle?: string;
}

defineProps<Props>();

const isDarkMode = ref(false);

onMounted(() => {
  isDarkMode.value = document.documentElement.classList.contains('dark');
});

const toggleDarkMode = () => {
  const isDark = document.documentElement.classList.toggle('dark');
  localStorage.setItem('theme', isDark ? 'dark' : 'light');
  isDarkMode.value = isDark;
};
</script>
