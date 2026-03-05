import { useState, useEffect } from 'react';
import { planCommunityService } from '../services/planCommunityService';
import type { PlanCommunity } from '../services/planCommunityService';

export default function PlanCommunities() {
  const [planCommunities, setPlanCommunities] = useState<PlanCommunity[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadPlanCommunities();
  }, []);

  const loadPlanCommunities = async () => {
    try {
      setLoading(true);
      const data = await planCommunityService.getAll();
      setPlanCommunities(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

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
        <h1 className="text-2xl font-bold text-text-light dark:text-text-dark">方案社区关联</h1>
        <button className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
          添加关联
        </button>
      </div>

      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <table className="w-full">
          <thead className="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">ID</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">方案ID</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">社区ID</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">道闸需求</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">框架需求</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase tracking-wider">操作</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
            {planCommunities.map((item) => (
              <tr key={item.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
                <td className="px-6 py-4 whitespace-nowrap text-sm text-text-light dark:text-text-dark">{item.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-text-light dark:text-text-dark">{item.planId}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{item.communityId}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{item.barrierRequiredQty}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-subtext-light dark:text-subtext-dark">{item.frameRequiredQty}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                  <button className="text-primary hover:text-primary/80 transition-colors">编辑</button>
                  <button className="text-red-500 hover:text-red-600 transition-colors">删除</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
