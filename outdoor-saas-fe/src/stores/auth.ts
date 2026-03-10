import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import * as authService from '@/src/services/authService';
import type { User } from '@/src/services/authService';

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null);
  const isLoading = ref(true);

  // Getters
  const isAuthenticated = computed(() => !!user.value);

  // Actions
  const initAuth = () => {
    const currentUser = authService.getCurrentUser();
    if (currentUser) {
      user.value = currentUser;
    }
    isLoading.value = false;
  };

  const login = async (username: string, password: string) => {
    isLoading.value = true;
    try {
      const response = await authService.login({ username, password });
      user.value = response.user;
    } finally {
      isLoading.value = false;
    }
  };

  const register = async (
    username: string,
    password: string,
    realName?: string,
    email?: string,
    phone?: string
  ) => {
    isLoading.value = true;
    try {
      const response = await authService.register({
        username,
        password,
        realName,
        email,
        phone,
      });
      user.value = response.user;
    } finally {
      isLoading.value = false;
    }
  };

  const logout = () => {
    authService.logout();
    user.value = null;
  };

  const refreshUser = async () => {
    try {
      const refreshedUser = await authService.fetchCurrentUser();
      user.value = refreshedUser;
    } catch (error) {
      console.error('刷新用户信息失败:', error);
      logout();
    }
  };

  return {
    user,
    isLoading,
    isAuthenticated,
    initAuth,
    login,
    register,
    logout,
    refreshUser,
  };
});
