<template>
  <div class="space-y-6">
    <div v-if="loading" class="flex items-center justify-center h-64">
      <div class="text-subtext-light dark:text-subtext-dark">加载中...</div>
    </div>

    <div v-else-if="error || !plan" class="flex items-center justify-center h-64">
      <div class="text-red-500">{{ error || '方案不存在' }}</div>
    </div>

    <template v-else>
      <!-- 页面标题 -->
      <div class="flex items-center justify-between">
        <div class="flex items-center space-x-4">
          <button
            @click="$router.push('/plans')"
            class="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 class="text-2xl font-bold text-text-light dark:text-text-dark">
            方案详情
          </h1>
        </div>
        <div class="space-x-2">
          <button class="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
            编辑
          </button>
          <button class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors">
            删除
          </button>
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-text-light dark:text-text-dark">基本信息</h2>
          <span :class="['px-3 py-1 rounded-full text-sm font-medium', getReleaseStatusColor(plan.releaseStatus)]">
            {{ getReleaseStatusText(plan.releaseStatus) }}
          </span>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">方案编号</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ plan.planNo }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">方案名称</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ plan.planName }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">客户名称</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ plan.customer }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">销售类型</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ getSalesTypeText(plan.salesType) }}</p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">投放日期</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              {{ plan.releaseDateBegin }} ~ {{ plan.releaseDateEnd }}
            </p>
          </div>
          <div>
            <label class="text-sm text-subtext-light dark:text-subtext-dark">资源统计</label>
            <p class="text-text-light dark:text-text-dark font-medium">
              社区: {{ communities.length }} | 道闸: {{ barriers.length }} | 框架: {{ frames.length }}
            </p>
          </div>
          <div v-if="plan.mediaRequirements" class="col-span-2">
            <label class="text-sm text-subtext-light dark:text-subtext-dark">媒体要求</label>
            <p class="text-text-light dark:text-text-dark font-medium">{{ plan.mediaRequirements }}</p>
          </div>
        </div>
      </div>

      <!-- 社区列表 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-semibold text-text-light dark:text-text-dark">
            关联社区 ({{ communities.length }})
          </h2>
        </div>
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">社区编号</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">社区名称</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">投放日期</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">道闸/框架</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="community in communities" :key="community.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50">
              <td class="px-6 py-4 text-sm text-text-light dark:text-text-dark">{{ community.communityNo }}</td>
              <td class="px-6 py-4 text-sm text-text-light dark:text-text-dark">{{ community.buildingName }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">
                {{ community.releaseDateBegin }} ~ {{ community.releaseDateEnd }}
              </td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">
                {{ community.barrierRequiredQty }} / {{ community.frameRequiredQty }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 道闸列表 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-semibold text-text-light dark:text-text-dark">
            关联道闸 ({{ barriers.length }})
          </h2>
        </div>
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">道闸编号</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">所属社区</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">状态</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="barrier in barriers" :key="barrier.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50">
              <td class="px-6 py-4 text-sm text-text-light dark:text-text-dark">{{ barrier.gateNo }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ barrier.communityName }}</td>
              <td class="px-6 py-4 text-sm">
                <span :class="['px-2 py-1 rounded text-xs', getReleaseStatusColor(barrier.releaseStatus)]">
                  {{ getReleaseStatusText(barrier.releaseStatus) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 框架列表 -->
      <div class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 class="text-lg font-semibold text-text-light dark:text-text-dark">
            关联框架 ({{ frames.length }})
          </h2>
        </div>
        <table class="w-full">
          <thead class="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">框架编号</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">所属社区</th>
              <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">状态</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="frame in frames" :key="frame.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50">
              <td class="px-6 py-4 text-sm text-text-light dark:text-text-dark">{{ frame.frameNo }}</td>
              <td class="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{{ frame.communityName }}</td>
              <td class="px-6 py-4 text-sm">
                <span :class="['px-2 py-1 rounded text-xs', getReleaseStatusColor(frame.releaseStatus)]">
                  {{ getReleaseStatusText(frame.releaseStatus) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import planService, { Plan } from '@/src/services/planService';
import { planCommunityService } from '@/src/services/planCommunityService';
import { planBarrierService } from '@/src/services/planBarrierService';
import { planFrameService } from '@/src/services/planFrameService';

interface PlanCommunity {
  id: number;
  planId: number;
  communityId: number;
  communityNo?: string;
  buildingName?: string;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  barrierRequiredQty?: number;
  frameRequiredQty?: number;
}

interface PlanBarrier {
  id: number;
  planId: number;
  barrierGateId: number;
  gateNo?: string;
  communityName?: string;
  releaseStatus?: number;
}

interface PlanFrame {
  id: number;
  planId: number;
  frameId: number;
  frameNo?: string;
  communityName?: string;
  releaseStatus?: number;
}

const route = useRoute();
const plan = ref<Plan | null>(null);
const communities = ref<PlanCommunity[]>([]);
const barriers = ref<PlanBarrier[]>([]);
const frames = ref<PlanFrame[]>([]);
const loading = ref(true);
const error = ref('');

const loadPlanDetail = async (planId: number) => {
  try {
    loading.value = true;
    
    const planData = await planService.getPlanById(planId);
    plan.value = planData;

    // 并行加载关联数据
    const [communitiesData, barriersData, framesData] = await Promise.all([
      planCommunityService.getByPlan(planId),
      planBarrierService.getByPlan(planId),
      planFrameService.getByPlan(planId),
    ]);

    communities.value = communitiesData;
    barriers.value = barriersData;
    frames.value = framesData;
  } catch (err) {
    error.value = err instanceof Error ? err.message : '加载失败';
  } finally {
    loading.value = false;
  }
};

const getReleaseStatusText = (status?: number) => {
  const statusMap: Record<number, string> = {
    1: '未发布',
    2: '已发布',
    3: '执行中',
    4: '已完成',
    5: '已取消',
  };
  return statusMap[status || 1] || '未知';
};

const getReleaseStatusColor = (status?: number) => {
  switch (status) {
    case 1: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
    case 2: return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200';
    case 3: return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200';
    case 4: return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200';
    case 5: return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200';
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300';
  }
};

const getSalesTypeText = (type?: number) => {
  switch (type) {
    case 1: return '直销';
    case 2: return '代理';
    default: return '未知';
  }
};

onMounted(() => {
  const id = route.params.id as string;
  if (id) {
    loadPlanDetail(parseInt(id));
  }
});
</script>
