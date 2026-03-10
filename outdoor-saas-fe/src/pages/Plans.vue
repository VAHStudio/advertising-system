<template>
  <div class="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
    <!-- Header -->
    <div class="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
      <h1 class="text-xl font-bold text-slate-900 dark:text-white">广告方案管理</h1>
      <div v-if="selectedPlan" class="flex items-center gap-3">
        <button 
          @click="selectedPlan = null"
          class="px-3 py-1.5 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-sm font-medium transition-colors flex items-center gap-1.5"
        >
          <Icon name="arrow_back" :size="18" />
          返回看板
        </button>
      </div>
      <div v-else class="flex items-center gap-3">
        <!-- 视图切换按钮 -->
        <div class="flex items-center bg-slate-100 dark:bg-slate-800 p-1 rounded-lg">
          <button
            @click="viewMode = 'kanban'"
            :class="[
              'px-3 py-1.5 rounded-md text-sm font-medium flex items-center gap-1.5 transition-colors',
              viewMode === 'kanban'
                ? 'bg-white dark:bg-slate-700 shadow-sm text-slate-900 dark:text-white'
                : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
            ]"
          >
            <Icon name="grid_view" :size="16" />
            看板视图
          </button>
          <button
            @click="viewMode = 'list'"
            :class="[
              'px-3 py-1.5 rounded-md text-sm font-medium flex items-center gap-1.5 transition-colors',
              viewMode === 'list'
                ? 'bg-white dark:bg-slate-700 shadow-sm text-slate-900 dark:text-white'
                : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
            ]"
          >
            <Icon name="list" :size="16" />
            列表视图
          </button>
        </div>
        <button class="px-4 py-2 bg-primary hover:bg-blue-600 text-white rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
          <Icon name="add" :size="18" />
          创建方案
        </button>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
        <p class="text-slate-500 dark:text-slate-400">加载方案数据中...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="flex-1 flex items-center justify-center">
      <div class="text-center">
        <p class="text-red-500 mb-4">{{ error }}</p>
        <button 
          @click="refresh"
          class="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition-colors"
        >
          重新加载
        </button>
      </div>
    </div>

    <!-- Content -->
    <div v-else class="flex-1 overflow-hidden flex flex-col">
      <div class="flex-1 overflow-y-auto custom-scrollbar p-6">
        <KanbanView 
          v-if="!selectedPlan && viewMode === 'kanban'" 
          :plans="displayPlans"
          @select="selectPlan"
        />
        <ListView 
          v-else-if="!selectedPlan && viewMode === 'list'" 
          :plans="displayPlans"
          @select="selectPlan"
        />
        <PlanDetailView 
          v-else-if="selectedPlan" 
          :plan="selectedPlan"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { planService } from '@/src/services/planService';
import type { Plan } from '@/src/services/planService';
import type { PlanQueryParam } from '@/src/types/query';
import { usePagination } from '@/src/composables/usePagination';
import Icon from '@/src/components/Icon.vue';
import KanbanView from './components/KanbanView.vue';
import ListView from './components/ListView.vue';
import PlanDetailView from './components/PlanDetailView.vue';

type ViewMode = 'kanban' | 'list';
type PlanStatus = 'draft' | 'communicating' | 'pending' | 'signed';

interface DisplayPlan {
  id: string;
  title: string;
  customer: string;
  status: PlanStatus;
  budget: number;
  requirements: string;
  mediaTypes: string[];
  points: any[];
  mockups: string[];
  updatedAt: string;
}

const viewMode = ref<ViewMode>('kanban');
const selectedPlan = ref<DisplayPlan | null>(null);

const {
  data: plans,
  loading,
  error,
  refresh,
} = usePagination<Plan, PlanQueryParam>({
  fetchFn: planService.filterPage,
  defaultPageSize: 10,
});

const mapStatus = (releaseStatus: number): PlanStatus => {
  switch (releaseStatus) {
    case 0: return 'draft';
    case 1: return 'communicating';
    case 2: return 'pending';
    case 3: return 'signed';
    default: return 'draft';
  }
};

const displayPlans = computed<DisplayPlan[]>(() => {
  return plans.value.map(plan => ({
    id: plan.planNo,
    title: plan.planName,
    customer: plan.customer,
    status: mapStatus(plan.releaseStatus),
    budget: plan.budget || 500000,
    requirements: plan.mediaRequirements || '暂无需求说明',
    mediaTypes: ['电梯框架', '道闸'],
    updatedAt: plan.updatedAt ? new Date(plan.updatedAt).toLocaleDateString('zh-CN') : new Date().toLocaleDateString('zh-CN'),
    points: [],
    mockups: []
  }));
});

const selectPlan = (plan: DisplayPlan) => {
  selectedPlan.value = plan;
};
</script>
