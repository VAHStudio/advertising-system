<template>
  <div class="bg-white dark:bg-surface-dark rounded-xl shadow-sm border border-border-light dark:border-border-dark overflow-hidden">
    <table class="w-full">
      <thead class="bg-gray-50 dark:bg-gray-800 border-b border-border-light dark:border-border-dark">
        <tr>
          <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">方案编号</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">方案名称</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">客户</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">状态</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">更新时间</th>
          <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">操作</th>
        </tr>
      </thead>
      <tbody class="divide-y divide-border-light dark:divide-border-dark">
        <tr 
          v-for="plan in plans" 
          :key="plan.id" 
          class="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors cursor-pointer"
          @click="$emit('select', plan)"
        >
          <td class="px-6 py-4 whitespace-nowrap text-sm font-mono text-subtext-light dark:text-subtext-dark">{{ plan.id }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-text-light dark:text-text-dark">{{ plan.title }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ plan.customer }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm">
            <span :class="['px-2 py-1 rounded text-xs font-medium', getStatusColor(plan.status)]">
              {{ getStatusText(plan.status) }}
            </span>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ plan.updatedAt }}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2" @click.stop>
            <button
              @click="$router.push(`/plans/${plan.id}`)"
              class="text-primary hover:text-primary/80 transition-colors"
            >
              查看
            </button>
            <button class="text-primary hover:text-primary/80 transition-colors">编辑</button>
            <button class="text-red-500 hover:text-red-600 transition-colors">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router';

interface DisplayPlan {
  id: string;
  title: string;
  customer: string;
  status: 'draft' | 'communicating' | 'pending' | 'signed';
  budget: number;
  requirements: string;
  mediaTypes: string[];
  points: any[];
  mockups: string[];
  updatedAt: string;
}

const router = useRouter();

defineProps<{
  plans: DisplayPlan[];
}>();

defineEmits<{
  select: [plan: DisplayPlan];
}>();

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    communicating: '沟通中',
    pending: '待确认',
    signed: '已签约'
  };
  return map[status] || status;
};

const getStatusColor = (status: string) => {
  const map: Record<string, string> = {
    draft: 'bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300',
    communicating: 'bg-blue-100 dark:bg-blue-900/50 text-blue-700 dark:text-blue-300',
    pending: 'bg-amber-100 dark:bg-amber-900/50 text-amber-700 dark:text-amber-300',
    signed: 'bg-emerald-100 dark:bg-emerald-900/50 text-emerald-700 dark:text-emerald-300'
  };
  return map[status] || '';
};
</script>
