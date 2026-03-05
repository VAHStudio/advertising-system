import { Icon } from '../components/Icon'
import React, { useState } from 'react';

// --- Types ---
interface Task {
  id: string;
  customer: string;
  campaign: string;
  pointCount: number;
  location: string;
  planDate: string;
  deadline: string;
  status: 'pending' | 'executing' | 'reviewing' | 'completed';
  progress: number;
  thumbnail: string;
  // Detail fields
  points?: { name: string, media: string, status: string, executor: string }[];
}

interface ReviewPhoto {
  id: string;
  url: string;
  pointName: string;
  slotName: string;
  aiStatus: 'pass' | 'reject' | 'warning';
  aiOpinion: string;
  submitter: string;
  submitTime: string;
}

// --- Mock Data ---
const mockTasks: Task[] = [
  {
    id: 'PUB-240701', customer: '星空传媒', campaign: '夏季大促主视觉', pointCount: 24, location: '市中心区域',
    planDate: '2024.07.05', deadline: '18:00 前完成', status: 'executing', progress: 50,
    thumbnail: 'https://images.unsplash.com/photo-1542204165-65bf26472b9b?auto=format&fit=crop&w=100&q=80',
    points: [{ name: '翠园社区', media: '电梯框架', status: '已完成', executor: '张师傅' }, { name: '阳光小区', media: '电梯框架', status: '执行中', executor: '李师傅' }]
  },
  {
    id: 'PUB-240702', customer: '绿生活零售', campaign: '品牌焕新A版', pointCount: 8, location: '科技园区域',
    planDate: '2024.07.06', deadline: '12:00 前完成', status: 'pending', progress: 0,
    thumbnail: '',
    points: [{ name: '科技园南区', media: '道闸', status: '待执行', executor: '王师傅' }]
  },
  {
    id: 'PUB-240698', customer: '海洋广告', campaign: '新车发布预热', pointCount: 15, location: '全城高速路段',
    planDate: '2024.07.04', deadline: '已逾期 1 天', status: 'reviewing', progress: 100,
    thumbnail: 'https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=100&q=80',
    points: [{ name: '滨海大道跨线桥', media: '户外大牌', status: '待审核', executor: '张师傅' }]
  }
];

const mockPhotos: ReviewPhoto[] = [
  { id: 'P-001', url: 'https://images.unsplash.com/photo-1542204165-65bf26472b9b?auto=format&fit=crop&w=400&q=80', pointName: '市中心商业街', slotName: '入口主屏 A', aiStatus: 'pass', aiOpinion: '画面清晰，色彩正常，无遮挡。', submitter: '张师傅', submitTime: '今天 10:23' },
  { id: 'P-002', url: 'https://images.unsplash.com/photo-1559136555-9303baea8ebd?auto=format&fit=crop&w=400&q=80', pointName: '市中心商业街', slotName: '广场侧屏 B', aiStatus: 'warning', aiOpinion: '画面略微反光，但不影响整体识别。', submitter: '张师傅', submitTime: '今天 10:45' },
];

