<template>
  <aside class="w-48 h-full bg-surface-light dark:bg-surface-dark border-r border-gray-200 dark:border-gray-700 flex flex-col flex-shrink-0 transition-colors duration-300 z-20">
    <div class="h-16 flex items-center px-4 border-b border-gray-100 dark:border-gray-800">
      <div class="w-8 h-8 rounded-lg bg-gradient-to-br from-primary to-accent flex items-center justify-center text-white font-bold text-lg mr-2 shadow-lg flex-shrink-0">
        O
      </div>
      <div class="overflow-hidden">
        <h1 class="font-display font-bold text-base tracking-tight text-text-light dark:text-text-dark truncate">Outdoor 4.0</h1>
        <p class="text-[9px] text-subtext-light dark:text-subtext-dark uppercase tracking-wider font-semibold truncate">AI SaaS 平台</p>
      </div>
    </div>
    <nav class="flex-1 overflow-y-auto py-4 px-2 space-y-1 custom-scrollbar">
      <template v-for="(item, index) in navItems" :key="index">
        <div v-if="item.type === 'divider'" class="pt-4 pb-2 px-3">
          <p class="text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">{{ item.name }}</p>
        </div>
        <router-link
          v-else
          :to="item.path!"
          :class="[
            'flex items-center px-3 py-2 rounded-lg transition-colors group',
            isActive(item.path!)
              ? 'bg-primary/10 text-primary border-l-4 border-primary'
              : 'text-subtext-light dark:text-subtext-dark hover:bg-gray-100 dark:hover:bg-gray-800 hover:text-primary'
          ]"
        >
          <span :class="['mr-3 transition-colors', isActive(item.path!) ? 'text-primary' : 'group-hover:text-primary']">
            <Icon :name="item.icon!" :size="20" />
          </span>
          <span class="font-medium text-sm">{{ item.name }}</span>
          <span v-if="item.path === '/ai-assistant'" class="ml-auto text-xs bg-gradient-to-r from-primary to-primary/80 text-white px-2 py-0.5 rounded-full">
            AI
          </span>
        </router-link>
      </template>
    </nav>
    <div class="p-4 border-t border-gray-200 dark:border-gray-700">
      <div class="flex items-center gap-3">
        <div class="w-9 h-9 rounded-full bg-gradient-to-br from-blue-400 to-blue-600 flex items-center justify-center text-white font-bold text-sm border-2 border-surface-light dark:border-gray-600">
          {{ userInitial }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-sm font-medium text-text-light dark:text-text-dark truncate">
            {{ userDisplayName }}
          </p>
          <p class="text-xs text-subtext-light dark:text-subtext-dark truncate">
            {{ userRole }}
          </p>
        </div>
        <button 
          @click="handleLogout"
          class="text-subtext-light hover:text-primary dark:text-subtext-dark dark:hover:text-white transition-colors"
          title="退出登录"
        >
          <Icon name="logout" :size="20" />
        </button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useAuthStore } from '@/src/stores/auth';
import Icon from './Icon.vue';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const navItems = [
  { type: 'divider', name: '资源运营' },
  { name: '社区管理', icon: 'apartment', path: '/communities' },
  { name: '道闸管理', icon: 'gate', path: '/barrier-gates' },
  { name: '框架管理', icon: 'grid_view', path: '/frames' },
  { type: 'divider', name: '方案管理' },
  { name: '方案列表', icon: 'psychology', path: '/plans' },
  { name: '方案社区', icon: 'location_on', path: '/plan-communities' },
  { name: '方案道闸', icon: 'directions_car', path: '/plan-barriers' },
  { name: '方案框架', icon: 'view_module', path: '/plan-frames' },
  { type: 'divider', name: '智能助手' },
  { name: 'AI 助手', icon: 'smart_toy', path: '/ai-assistant' },
];

const isActive = (path: string) => {
  return route.path === path || (path === '/ai-assistant' && route.path.startsWith('/ai-assistant'));
};

const userInitial = computed(() => {
  return authStore.user?.realName?.charAt(0) || authStore.user?.username?.charAt(0) || 'U';
});

const userDisplayName = computed(() => {
  return authStore.user?.realName || authStore.user?.username || '用户';
});

const userRole = computed(() => {
  const role = authStore.user?.role;
  switch (role) {
    case 'ADMIN': return '管理员';
    case 'SALES': return '销售经理';
    case 'MEDIA': return '媒介专员';
    case 'ENGINEERING': return '工程师';
    case 'FINANCE': return '财务经理';
    default: return '用户';
  }
});

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>
