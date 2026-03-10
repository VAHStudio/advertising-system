import { ref, computed, watch, readonly } from 'vue';
import type { PageResult, BaseQueryParam } from '@/src/types/query';

interface UsePaginationOptions<T, Q extends BaseQueryParam> {
  fetchFn: (params: Q) => Promise<PageResult<T>>;
  defaultPageSize?: number;
  defaultParams?: Partial<Q>;
}

export function usePagination<T, Q extends BaseQueryParam>(options: UsePaginationOptions<T, Q>) {
  const { fetchFn, defaultPageSize = 10, defaultParams = {} } = options;

  const data = ref<T[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const currentPage = ref(1);
  const pageSize = ref(defaultPageSize);
  const total = ref(0);

  const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

  const fetchData = async () => {
    try {
      loading.value = true;
      error.value = null;

      const params = {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...defaultParams,
      } as Q;

      const result = await fetchFn(params);
      data.value = result.list || [];
      total.value = result.total || 0;
    } catch (err) {
      error.value = err instanceof Error ? err.message : '加载数据失败';
      console.error('Pagination error:', err);
    } finally {
      loading.value = false;
    }
  };

  const setPage = (page: number) => {
    if (page !== currentPage.value) {
      currentPage.value = page;
    }
  };

  const setPageSize = (size: number) => {
    if (size !== pageSize.value) {
      pageSize.value = size;
      currentPage.value = 1;
    }
  };

  const refresh = () => {
    fetchData();
  };

  // Watch for page changes
  watch([currentPage, pageSize], () => {
    fetchData();
  }, { immediate: true });

  return {
    data: readonly(data),
    loading: readonly(loading),
    error: readonly(error),
    currentPage: readonly(currentPage),
    pageSize: readonly(pageSize),
    total: readonly(total),
    totalPages,
    setPage,
    setPageSize,
    refresh,
  };
}

export default usePagination;
