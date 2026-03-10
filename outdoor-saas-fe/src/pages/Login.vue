<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 p-4">
    <transition
      appear
      enter-active-class="transition-all duration-500"
      enter-from-class="opacity-0 translate-y-5"
      enter-to-class="opacity-100 translate-y-0"
    >
      <div class="w-full max-w-md">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-8">
          <!-- Logo -->
          <div class="text-center mb-8">
            <div class="w-16 h-16 mx-auto rounded-xl bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold text-2xl mb-4">
              O
            </div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">
              Outdoor 4.0
            </h1>
            <p class="text-gray-500 dark:text-gray-400">AI SaaS 广告投放管理平台</p>
          </div>

          <!-- 错误提示 -->
          <transition
            enter-active-class="transition-all duration-300"
            enter-from-class="opacity-0 h-0"
            enter-to-class="opacity-100 h-auto"
          >
            <div v-if="error" class="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
              <div class="flex items-center gap-2 text-red-600 dark:text-red-400">
                <Icon name="error" :size="20" />
                <span class="text-sm">{{ error }}</span>
              </div>
            </div>
          </transition>

          <!-- 登录表单 -->
          <form @submit.prevent="handleSubmit" class="space-y-6">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                用户名
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Icon name="person" :size="20" className="text-gray-400" />
                </div>
                <input
                  v-model="username"
                  type="text"
                  class="w-full pl-10 pr-4 py-3 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white transition-colors"
                  placeholder="请输入用户名"
                  :disabled="isLoading"
                  autofocus
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                密码
              </label>
              <div class="relative">
                <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Icon name="lock" :size="20" className="text-gray-400" />
                </div>
                <input
                  v-model="password"
                  :type="showPassword ? 'text' : 'password'"
                  class="w-full pl-10 pr-12 py-3 border border-gray-300 dark:border-gray-600 rounded-lg focus:ring-2 focus:ring-primary focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white transition-colors"
                  placeholder="请输入密码"
                  :disabled="isLoading"
                />
                <button
                  type="button"
                  @click="showPassword = !showPassword"
                  class="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
                >
                  <Icon :name="showPassword ? 'visibility_off' : 'visibility'" :size="20" />
                </button>
              </div>
            </div>

            <div class="flex items-center justify-between">
              <label class="flex items-center">
                <input
                  type="checkbox"
                  class="w-4 h-4 text-primary border-gray-300 rounded focus:ring-primary"
                />
                <span class="ml-2 text-sm text-gray-600 dark:text-gray-400">记住我</span>
              </label>
              <a
                href="#"
                class="text-sm text-primary hover:text-primary/80 transition-colors"
              >
                忘记密码？
              </a>
            </div>

            <button
              type="submit"
              :disabled="isLoading"
              class="w-full py-3 px-4 bg-primary hover:bg-primary/90 text-white font-medium rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              <template v-if="isLoading">
                <div class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                登录中...
              </template>
              <template v-else>
                <Icon name="login" :size="20" />
                登录
              </template>
            </button>
          </form>
        </div>

        <!-- 版权信息 -->
        <p class="mt-8 text-center text-xs text-gray-500 dark:text-gray-400">
          © 2024 Outdoor 4.0. All rights reserved.
        </p>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/src/stores/auth';
import Icon from '@/src/components/Icon.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const username = ref('');
const password = ref('');
const error = ref('');
const isLoading = ref(false);
const showPassword = ref(false);

const from = (route.query.from as string) || '/';

const handleSubmit = async () => {
  error.value = '';

  if (!username.value.trim() || !password.value.trim()) {
    error.value = '请输入用户名和密码';
    return;
  }

  isLoading.value = true;

  try {
    await authStore.login(username.value, password.value);
    router.replace(from || '/');
  } catch (err) {
    error.value = err instanceof Error ? err.message : '登录失败';
  } finally {
    isLoading.value = false;
  }
};
</script>
