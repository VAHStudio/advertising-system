<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-text-light dark:text-text-dark">方案道闸明细</h1>
      <button class="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
        添加明细
      </button>
    </div>

    <div v-if="loading" class="flex items-center justify-center h-64">
      <div class="text-subtext-light dark:text-subtext-dark">加载中...</div>
    </div>

    <div v-else-if="error" class="flex items-center justify-center h-64">
      <div class="text-red-500">{{ error }}</div>
    </div>

    <div v-else class="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
      <table class="w-full">
        <thead class="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
          <tr>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">ID</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">方案ID</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">道闸ID</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">方案社区ID</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">发布状态</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="item in planBarriers" :key="item.id" class="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
            <td class="px-6 py-4 whitespace-nowrap text-sm text-text-light dark:text-text-dark">{{ item.id }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-text-light dark:text-text-dark">{{ item.planId }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ item.barrierGateId }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ item.planCommunityId }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm">
              <span :class="['px-2 py-1 rounded-full text-xs', item.releaseStatus === 1 
                ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' 
                : 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200']">
                {{ item.releaseStatus === 1 ? '已发布' : '未发布' }}
              </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2">
              <button class="text-primary hover:text-primary/80 transition-colors">编辑</button>
              <button class="text-red-500 hover:text-red-600 transition-colors">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <Pagination
        :current="currentPage"
        :page-size="pageSize"
        :total="total"
        @change="setPage"
        @page-size-change="setPageSize"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { planBarrierService, type PlanBarrier, type PlanBarrierQueryParam } from '@/src/services/planBarrierService';
import Pagination from '@/src/components/Pagination.vue';
import { usePagination } from '@/src/composables/usePagination';

const {
  data: planBarriers,
  loading,
  error,
  currentPage,
  pageSize,
  total,
  setPage,
  setPageSize,
} = usePagination<PlanBarrier, PlanBarrierQueryParam>({
  fetchFn: planBarrierService.filterPage,
  defaultPageSize: 10,
});
</script>
