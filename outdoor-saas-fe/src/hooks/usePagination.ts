import { useState, useEffect, useCallback, useRef } from 'react';
import type { PageResult, BaseQueryParam } from '../types/query';

interface PaginationState<T> {
  data: T[];
  loading: boolean;
  error: string | null;
  currentPage: number;
  pageSize: number;
  total: number;
}

interface PaginationActions {
  setPage: (page: number) => void;
  setPageSize: (size: number) => void;
  refresh: () => void;
}

interface UsePaginationOptions<T, P extends BaseQueryParam> {
  fetchFn: (params: P) => Promise<PageResult<T>>;
  defaultPageSize?: number;
  additionalParams?: Omit<P, 'pageNum' | 'pageSize'>;
}

/**
 * 通用分页 Hook
 * 用于管理分页列表的状态和逻辑
 * 
 * @example
 * const { data, loading, error, currentPage, pageSize, total, setPage, setPageSize, refresh } = usePagination({
 *   fetchFn: communityService.filterPage,
 *   defaultPageSize: 10
 * });
 */
export function usePagination<T, P extends BaseQueryParam>({
  fetchFn,
  defaultPageSize = 10,
  additionalParams,
}: UsePaginationOptions<T, P>): PaginationState<T> & PaginationActions {
  const [data, setData] = useState<T[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSizeState] = useState(defaultPageSize);
  const [total, setTotal] = useState(0);

  // 使用 ref 存储 additionalParams，避免对象引用变化导致无限循环
  const additionalParamsRef = useRef(additionalParams);
  additionalParamsRef.current = additionalParams;

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);

      const params = {
        ...additionalParamsRef.current,
        pageNum: currentPage,
        pageSize: pageSize,
      } as P;

      const result = await fetchFn(params);
      setData(result.list || []);
      setTotal(result.total || 0);
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
      setData([]);
      setTotal(0);
    } finally {
      setLoading(false);
    }
  }, [fetchFn, currentPage, pageSize]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const setPage = useCallback((page: number) => {
    setCurrentPage(page);
  }, []);

  const setPageSize = useCallback((size: number) => {
    setPageSizeState(size);
    setCurrentPage(1);
  }, []);

  const refresh = useCallback(() => {
    fetchData();
  }, [fetchData]);

  return {
    data,
    loading,
    error,
    currentPage,
    pageSize,
    total,
    setPage,
    setPageSize,
    refresh,
  };
}

export default usePagination;