export default function Execution() {
  const [reviewMode, setReviewMode] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [selectedPhotos, setSelectedPhotos] = useState<string[]>([]);
  const [filterAiStatus, setFilterAiStatus] = useState<'all' | 'pass' | 'reject' | 'warning'>('all');
  const [selectedTaskDetail, setSelectedTaskDetail] = useState<Task | null>(null);

  // --- Handlers ---
  const handleOpenReview = (task: Task) => {
    setSelectedTask(task);
    setReviewMode(true);
    setSelectedPhotos([]);
    setFilterAiStatus('all');
  };

  const handleCloseReview = () => {
    setReviewMode(false);
    setSelectedTask(null);
  };

  const togglePhotoSelection = (id: string) => {
    setSelectedPhotos(prev => prev.includes(id) ? prev.filter(pId => pId !== id) : [...prev, id]);
  };

  const toggleAllPhotos = () => {
    const filteredPhotos = mockPhotos.filter(p => filterAiStatus === 'all' || p.aiStatus === filterAiStatus);
    if (selectedPhotos.length === filteredPhotos.length) {
      setSelectedPhotos([]);
    } else {
      setSelectedPhotos(filteredPhotos.map(p => p.id));
    }
  };

  // --- Render List ---
  const renderList = () => (
    <div className="flex-1 overflow-hidden flex relative">
      <div className="flex-1 overflow-y-auto custom-scrollbar p-6">
        <div className="max-w-7xl mx-auto space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="bg-surface-light dark:bg-surface-dark p-4 rounded-xl border border-border-light dark:border-border-dark shadow-sm">
              <div className="text-sm text-slate-500 dark:text-slate-400 mb-1">待执行</div>
              <div className="text-2xl font-bold text-slate-900 dark:text-white">24</div>
            </div>
            <div className="bg-surface-light dark:bg-surface-dark p-4 rounded-xl border border-border-light dark:border-border-dark shadow-sm">
              <div className="text-sm text-slate-500 dark:text-slate-400 mb-1">执行中</div>
              <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">12</div>
            </div>
            <div className="bg-surface-light dark:bg-surface-dark p-4 rounded-xl border border-border-light dark:border-border-dark shadow-sm">
              <div className="text-sm text-slate-500 dark:text-slate-400 mb-1">待审核</div>
              <div className="text-2xl font-bold text-orange-500 dark:text-orange-400">8</div>
            </div>
            <div className="bg-surface-light dark:bg-surface-dark p-4 rounded-xl border border-border-light dark:border-border-dark shadow-sm">
              <div className="text-sm text-slate-500 dark:text-slate-400 mb-1">今日完成</div>
              <div className="text-2xl font-bold text-emerald-500 dark:text-emerald-400">45</div>
            </div>
          </div>

          <div className="bg-surface-light dark:bg-surface-dark rounded-xl border border-border-light dark:border-border-dark shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-600 dark:text-slate-400">
                <thead className="bg-slate-50 dark:bg-slate-800/50 text-xs uppercase font-semibold text-slate-500 dark:text-slate-400">
                  <tr>
                    <th className="px-6 py-4">上刊单号 / 客户</th>
                    <th className="px-6 py-4">画面内容</th>
                    <th className="px-6 py-4">点位数量</th>
                    <th className="px-6 py-4">计划时间</th>
                    <th className="px-6 py-4">进度状态</th>
                    <th className="px-6 py-4 text-right">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border-light dark:divide-border-dark">
                  {mockTasks.map(task => (
                    <tr key={task.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors group">
                      <td className="px-6 py-4">
                        <div className="font-medium text-slate-900 dark:text-white">#{task.id}</div>
                        <div className="text-xs text-slate-500 mt-0.5">{task.customer}</div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded bg-slate-100 dark:bg-slate-800 overflow-hidden border border-border-light dark:border-border-dark flex items-center justify-center text-slate-400">
                            {task.thumbnail ? (
                              <img alt="Ad creative thumbnail" className="w-full h-full object-cover" src={task.thumbnail} referrerPolicy="no-referrer" />
                            ) : (
                              <Icon name="image" size={20} />
                            )}
                          </div>
                          <span className="text-xs font-medium">{task.campaign}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="font-medium text-slate-900 dark:text-white">{task.pointCount} 个</div>
                        <div className="text-xs text-slate-500">{task.location}</div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="text-slate-900 dark:text-white">{task.planDate}</div>
                        <div className={`text-xs ${task.deadline.includes('逾期') ? 'text-red-500' : 'text-slate-500'}`}>{task.deadline}</div>
                      </td>
                      <td className="px-6 py-4">
                        <div className="w-full max-w-[120px]">
                          <div className="flex justify-between text-xs mb-1">
                            <span className={`font-medium ${
                              task.status === 'executing' ? 'text-blue-600 dark:text-blue-400' :
                              task.status === 'reviewing' ? 'text-orange-500 dark:text-orange-400' :
                              'text-slate-500 dark:text-slate-400'
                            }`}>
                              {task.status === 'executing' ? '执行中' : task.status === 'reviewing' ? '待审核' : '待执行'}
                            </span>
                            <span className="text-slate-500">{Math.round(task.pointCount * (task.progress / 100))}/{task.pointCount}</span>
                          </div>
                          <div className="w-full bg-slate-200 dark:bg-slate-700 rounded-full h-1.5">
                            <div className={`h-1.5 rounded-full ${
                              task.status === 'executing' ? 'bg-blue-500' :
                              task.status === 'reviewing' ? 'bg-orange-500' :
                              'bg-slate-400'
                            }`} style={{ width: `${task.progress}%` }}></div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-right">
                        {task.status === 'reviewing' ? (
                          <button 
                            onClick={() => handleOpenReview(task)}
                            className="px-3 py-1.5 bg-orange-100 text-orange-700 hover:bg-orange-200 dark:bg-orange-900/30 dark:text-orange-400 dark:hover:bg-orange-900/50 rounded-lg text-sm font-medium transition-colors"
                          >
                            批量审核
                          </button>
                        ) : (
                          <button onClick={() => setSelectedTaskDetail(task)} className="text-primary hover:text-blue-700 dark:hover:text-blue-400 text-sm font-medium">查看详情</button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {/* Detail Drawer */}
      <div className={`absolute inset-y-0 right-0 w-[450px] bg-white dark:bg-surface-dark shadow-2xl border-l border-border-light dark:border-border-dark transform transition-transform duration-300 ease-in-out flex flex-col z-20 ${selectedTaskDetail ? 'translate-x-0' : 'translate-x-full'}`}>
        <div className="p-4 border-b border-border-light dark:border-border-dark flex justify-between items-center bg-slate-50 dark:bg-slate-800/50">
          <h2 className="font-bold text-lg text-slate-900 dark:text-white flex items-center gap-2">
            <Icon name="assignment" className="text-primary" size={20} />
            上刊任务详情
          </h2>
          <button onClick={() => setSelectedTaskDetail(null)} className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-md hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">
            <Icon name="close" size={20} />
          </button>
        </div>
        
        {selectedTaskDetail && (
          <div className="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-6">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <span className={`inline-flex items-center px-2 py-0.5 rounded text-[10px] font-medium ${
                  selectedTaskDetail.status === 'executing' ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' :
                  selectedTaskDetail.status === 'reviewing' ? 'bg-orange-100 text-orange-700 dark:bg-orange-900/30 dark:text-orange-400' :
                  'bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300'
                }`}>
                  {selectedTaskDetail.status === 'executing' ? '执行中' : selectedTaskDetail.status === 'reviewing' ? '待审核' : '待执行'}
                </span>
                <span className="text-xs font-mono text-slate-500">#{selectedTaskDetail.id}</span>
              </div>
              <h3 className="text-2xl font-bold text-slate-900 dark:text-white mb-1">{selectedTaskDetail.customer}</h3>
              <p className="text-sm text-primary font-medium">{selectedTaskDetail.campaign}</p>
            </div>

            <div className="grid grid-cols-2 gap-4 bg-slate-50 dark:bg-slate-800/30 p-4 rounded-xl border border-border-light dark:border-border-dark">
              <div>
                <div className="text-xs text-slate-500 mb-1">计划时间</div>
                <div className="font-medium text-slate-900 dark:text-white font-mono">{selectedTaskDetail.planDate}</div>
              </div>
              <div>
                <div className="text-xs text-slate-500 mb-1">截止时间</div>
                <div className={`font-medium ${selectedTaskDetail.deadline.includes('逾期') ? 'text-red-500' : 'text-slate-900 dark:text-white'} font-mono`}>{selectedTaskDetail.deadline}</div>
              </div>
              <div>
                <div className="text-xs text-slate-500 mb-1">点位数量</div>
                <div className="font-medium text-slate-900 dark:text-white">{selectedTaskDetail.pointCount} 个</div>
              </div>
              <div>
                <div className="text-xs text-slate-500 mb-1">区域</div>
                <div className="font-medium text-slate-900 dark:text-white">{selectedTaskDetail.location}</div>
              </div>
            </div>

            <div>
              <h4 className="text-sm font-semibold text-slate-900 dark:text-white mb-3 flex items-center gap-2">
                <Icon name="format_list_bulleted" className="text-slate-400" size={18} />
                执行点位清单
              </h4>
              <div className="space-y-2">
                {selectedTaskDetail.points?.map((pt, idx) => (
                  <div key={idx} className="bg-white dark:bg-slate-800 p-3 rounded-lg border border-border-light dark:border-border-dark flex justify-between items-center">
                    <div>
                      <div className="font-medium text-sm text-slate-900 dark:text-white">{pt.name}</div>
                      <div className="text-xs text-slate-500">{pt.media} · 执行人: {pt.executor}</div>
                    </div>
                    <div className={`text-xs font-bold ${pt.status === '已完成' ? 'text-emerald-500' : pt.status === '执行中' ? 'text-blue-500' : 'text-slate-400'}`}>
                      {pt.status}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
      {selectedTaskDetail && <div className="absolute inset-0 bg-black/20 dark:bg-black/40 z-10 md:hidden" onClick={() => setSelectedTaskDetail(null)} />}
    </div>
  );

  // --- Render Batch Review (Simplified for brevity, same as before) ---
  const renderBatchReview = () => {
    if (!selectedTask) return null;
    return (
      <div className="flex-1 flex flex-col bg-slate-50 dark:bg-[#0B1120] overflow-hidden">
        <div className="bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark p-4 flex items-center justify-between flex-shrink-0 shadow-sm z-10">
          <div className="flex items-center gap-4">
            <button onClick={handleCloseReview} className="p-2 text-slate-500 hover:text-slate-800 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors">
              <Icon name="arrow_back" size={20} />
            </button>
            <div>
              <h2 className="font-bold text-lg text-slate-900 dark:text-white flex items-center gap-2">批量照片审核</h2>
            </div>
          </div>
        </div>
        <div className="flex-1 overflow-y-auto custom-scrollbar p-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {mockPhotos.map(photo => (
              <div key={photo.id} className="bg-white dark:bg-surface-dark rounded-xl shadow-sm border-2 border-border-light dark:border-border-dark overflow-hidden flex flex-col">
                <div className="relative aspect-[4/3] bg-slate-100 dark:bg-slate-800">
                  <img src={photo.url} alt={photo.slotName} className="w-full h-full object-cover" referrerPolicy="no-referrer" />
                </div>
                <div className="p-4 flex-1 flex flex-col">
                  <h3 className="font-bold text-slate-900 dark:text-white text-sm truncate">{photo.pointName}</h3>
                  <p className="text-xs text-slate-500 dark:text-slate-400 truncate mt-0.5">{photo.slotName}</p>
                  <div className="mt-auto p-3 rounded-lg border text-xs leading-relaxed border-emerald-500 bg-emerald-50 dark:bg-emerald-900/20 text-emerald-700 dark:text-emerald-400">
                    AI意见: {photo.aiOpinion}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  };

  return (
    <div className="flex-1 flex flex-col min-w-0 relative">
      {!reviewMode && (
        <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
          <div>
            <h1 className="text-xl font-bold text-slate-900 dark:text-white">上刊执行管理</h1>
            <p className="text-sm text-slate-500 dark:text-slate-400">跟踪广告画面的安装进度与监播反馈。</p>
          </div>
          <button className="inline-flex items-center gap-2 px-4 py-2 bg-primary hover:bg-blue-600 text-white rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors">
            <Icon name="add" size={18} />
            新建上刊单
          </button>
        </div>
      )}
      {reviewMode ? renderBatchReview() : renderList()}
    </div>
  );
}
