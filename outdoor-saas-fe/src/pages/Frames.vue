<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-bold text-text-light dark:text-text-dark">框架管理</h1>
      <button class="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
        新增框架
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
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">框架编号</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">楼栋</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">单元</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">电梯</th>
            <th class="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
          <tr
            v-for="frame in frames"
            :key="frame.id"
            class="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors cursor-pointer"
            @click="$router.push(`/frames/${frame.id}`)"
          >
            <td class="px-6 py-4 whitespace-nowrap text-sm text-text-light dark:text-text-dark">{{ frame.id }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-text-light dark:text-text-dark">{{ frame.frameNo }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ frame.building }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ frame.unit }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{{ frame.elevator }}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm space-x-2" @click.stop>
              <button
                class="text-primary hover:text-primary/80 transition-colors"
                @click="$router.push(`/frames/${frame.id}`)"
              >
                查看
              </button>
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
import { frameService } from '@/src/services/frameService';
import type { Frame } from '@/src/services/frameService';
import type { FrameQueryParam } from '@/src/types/query';
import Pagination from '@/src/components/Pagination.vue';
import { usePagination } from '@/src/composables/usePagination';

const {
  data: frames,
  loading,
  error,
  currentPage,
  pageSize,
  total,
  setPage,
  setPageSize,
} = usePagination<Frame, FrameQueryParam>({
  fetchFn: frameService.filterPage,
  defaultPageSize: 10,
});
</script>
