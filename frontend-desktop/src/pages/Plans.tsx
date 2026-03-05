import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { Icon } from '../components/Icon';
import { planService, Plan } from '../services/planService';

// --- Types ---
type PlanStatus = 'draft' | 'communicating' | 'pending' | 'signed';

interface DisplayPlan {
  id: string;
  title: string;
  customer: string;
  status: PlanStatus;
  budget: number;
  requirements: string;
  mediaTypes: string[];
  points: any[];
  mockups: string[];
  updatedAt: string;
}

const columns: { id: PlanStatus; title: string; color: string }[] = [
  { id: 'draft', title: '草稿', color: 'bg-slate-200 dark:bg-slate-700 text-slate-700 dark:text-slate-300' },
  { id: 'communicating', title: '沟通中', color: 'bg-blue-100 dark:bg-blue-900/50 text-blue-700 dark:text-blue-300' },
  { id: 'pending', title: '待确认', color: 'bg-amber-100 dark:bg-amber-900/50 text-amber-700 dark:text-amber-300' },
  { id: 'signed', title: '已签约', color: 'bg-emerald-100 dark:bg-emerald-900/50 text-emerald-700 dark:text-emerald-300' }
];

// Map release status to our status types
const mapStatus = (releaseStatus: number): PlanStatus => {
  switch (releaseStatus) {
    case 0: return 'draft';
    case 1: return 'communicating';
    case 2: return 'pending';
    case 3: return 'signed';
    default: return 'draft';
  }
};

