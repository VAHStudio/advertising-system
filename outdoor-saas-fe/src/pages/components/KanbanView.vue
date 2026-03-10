<template>
  <div class="flex gap-6 h-full overflow-x-auto pb-4">
    <div 
      v-for="col in columns" 
      :key="col.id" 
      class="flex-shrink-0 w-80 flex flex-col bg-slate-100/50 dark:bg-slate-800/20 rounded-2xl border border-border-light dark:border-border-dark overflow-hidden"
    >
      <!-- Column Header -->
      <div class="p-4 border-b border-border-light dark:border-border-dark flex items-center justify-between bg-slate-100 dark:bg-slate-800/50">
        <div class="flex items-center gap-2">
          <span :class="['w-2 h-2 rounded-full', col.color.split(' ')[0]]"></span>
          <h3 class="font-bold text-slate-800 dark:text-white">{{ col.title }}</h3>
        </div>
        <span class="text-xs font-medium bg-white dark:bg-slate-700 px-2 py-1 rounded-full text-slate-500 dark:text-slate-400 shadow-sm">
          {{ getColumnPlans(col.id).length }}
        </span>
      </div>

      <!-- Cards -->
      <div class="flex-1 overflow-y-auto p-3 space-y-3 custom-scrollbar">
        <div 
          v-for="plan in getColumnPlans(col.id)" 
          :key="plan.id"
          @click="$emit('select', plan)"
          class="bg-white dark:bg-surface-dark p-4 rounded-xl shadow-sm border border-border-light dark:border-border-dark cursor-pointer hover:shadow-md hover:border-primary transition-all group"
        >
          <div class="flex justify-between items-start mb-2">
            <span class="text-[10px] font-mono text-slate-400">{{ plan.id }}</span>
            <span :class="['text-[10px] px-2 py-0.5 rounded-full font-medium', col.color]">
              {{ col.title }}
            </span>
          </div>
          <h4 class="font-bold text-slate-900 dark:text-white mb-1 group-hover:text-primary transition-colors">{{ plan.title }}</h4>
          <p class="text-xs text-slate-500 dark:text-slate-400 mb-3">{{ plan.customer }}</p>
          
          <div class="flex flex-wrap gap-1 mb-3">
            <span 
              v-for="(type, idx) in plan.mediaTypes" 
              :key="idx" 
              class="text-[10px] bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 px-1.5 py-0.5 rounded"
            >
              {{ type }}
            </span>
          </div>
          
          <div class="flex justify-between items-center pt-3 border-t border-border-light dark:border-border-dark">
            <span class="text-xs font-medium text-emerald-600 dark:text-emerald-400">¥ {{ (plan.budget / 10000).toFixed(1) }}w</span>
            <div class="flex items-center gap-2">
              <button
                @click.stop="$router.push(`/plans/${plan.id}`)"
                class="text-[10px] text-primary hover:text-primary/80 transition-colors"
              >
                查看详情 →
              </button>
              <span class="text-[10px] text-slate-400">{{ plan.updatedAt }}</span>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="getColumnPlans(col.id).length === 0" class="h-24 flex items-center justify-center border-2 border-dashed border-border-light dark:border-border-dark rounded-xl text-slate-400 text-sm">
          暂无方案
        </div>
      </div>
    </div>
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

const columns = [
  { id: 'draft' as const, title: '草稿', color: 'bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300' },
  { id: 'communicating' as const, title: '沟通中', color: 'bg-blue-100 dark:bg-blue-900/50 text-blue-700 dark:text-blue-300' },
  { id: 'pending' as const, title: '待确认', color: 'bg-amber-100 dark:bg-amber-900/50 text-amber-700 dark:text-amber-300' },
  { id: 'signed' as const, title: '已签约', color: 'bg-emerald-100 dark:bg-emerald-900/50 text-emerald-700 dark:text-emerald-300' }
];

const router = useRouter();

const props = defineProps<{
  plans: DisplayPlan[];
}>();

defineEmits<{
  select: [plan: DisplayPlan];
}>();

const getColumnPlans = (status: string) => {
  return props.plans.filter(p => p.status === status);
};
</script>
