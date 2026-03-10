<template>
  <div class="max-w-5xl mx-auto space-y-6 pb-12">
    <!-- Header Info -->
    <div class="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark p-6">
      <div class="flex justify-between items-start mb-6">
        <div>
          <div class="flex items-center gap-3 mb-2">
            <h1 class="text-2xl font-bold text-slate-900 dark:text-white">{{ plan.title }}</h1>
            <span :class="['text-xs px-2.5 py-1 rounded-full font-medium', getStatusColor(plan.status)]">
              {{ getStatusText(plan.status) }}
            </span>
          </div>
          <p class="text-sm text-slate-500 dark:text-slate-400 font-mono">{{ plan.id }} · 更新于 {{ plan.updatedAt }}</p>
        </div>
        <button class="px-4 py-2 bg-primary hover:bg-blue-600 text-white rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
          <Icon name="edit" :size="18" />
          编辑方案
        </button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="space-y-1">
          <span class="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">客户</span>
          <p class="font-medium text-slate-900 dark:text-white flex items-center gap-2">
            <Icon name="business" class="text-slate-400" :size="18" />
            {{ plan.customer }}
          </p>
        </div>
        <div class="space-y-1">
          <span class="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">预算</span>
          <p class="font-medium text-emerald-600 dark:text-emerald-400 flex items-center gap-2">
            <Icon name="payments" class="text-emerald-500" :size="18" />
            ¥ {{ plan.budget.toLocaleString() }}
          </p>
        </div>
        <div class="space-y-1">
          <span class="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">媒体类型</span>
          <div class="flex flex-wrap gap-2 mt-1">
            <span 
              v-for="(type, idx) in plan.mediaTypes" 
              :key="idx" 
              class="text-xs bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-300 px-2 py-1 rounded-md border border-indigo-100 dark:border-indigo-800/50"
            >
              {{ type }}
            </span>
          </div>
        </div>
        <div class="md:col-span-3 space-y-1">
          <span class="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">需求说明</span>
          <p class="text-sm text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800/50 p-4 rounded-xl border border-border-light dark:border-border-dark leading-relaxed">
            {{ plan.requirements }}
          </p>
        </div>
      </div>
    </div>

    <!-- Points Section -->
    <div class="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark overflow-hidden flex flex-col">
      <div class="p-4 border-b border-border-light dark:border-border-dark flex items-center justify-between bg-slate-50/50 dark:bg-slate-800/30">
        <h2 class="font-bold text-slate-900 dark:text-white flex items-center gap-2">
          <Icon name="place" class="text-primary" :size="20" />
          选点列表 ({{ plan.points.length }} 个点位)
        </h2>
      </div>
      
      <div v-if="plan.points.length === 0" class="p-8 text-center text-slate-500 dark:text-slate-400">
        暂无选点数据
      </div>
      
      <div v-else class="divide-y divide-border-light dark:divide-border-dark">
        <div v-for="point in plan.points" :key="point.id" class="p-4 hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors">
          <div class="flex items-center justify-between">
            <div>
              <h3 class="font-medium text-slate-900 dark:text-white">{{ point.name }}</h3>
              <p class="text-sm text-slate-500 dark:text-slate-400">{{ point.location }}</p>
            </div>
            <span class="text-xs text-slate-400">{{ point.id }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Mockups -->
    <div class="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark p-6">
      <h2 class="font-bold text-slate-900 dark:text-white flex items-center gap-2 mb-4">
        <Icon name="image" class="text-primary" :size="20" />
        设计小样图
      </h2>
      <div v-if="plan.mockups.length === 0" class="h-32 flex items-center justify-center border-2 border-dashed border-border-light dark:border-border-dark rounded-xl text-slate-400 text-sm">
        暂无设计图
      </div>
      <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div 
          v-for="(url, idx) in plan.mockups" 
          :key="idx" 
          class="aspect-video rounded-lg overflow-hidden border border-border-light dark:border-border-dark group relative cursor-pointer"
        >
          <img :src="url" :alt="`Mockup ${idx + 1}`" class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" referrerpolicy="no-referrer" />
          <div class="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors flex items-center justify-center">
            <Icon name="zoom_in" class="text-white" :size="20" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Icon from '@/src/components/Icon.vue';

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

defineProps<{
  plan: DisplayPlan;
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
