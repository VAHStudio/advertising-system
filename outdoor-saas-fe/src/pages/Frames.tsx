import { useNavigate } from 'react-router-dom';
import { frameService } from '../services/frameService';
import type { Frame } from '../services/frameService';
import type { FrameQueryParam } from '../types/query';
import Pagination from '../components/Pagination';
import { usePagination } from '../hooks/usePagination';

export default function Frames() {
  const navigate = useNavigate();
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

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-subtext-light dark:text-subtext-dark">加载中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-red-500">{error}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-text-light dark:text-text-dark">框架管理</h1>
        <button className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
          新增框架
        </button>
      </div>

      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">ID</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">框架编号</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">楼栋</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">单元</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">电梯</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
            {frames.map((frame) => (
              <tr
                key={frame.id}
                className="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors cursor-pointer"
                onClick={() => navigate(`/frames/${frame.id}`)}
              >
                <td className="px-6 py-4 whitespace-nowrap text-sm text-text-light dark:text-text-dark">{frame.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-text-light dark:text-text-dark">{frame.frameNo}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{frame.building}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{frame.unit}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{frame.elevator}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm space-x-2" onClick={(e) => e.stopPropagation()}>
                  <button
                    className="text-primary hover:text-primary/80 transition-colors"
                    onClick={() => navigate(`/frames/${frame.id}`)}
                  >
                    查看
                  </button>
                  <button className="text-primary hover:text-primary/80 transition-colors">编辑</button>
                  <button className="text-red-500 hover:text-red-600 transition-colors">删除</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* 分页组件 */}
        <Pagination
          current={currentPage}
          pageSize={pageSize}
          total={total}
          onChange={setPage}
          onPageSizeChange={setPageSize}
        />
      </div>
    </div>
  );
}
