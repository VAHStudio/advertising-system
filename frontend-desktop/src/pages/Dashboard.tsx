import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { dashboardService, DashboardStats } from '../services/dashboardService';
import { Icon } from '../components/Icon';

export default function Dashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const data = await dashboardService.getStats();
      setStats(data);
    } catch (err) {
      setError('加载数据失败');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-subtext-light dark:text-subtext-dark">加载数据中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-center">
          <p className="text-red-500 mb-4">{error}</p>
          <button 
            onClick={loadDashboardData}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition-colors"
          >
            重新加载
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="p-8 max-w-7xl mx-auto space-y-8 pb-24">
          <section>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-bold text-text-light dark:text-text-dark flex items-center gap-2">
                <Icon name="bolt" className="text-accent" size={20} /> 快捷操作
              </h3>
              <button className="text-sm text-primary hover:text-primary-dark font-medium">编辑快捷方式</button>
            </div>
            <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-5 gap-4">
              <button onClick={() => navigate('/customers')} className="flex flex-col items-center justify-center p-4 bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 hover:shadow-md hover:border-primary/50 dark:hover:border-primary/50 transition-all group">
                <div className="w-10 h-10 rounded-full bg-blue-50 dark:bg-blue-900/30 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                  <Icon name="add_box" className="text-primary" size={20} />
                </div>
                <span className="text-sm font-medium text-text-light dark:text-text-dark">新建客户</span>
              </button>
              <button onClick={() => navigate('/plans')} className="flex flex-col items-center justify-center p-4 bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 hover:shadow-md hover:border-primary/50 dark:hover:border-primary/50 transition-all group">
                <div className="w-10 h-10 rounded-full bg-purple-50 dark:bg-purple-900/30 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                  <Icon name="auto_fix_high" className="text-accent" size={20} />
                </div>
                <span className="text-sm font-medium text-text-light dark:text-text-dark">生成方案</span>
              </button>
              <button onClick={() => navigate('/inventory')} className="flex flex-col items-center justify-center p-4 bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 hover:shadow-md hover:border-primary/50 dark:hover:border-primary/50 transition-all group">
                <div className="w-10 h-10 rounded-full bg-green-50 dark:bg-green-900/30 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                  <Icon name="map" className="text-green-500" size={20} />
                </div>
                <span className="text-sm font-medium text-text-light dark:text-text-dark">查看档期</span>
              </button>
              <button onClick={() => navigate('/sales-contracts')} className="flex flex-col items-center justify-center p-4 bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 hover:shadow-md hover:border-primary/50 dark:hover:border-primary/50 transition-all group">
                <div className="w-10 h-10 rounded-full bg-orange-50 dark:bg-orange-900/30 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                  <Icon name="assignment_turned_in" className="text-orange-500" size={20} />
                </div>
                <span className="text-sm font-medium text-text-light dark:text-text-dark">起草合同</span>
              </button>
              <button onClick={() => navigate('/execution')} className="flex flex-col items-center justify-center p-4 bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 hover:shadow-md hover:border-primary/50 dark:hover:border-primary/50 transition-all group">
                <div className="w-10 h-10 rounded-full bg-pink-50 dark:bg-pink-900/30 flex items-center justify-center mb-2 group-hover:scale-110 transition-transform">
                  <Icon name="camera_alt" className="text-pink-500" size={20} />
                </div>
                <span className="text-sm font-medium text-text-light dark:text-text-dark">申请上刊</span>
              </button>
            </div>
          </section>

          <section>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-bold text-text-light dark:text-text-dark flex items-center gap-2">
                <Icon name="analytics" className="text-primary" size={20} /> 数据概览
              </h3>
              <div className="flex items-center gap-2 bg-white dark:bg-gray-800 rounded-lg p-1 border border-gray-200 dark:border-gray-700">
                <button className="px-3 py-1 text-xs font-medium rounded bg-gray-100 dark:bg-gray-700 text-text-light dark:text-text-dark">本周</button>
                <button className="px-3 py-1 text-xs font-medium rounded text-subtext-light dark:text-subtext-dark hover:text-primary">本月</button>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <p className="text-sm text-subtext-light dark:text-subtext-dark font-medium">可用资源位</p>
                    <h4 className="text-3xl font-display font-bold text-text-light dark:text-text-dark mt-1">{stats?.availableResources?.toLocaleString() || 0}</h4>
                  </div>
                  <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400">
                    <Icon name="trending_up" size={14} className="mr-1" /> +4.5%
                  </span>
                </div>
                <div className="h-2 w-full bg-gray-100 dark:bg-gray-700 rounded-full overflow-hidden">
                  <div className="h-full bg-primary w-[75%] rounded-full"></div>
                </div>
                <p className="text-xs text-subtext-light dark:text-subtext-dark mt-2">{stats?.resourceUtilization || 75}% 入驻率</p>
              </div>

              <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <p className="text-sm text-subtext-light dark:text-subtext-dark font-medium">本月营收</p>
                    <h4 className="text-3xl font-display font-bold text-text-light dark:text-text-dark mt-1">¥{(stats?.monthlyRevenue || 0).toLocaleString()}</h4>
                  </div>
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${(stats?.revenueChange || 0) >= 0 ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400'}`}>
                    <Icon name={(stats?.revenueChange || 0) >= 0 ? 'trending_up' : 'trending_down'} size={14} className="mr-1" /> {(stats?.revenueChange || 0) >= 0 ? '+' : ''}{stats?.revenueChange || -2.1}%
                  </span>
                </div>
                <div className="flex items-end space-x-2 h-10 mt-2">
                  <div className="w-1/6 bg-accent/30 dark:bg-accent/20 rounded-t h-[40%]"></div>
                  <div className="w-1/6 bg-accent/40 dark:bg-accent/30 rounded-t h-[60%]"></div>
                  <div className="w-1/6 bg-accent/60 dark:bg-accent/40 rounded-t h-[30%]"></div>
                  <div className="w-1/6 bg-accent/80 dark:bg-accent/50 rounded-t h-[80%]"></div>
                  <div className="w-1/6 bg-accent dark:bg-accent/70 rounded-t h-[50%]"></div>
                  <div className="w-1/6 bg-accent dark:bg-accent/90 rounded-t h-[75%]"></div>
                </div>
              </div>

              <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <p className="text-sm text-subtext-light dark:text-subtext-dark font-medium">待办事项</p>
                    <h4 className="text-3xl font-display font-bold text-text-light dark:text-text-dark mt-1">{stats?.pendingTasks || 0}</h4>
                  </div>
                  <div className="w-8 h-8 rounded-full bg-orange-100 dark:bg-orange-900/30 flex items-center justify-center text-orange-600 dark:text-orange-400">
                    <Icon name="priority_high" size={20} />
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-2 mt-2">
                  <div className="bg-gray-50 dark:bg-gray-800 rounded p-2 text-center">
                    <span className="block text-xs text-subtext-light dark:text-subtext-dark">活跃方案</span>
                    <span className="block text-lg font-bold text-text-light dark:text-text-dark">{stats?.activePlans || 0}</span>
                  </div>
                  <div className="bg-gray-50 dark:bg-gray-800 rounded p-2 text-center">
                    <span className="block text-xs text-subtext-light dark:text-subtext-dark">总社区</span>
                    <span className="block text-lg font-bold text-text-light dark:text-text-dark">{stats?.totalCommunities || 0}</span>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 bg-surface-light dark:bg-surface-dark rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 overflow-hidden">
              <div className="px-6 py-4 border-b border-gray-100 dark:border-gray-700 flex justify-between items-center bg-gray-50/50 dark:bg-gray-800/30">
                <h3 className="font-bold text-text-light dark:text-text-dark">最近方案</h3>
                <a className="text-sm text-primary hover:underline" href="#/plans">查看全部</a>
              </div>
              <div className="divide-y divide-gray-100 dark:divide-gray-700">
                {stats?.recentPlans?.length > 0 ? (
                  stats.recentPlans.map((plan, index) => (
                    <div key={plan.id || index} className="p-4 hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors flex items-center gap-4">
                      <div className="flex-shrink-0 w-10 h-10 rounded-full bg-blue-100 dark:bg-blue-900/30 flex items-center justify-center text-blue-600 dark:text-blue-400">
                        <Icon name="description" size={20} />
                      </div>
                      <div className="flex-1">
                        <h4 className="text-sm font-semibold text-text-light dark:text-text-dark">{plan.planName}</h4>
                        <p className="text-xs text-subtext-light dark:text-subtext-dark mt-1">客户: {plan.customer}</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className={`text-xs font-medium px-2 py-1 rounded ${plan.status === '进行中' ? 'bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-400' : plan.status === '已完成' ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400' : 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'}`}>{plan.status}</span>
                        <button className="text-subtext-light hover:text-primary dark:text-subtext-dark">
                          <Icon name="chevron_right" size={20} />
                        </button>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="p-8 text-center text-subtext-light dark:text-subtext-dark">
                    暂无方案数据
                  </div>
                )}
              </div>
            </div>

            <div className="lg:col-span-1 bg-gradient-to-br from-primary to-accent text-white rounded-2xl shadow-lg p-6 relative overflow-hidden">
              <div className="absolute top-0 right-0 -mr-8 -mt-8 w-32 h-32 rounded-full bg-white opacity-10"></div>
              <div className="absolute bottom-0 left-0 -ml-8 -mb-8 w-24 h-24 rounded-full bg-white opacity-10"></div>
              <div className="flex items-center gap-2 mb-4">
                <Icon name="auto_awesome" size={20} />
                <h3 className="font-bold text-lg">资源统计</h3>
              </div>
              <div className="space-y-3 text-sm text-blue-50">
                <div className="flex justify-between">
                  <span>总社区数:</span>
                  <span className="font-semibold">{stats?.totalCommunities || 0}</span>
                </div>
                <div className="flex justify-between">
                  <span>总道闸数:</span>
                  <span className="font-semibold">{stats?.totalBarrierGates || 0}</span>
                </div>
                <div className="flex justify-between">
                  <span>总框架数:</span>
                  <span className="font-semibold">{stats?.totalFrames || 0}</span>
                </div>
                <div className="flex justify-between">
                  <span>可用资源:</span>
                  <span className="font-semibold">{stats?.availableResources || 0}</span>
                </div>
              </div>
              <button 
                onClick={() => navigate('/resources')}
                className="w-full mt-6 py-2.5 bg-white text-primary font-semibold rounded-lg hover:bg-blue-50 transition-colors shadow-sm flex items-center justify-center gap-2"
              >
                <span>查看资源详情</span>
                <Icon name="arrow_forward" size={16} />
              </button>
            </div>
          </section>
        </div>
  );
}