export default function Plans() {
  const location = useLocation();
  const [selectedPlan, setSelectedPlan] = useState<DisplayPlan | null>(null);
  const [isMapMode, setIsMapMode] = useState(false);
  const [expandedPoints, setExpandedPoints] = useState<string[]>([]);
  const [plans, setPlans] = useState<DisplayPlan[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load real plans data
  useEffect(() => {
    loadPlansData();
  }, []);

  const loadPlansData = async () => {
    try {
      setLoading(true);
      const plansResponse = await planService.getAllPlans();
      const plansData = plansResponse;
      
      // Transform API data to display format
      const displayPlans: DisplayPlan[] = plansData.map(plan => ({
        id: plan.planNo,
        title: plan.planName,
        customer: plan.customer,
        status: mapStatus(plan.releaseStatus),
        budget: 500000, // Mock budget as it's not in the API
        requirements: plan.mediaRequirements || '暂无需求说明',
        mediaTypes: ['电梯框架', '道闸'], // Mock media types
        updatedAt: plan.updatedAt ? new Date(plan.updatedAt).toLocaleDateString('zh-CN') : new Date().toLocaleDateString('zh-CN'),
        points: [], // Will be populated when viewing details
        mockups: []
      }));
      
      setPlans(displayPlans);
      
      // Check location state for auto-open detail
      if (location.state?.autoOpenDetail && (location.state?.highlightPlanNo || location.state?.highlightPlanId)) {
        const planIdOrNo = location.state.highlightPlanNo || location.state.highlightPlanId;
        const targetPlan = displayPlans.find(p => p.id === planIdOrNo);
        if (targetPlan) {
          setSelectedPlan(targetPlan);
          setExpandedPoints(targetPlan.points.map(p => p.id));
          // Clear location state to prevent reopening on refresh
          window.history.replaceState({}, document.title);
        }
      }
    } catch (err) {
      setError('加载方案数据失败');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // --- Handlers ---
  const togglePointExpand = (pointId: string) => {
    setExpandedPoints(prev => 
      prev.includes(pointId) ? prev.filter(id => id !== pointId) : [...prev, pointId]
    );
  };

  const toggleAllPoints = () => {
    if (!selectedPlan) return;
    if (expandedPoints.length === selectedPlan.points.length) {
      setExpandedPoints([]);
    } else {
      setExpandedPoints(selectedPlan.points.map(p => p.id));
    }
  };

  // --- Render Kanban ---
  const renderKanban = () => (
    <div className="flex gap-6 h-full overflow-x-auto pb-4">
      {columns.map(col => {
        const columnPlans = plans.filter(p => p.status === col.id);
        return (
          <div key={col.id} className="flex-shrink-0 w-80 flex flex-col bg-slate-100/50 dark:bg-slate-800/20 rounded-2xl border border-border-light dark:border-border-dark overflow-hidden">
            <div className="p-4 border-b border-border-light dark:border-border-dark flex items-center justify-between bg-slate-100 dark:bg-slate-800/50">
              <div className="flex items-center gap-2">
                <span className={`w-2 h-2 rounded-full ${col.color.split(' ')[0]}`}></span>
                <h3 className="font-bold text-slate-800 dark:text-white">{col.title}</h3>
              </div>
              <span className="text-xs font-medium bg-white dark:bg-slate-700 px-2 py-1 rounded-full text-slate-500 dark:text-slate-400 shadow-sm">
                {columnPlans.length}
              </span>
            </div>
            <div className="flex-1 overflow-y-auto p-3 space-y-3 custom-scrollbar">
              {columnPlans.map(plan => (
                <div 
                  key={plan.id}
                  onClick={() => {
                    setSelectedPlan(plan);
                    setExpandedPoints(plan.points.map(p => p.id));
                  }}
                  className="bg-white dark:bg-surface-dark p-4 rounded-xl shadow-sm border border-border-light dark:border-border-dark cursor-pointer hover:shadow-md hover:border-primary transition-all group"
                >
                  <div className="flex justify-between items-start mb-2">
                    <span className="text-[10px] font-mono text-slate-400">{plan.id}</span>
                    <span className={`text-[10px] px-2 py-0.5 rounded-full font-medium ${col.color}`}>
                      {col.title}
                    </span>
                  </div>
                  <h4 className="font-bold text-slate-900 dark:text-white mb-1 group-hover:text-primary transition-colors">{plan.title}</h4>
                  <p className="text-xs text-slate-500 dark:text-slate-400 mb-3">{plan.customer}</p>
                  
                  <div className="flex flex-wrap gap-1 mb-3">
                    {plan.mediaTypes.map((type, idx) => (
                      <span key={idx} className="text-[10px] bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 px-1.5 py-0.5 rounded">
                        {type}
                      </span>
                    ))}
                  </div>
                  
                  <div className="flex justify-between items-center pt-3 border-t border-border-light dark:border-border-dark">
                    <span className="text-xs font-medium text-emerald-600 dark:text-emerald-400">¥ {(plan.budget / 10000).toFixed(1)}w</span>
                    <span className="text-[10px] text-slate-400">{plan.updatedAt}</span>
                  </div>
                </div>
              ))}
              {columnPlans.length === 0 && (
                <div className="h-24 flex items-center justify-center border-2 border-dashed border-border-light dark:border-border-dark rounded-xl text-slate-400 text-sm">
                  暂无方案
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );

  // --- Render Details ---
  const renderDetails = () => {
    if (!selectedPlan) return null;
    const isAllExpanded = expandedPoints.length === selectedPlan.points.length && selectedPlan.points.length > 0;

    return (
      <div className="max-w-5xl mx-auto space-y-6 pb-12">
        {/* Header Info */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark p-6">
          <div className="flex justify-between items-start mb-6">
            <div>
              <div className="flex items-center gap-3 mb-2">
                <h1 className="text-2xl font-bold text-slate-900 dark:text-white">{selectedPlan.title}</h1>
                <span className={`text-xs px-2.5 py-1 rounded-full font-medium ${columns.find(c => c.id === selectedPlan.status)?.color}`}>
                  {columns.find(c => c.id === selectedPlan.status)?.title}
                </span>
              </div>
              <p className="text-sm text-slate-500 dark:text-slate-400 font-mono">{selectedPlan.id} · 更新于 {selectedPlan.updatedAt}</p>
            </div>
            <button className="px-4 py-2 bg-primary hover:bg-blue-600 text-white rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
              <Icon name="edit" size={18} />
              编辑方案
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="space-y-1">
              <span className="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">客户</span>
              <p className="font-medium text-slate-900 dark:text-white flex items-center gap-2">
                <Icon name="business" className="text-slate-400" size={18} />
                {selectedPlan.customer}
              </p>
            </div>
            <div className="space-y-1">
              <span className="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">预算</span>
              <p className="font-medium text-emerald-600 dark:text-emerald-400 flex items-center gap-2">
                <Icon name="payments" className="text-emerald-500" size={18} />
                ¥ {selectedPlan.budget.toLocaleString()}
              </p>
            </div>
            <div className="space-y-1">
              <span className="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">媒体类型</span>
              <div className="flex flex-wrap gap-2 mt-1">
                {selectedPlan.mediaTypes.map((type, idx) => (
                  <span key={idx} className="text-xs bg-indigo-50 dark:bg-indigo-900/30 text-indigo-700 dark:text-indigo-300 px-2 py-1 rounded-md border border-indigo-100 dark:border-indigo-800/50">
                    {type}
                  </span>
                ))}
              </div>
            </div>
            <div className="md:col-span-3 space-y-1">
              <span className="text-xs text-slate-500 dark:text-slate-400 uppercase tracking-wider font-semibold">需求说明</span>
              <p className="text-sm text-slate-700 dark:text-slate-300 bg-slate-50 dark:bg-slate-800/50 p-4 rounded-xl border border-border-light dark:border-border-dark leading-relaxed">
                {selectedPlan.requirements}
              </p>
            </div>
          </div>
        </div>

        {/* Selected Points */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark overflow-hidden flex flex-col">
          <div className="p-4 border-b border-border-light dark:border-border-dark flex items-center justify-between bg-slate-50/50 dark:bg-slate-800/30">
            <h2 className="font-bold text-slate-900 dark:text-white flex items-center gap-2">
              <Icon name="place" className="text-primary" size={20} />
              选点列表 ({selectedPlan.points.length} 个点位)
            </h2>
            <div className="flex items-center gap-3">
              {selectedPlan.points.length > 0 && (
                <button 
                  onClick={toggleAllPoints}
                  className="text-sm text-primary hover:text-blue-700 font-medium transition-colors"
                >
                  {isAllExpanded ? '一键折叠' : '一键展开'}
                </button>
              )}
              <div className="flex items-center bg-slate-200 dark:bg-slate-700 p-1 rounded-lg">
                <button 
                  onClick={() => setIsMapMode(false)}
                  className={`px-3 py-1.5 rounded-md text-sm font-medium flex items-center gap-1.5 transition-colors ${!isMapMode ? 'bg-white dark:bg-slate-600 shadow-sm text-slate-900 dark:text-white' : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'}`}
                >
                  <Icon name="list" size={16} />
                  列表
                </button>
                <button 
                  onClick={() => setIsMapMode(true)}
                  className={`px-3 py-1.5 rounded-md text-sm font-medium flex items-center gap-1.5 transition-colors ${isMapMode ? 'bg-white dark:bg-slate-600 shadow-sm text-slate-900 dark:text-white' : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'}`}
                >
                  <Icon name="map" size={16} />
                  地图
                </button>
              </div>
            </div>
          </div>

          {isMapMode ? (
            <div className="h-[500px] relative bg-[#e5e7eb] dark:bg-[#1e293b]">
              <div className="w-full h-full flex items-center justify-center">
                <div className="text-center text-slate-400">
                  <Icon name="map" size={48} className="mb-4" />
                  <p>地图模式预览 (共 {selectedPlan.points.length} 个标记点)</p>
                </div>
              </div>
            </div>
          ) : (
            <div className="divide-y divide-border-light dark:divide-border-dark">
              {selectedPlan.points.length === 0 ? (
                <div className="p-8 text-center text-slate-500 dark:text-slate-400">
                  暂无选点数据
                </div>
              ) : (
                selectedPlan.points.map((point: any) => {
                  const isExpanded = expandedPoints.includes(point.id);
                  return (
                    <div key={point.id} className="bg-white dark:bg-surface-dark">
                      <div 
                        className="p-4 flex items-center justify-between cursor-pointer hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors"
                        onClick={() => togglePointExpand(point.id)}
                      >
                        <div className="flex items-center gap-3">
                          <Icon name="chevron_right" className={`text-slate-400 transition-transform duration-200 ${isExpanded ? 'rotate-90' : ''}`} size={20} />
                          <div>
                            <h3 className="font-bold text-slate-900 dark:text-white text-sm">{point.name}</h3>
                            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">{point.location} · 包含 {point.slots?.length || 0} 个广告位</p>
                          </div>
                        </div>
                        <span className="text-xs font-mono text-slate-400">{point.id}</span>
                      </div>
                      
                      {isExpanded && (
                        <div className="bg-slate-50 dark:bg-slate-800/30 border-t border-border-light dark:border-border-dark p-4 pl-12">
                          <table className="w-full text-left">
                            <thead>
                              <tr className="text-xs uppercase tracking-wider text-slate-500 dark:text-slate-400 border-b border-border-light dark:border-border-dark">
                                <th className="pb-2 font-medium">广告位编号</th>
                                <th className="pb-2 font-medium">广告位名称</th>
                                <th className="pb-2 font-medium">状态</th>
                                <th className="pb-2 font-medium text-right">单价</th>
                              </tr>
                            </thead>
                            <tbody className="divide-y divide-border-light dark:divide-border-dark">
                              {point.slots?.map((slot: any) => (
                                <tr key={slot.id} className="text-sm">
                                  <td className="py-3 font-mono text-xs text-slate-500">{slot.id}</td>
                                  <td className="py-3 font-medium text-slate-800 dark:text-slate-200">{slot.name}</td>
                                  <td className="py-3">
                                    <span className="inline-flex items-center px-2 py-0.5 rounded text-[10px] font-medium bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400">
                                      可售
                                    </span>
                                  </td>
                                  <td className="py-3 text-right text-slate-600 dark:text-slate-300">¥ {slot.price?.toLocaleString()}</td>
                                </tr>
                              ))}
                            </tbody>
                          </table>
                        </div>
                      )}
                    </div>
                  );
                })
              )}
            </div>
          )}
        </div>

        {/* Mockups */}
        <div className="bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark p-6">
          <h2 className="font-bold text-slate-900 dark:text-white flex items-center gap-2 mb-4">
            <Icon name="image" className="text-primary" size={20} />
            设计小样图
          </h2>
          {selectedPlan.mockups.length === 0 ? (
            <div className="h-32 flex items-center justify-center border-2 border-dashed border-border-light dark:border-border-dark rounded-xl text-slate-400 text-sm">
              暂无设计图
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {selectedPlan.mockups.map((url, idx) => (
                <div key={idx} className="aspect-video rounded-lg overflow-hidden border border-border-light dark:border-border-dark group relative cursor-pointer">
                  <img src={url} alt={`Mockup ${idx + 1}`} className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" referrerPolicy="no-referrer" />
                  <div className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors flex items-center justify-center">
                    <Icon name="zoom_in" className="text-white" size={20} />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-slate-500 dark:text-slate-400">加载方案数据中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative items-center justify-center">
        <div className="text-center">
          <p className="text-red-500 mb-4">{error}</p>
          <button 
            onClick={loadPlansData}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition-colors"
          >
            重新加载
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <h1 className="text-xl font-bold text-slate-900 dark:text-white">广告方案管理</h1>
        {selectedPlan ? (
          <button 
            onClick={() => setSelectedPlan(null)}
            className="px-3 py-1.5 text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg text-sm font-medium transition-colors flex items-center gap-1.5"
          >
            <Icon name="arrow_back" size={18} />
            返回看板
          </button>
        ) : (
          <button className="px-4 py-2 bg-primary hover:bg-blue-600 text-white rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
            <Icon name="add" size={18} />
            创建方案
          </button>
        )}
      </div>

      <div className="flex-1 overflow-hidden flex flex-col">
        <div className="flex-1 overflow-y-auto custom-scrollbar p-6">
          {selectedPlan ? renderDetails() : renderKanban()}
        </div>
      </div>
    </div>
  );
}
