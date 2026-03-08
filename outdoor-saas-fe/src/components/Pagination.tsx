import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
  current: number;
  pageSize: number;
  total: number;
  onChange: (page: number) => void;
  onPageSizeChange?: (pageSize: number) => void;
  pageSizeOptions?: number[];
}

export default function Pagination({
  current,
  pageSize,
  total,
  onChange,
  onPageSizeChange,
  pageSizeOptions = [10, 20, 50, 100]
}: PaginationProps) {
  const totalPages = Math.ceil(total / pageSize);
  
  // 计算要显示的页码
  const getPageNumbers = () => {
    const pages: (number | string)[] = [];
    const maxVisible = 7; // 最多显示7个页码
    
    if (totalPages <= maxVisible) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      // 总是显示第一页
      pages.push(1);
      
      if (current > 4) {
        pages.push('...');
      }
      
      // 显示当前页附近的页码
      const start = Math.max(2, current - 2);
      const end = Math.min(totalPages - 1, current + 2);
      
      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      
      if (current < totalPages - 3) {
        pages.push('...');
      }
      
      // 总是显示最后一页
      if (totalPages > 1) {
        pages.push(totalPages);
      }
    }
    
    return pages;
  };

  return (
    <div className="flex items-center justify-between px-6 py-4 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50">
      {/* 左侧：统计信息 */}
      <div className="text-sm text-subtext-light dark:text-subtext-dark">
        共 <span className="font-medium text-text-light dark:text-text-dark">{total}</span> 条记录
        {totalPages > 1 && (
          <span className="ml-2">
            第 <span className="font-medium text-text-light dark:text-text-dark">{current}</span> / {totalPages} 页
          </span>
        )}
      </div>

      {/* 中间：页码 */}
      <div className="flex items-center space-x-1">
        {/* 上一页 */}
        <button
          onClick={() => onChange(current - 1)}
          disabled={current <= 1}
          className="p-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200 dark:hover:bg-gray-700 text-subtext-light dark:text-subtext-dark"
        >
          <ChevronLeft size={18} />
        </button>

        {/* 页码按钮 */}
        {getPageNumbers().map((page, index) => (
          <div key={index}>
            {page === '...' ? (
              <span className="px-3 py-2 text-subtext-light dark:text-subtext-dark">...</span>
            ) : (
              <button
                onClick={() => onChange(page as number)}
                className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  current === page
                    ? 'bg-primary text-white'
                    : 'text-subtext-light dark:text-subtext-dark hover:bg-gray-200 dark:hover:bg-gray-700'
                }`}
              >
                {page}
              </button>
            )}
          </div>
        ))}

        {/* 下一页 */}
        <button
          onClick={() => onChange(current + 1)}
          disabled={current >= totalPages}
          className="p-2 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200 dark:hover:bg-gray-700 text-subtext-light dark:text-subtext-dark"
        >
          <ChevronRight size={18} />
        </button>
      </div>

      {/* 右侧：每页条数选择 */}
      {onPageSizeChange && (
        <div className="flex items-center space-x-2">
          <span className="text-sm text-subtext-light dark:text-subtext-dark">每页</span>
          <select
            value={pageSize}
            onChange={(e) => onPageSizeChange(Number(e.target.value))}
            className="px-2 py-1.5 text-sm rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-700 text-text-light dark:text-text-dark focus:outline-none focus:ring-2 focus:ring-primary"
          >
            {pageSizeOptions.map((size) => (
              <option key={size} value={size}>
                {size}
              </option>
            ))}
          </select>
          <span className="text-sm text-subtext-light dark:text-subtext-dark">条</span>
        </div>
      )}
    </div>
  );
}
