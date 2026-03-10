<template>
  <div class="flex items-center justify-between px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
    <!-- 左侧：统计信息 -->
    <div class="text-sm text-subtext-light dark:text-subtext-dark">
      共 <span class="font-medium text-text-light dark:text-text-dark">{{ total }}</span> 条记录
      <span v-if="totalPages > 1" class="ml-2">
        第 <span class="font-medium text-text-light dark:text-text-dark">{{ current }}</span> / {{ totalPages }} 页
      </span>
    </div>

    <!-- 中间：页码 -->
    <div class="flex items-center space-x-1">
      <!-- 上一页 -->
      <button
        @click="$emit('change', current - 1)"
        :disabled="current <= 1"
        class="p-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200 dark:hover:bg-gray-700 text-subtext-light dark:text-subtext-dark"
      >
        <ChevronLeft :size="18" />
      </button>

      <!-- 页码按钮 -->
      <template v-for="(page, index) in pageNumbers" :key="index">
        <span v-if="typeof page === 'string'" class="px-3 py-2 text-subtext-light dark:text-subtext-dark">{{ page }}</span>
        <button
          v-else
          @click="$emit('change', page as number)"
          :class="[
            'px-3 py-2 rounded-lg text-sm font-medium transition-colors',
            current === page
              ? 'bg-primary text-white'
              : 'text-subtext-light dark:text-subtext-dark hover:bg-gray-200 dark:hover:bg-gray-700'
          ]"
        >
          {{ page }}
        </button>
      </template>

      <!-- 下一页 -->
      <button
        @click="$emit('change', current + 1)"
        :disabled="current >= totalPages"
        class="p-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200 dark:hover:bg-gray-700 text-subtext-light dark:text-subtext-dark"
      >
        <ChevronRight :size="18" />
      </button>
    </div>

    <!-- 右侧：每页条数选择 -->
    <div v-if="showPageSizeSelector" class="flex items-center space-x-2">
      <span class="text-sm text-subtext-light dark:text-subtext-dark">每页</span>
      <select
        :value="pageSize"
        @change="$emit('pageSizeChange', Number(($event.target as HTMLSelectElement).value))"
        class="px-2 py-1.5 text-sm rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-text-light dark:text-text-dark focus:outline-none focus:ring-2 focus:ring-primary"
      >
        <option v-for="size in pageSizeOptions" :key="size" :value="size">
          {{ size }}
        </option>
      </select>
      <span class="text-sm text-subtext-light dark:text-subtext-dark">条</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ChevronLeft, ChevronRight } from 'lucide-vue-next';

interface Props {
  current: number;
  pageSize: number;
  total: number;
  pageSizeOptions?: number[];
  showPageSizeSelector?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  pageSizeOptions: () => [10, 20, 50, 100],
  showPageSizeSelector: true
});

defineEmits<{
  (e: 'change', page: number): void;
  (e: 'pageSizeChange', size: number): void;
}>();

const totalPages = computed(() => Math.ceil(props.total / props.pageSize));

// 计算要显示的页码
const pageNumbers = computed(() => {
  const pages: (number | string)[] = [];
  const maxVisible = 7; // 最多显示7个页码
  
  if (totalPages.value <= maxVisible) {
    for (let i = 1; i <= totalPages.value; i++) {
      pages.push(i);
    }
  } else {
    // 总是显示第一页
    pages.push(1);
    
    if (props.current > 4) {
      pages.push('...');
    }
    
    // 显示当前页附近的页码
    const start = Math.max(2, props.current - 2);
    const end = Math.min(totalPages.value - 1, props.current + 2);
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    if (props.current < totalPages.value - 3) {
      pages.push('...');
    }
    
    // 总是显示最后一页
    if (totalPages.value > 1) {
      pages.push(totalPages.value);
    }
  }
  
  return pages;
});
</script>
