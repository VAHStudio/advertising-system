<template>
  <div class="min-h-screen bg-background-light dark:bg-background-dark transition-colors duration-200">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useAuthStore } from '@/src/stores/auth';

const authStore = useAuthStore();

onMounted(() => {
  authStore.initAuth();
});
</script>
