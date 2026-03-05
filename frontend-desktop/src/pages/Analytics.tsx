import { Icon } from '../components/Icon'
import React, { useState } from 'react';
import { 
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, 
  BarChart, Bar, Legend, LineChart, Line, ComposedChart, ScatterChart, Scatter, ZAxis,
  FunnelChart, Funnel, LabelList, Cell
} from 'recharts';

const reportOptions = [
  { id: 'overview', name: '数据概览 (默认)' },
  { id: 'publish_rate', name: '各类媒体上刊率' },
  { id: 'contract_amortization', name: '合同摊销和及时回款率' },
  { id: 'worker_stats', name: '上刊工人计件统计' },
  { id: 'sales_funnel', name: '销售漏斗' },
  { id: 'roi', name: '资源点位投资回报率ROI' },
  { id: 'revenue_amortization', name: '资源点位收入摊销报表' },
];

// --- Mock Data ---
const revenueData = [
  { name: '1月', value: 4000 }, { name: '2月', value: 3000 }, { name: '3月', value: 5000 },
  { name: '4月', value: 4500 }, { name: '5月', value: 6000 }, { name: '6月', value: 5500 },
  { name: '7月', value: 7000 },
];

const occupancyData = [
  { name: '市中心', led: 85, lightbox: 90, billboard: 75 },
  { name: '科技园', led: 95, lightbox: 80, billboard: 60 },
  { name: 'A区郊外', led: 40, lightbox: 50, billboard: 85 },
  { name: '高速路段', led: 60, lightbox: 30, billboard: 95 },
];

const publishRateData = [
  { name: '电梯框架', rate: 85, fill: '#3B82F6' },
  { name: '道闸', rate: 72, fill: '#8B5CF6' },
  { name: '户外大牌', rate: 92, fill: '#10B981' },
  { name: '公交车身', rate: 65, fill: '#F59E0B' },
  { name: '地铁灯箱', rate: 88, fill: '#EC4899' },
];

const contractAmortizationData = [
  { month: '1月', amortization: 120, collectionRate: 95 },
  { month: '2月', amortization: 150, collectionRate: 92 },
  { month: '3月', amortization: 180, collectionRate: 88 },
  { month: '4月', amortization: 160, collectionRate: 96 },
  { month: '5月', amortization: 210, collectionRate: 90 },
  { month: '6月', amortization: 240, collectionRate: 94 },
];

const workerStatsData = [
  { name: '张师傅', tasks: 145, points: 420 },
  { name: '李师傅', tasks: 120, points: 380 },
  { name: '王师傅', tasks: 160, points: 510 },
  { name: '赵师傅', tasks: 90, points: 260 },
  { name: '刘师傅', tasks: 110, points: 310 },
];

const funnelData = [
  { value: 1000, name: '潜在客户', fill: '#BFDBFE' },
  { value: 800, name: '初步沟通', fill: '#93C5FD' },
  { value: 500, name: '方案报价', fill: '#60A5FA' },
  { value: 200, name: '合同谈判', fill: '#3B82F6' },
  { value: 150, name: '赢单', fill: '#1D4ED8' },
];

const roiData = [
  { name: '市中心大牌A', investment: 50, return: 120, roi: 240 },
  { name: '科技园道闸组', investment: 30, return: 80, roi: 266 },
  { name: '翠园电梯框架', investment: 20, return: 45, roi: 225 },
  { name: '高速跨线桥', investment: 80, return: 150, roi: 187 },
  { name: '地铁1号线', investment: 100, return: 190, roi: 190 },
];

const revenueAmortizationData = [
  { month: '1月', revenue: 450, cost: 200 },
  { month: '2月', revenue: 520, cost: 210 },
  { month: '3月', revenue: 480, cost: 205 },
  { month: '4月', revenue: 610, cost: 220 },
  { month: '5月', revenue: 590, cost: 215 },
  { month: '6月', revenue: 680, cost: 230 },
];

export default function Analytics() {
  const [selectedReport, setSelectedReport] = useState('overview');

  const renderOverview = () => (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-xl border border-border-light dark:border-border-dark shadow-sm relative overflow-hidden">
          <div className="absolute top-0 right-0 p-4 opacity-10">
            <Icon name="attach_money" className="text-primary" size={60} />
          </div>
          <div className="text-sm text-slate-500 dark:text-slate-400 mb-2 font-medium">总营收 (本月)</div>
          <div className="text-3xl font-bold text-slate-900 dark:text-white mb-2">¥ 1,245,800</div>
          <div className="flex items-center gap-1 text-xs font-medium text-emerald-600 dark:text-emerald-400">
            <Icon name="trending_up" size={14} />
            <span>较上月增长 8.4%</span>
          </div>
        </div>
        <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-xl border border-border-light dark:border-border-dark shadow-sm relative overflow-hidden">
          <div className="absolute top-0 right-0 p-4 opacity-10">
            <Icon name="pie_chart" className="text-blue-500" size={60} />
          </div>
          <div className="text-sm text-slate-500 dark:text-slate-400 mb-2 font-medium">平均入驻率</div>
          <div className="text-3xl font-bold text-slate-900 dark:text-white mb-2">78.5%</div>
          <div className="flex items-center gap-1 text-xs font-medium text-emerald-600 dark:text-emerald-400">
            <Icon name="trending_up" size={14} />
            <span>较上月提升 2.1%</span>
          </div>
        </div>
        <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-xl border border-border-light dark:border-border-dark shadow-sm relative overflow-hidden">
          <div className="absolute top-0 right-0 p-4 opacity-10">
            <Icon name="groups" className="text-purple-500" size={60} />
          </div>
          <div className="text-sm text-slate-500 dark:text-slate-400 mb-2 font-medium">活跃客户数</div>
          <div className="text-3xl font-bold text-slate-900 dark:text-white mb-2">142</div>
          <div className="flex items-center gap-1 text-xs font-medium text-emerald-600 dark:text-emerald-400">
            <Icon name="trending_up" size={14} />
            <span>新增 12 家</span>
          </div>
        </div>
        <div className="bg-surface-light dark:bg-surface-dark p-5 rounded-xl border border-border-light dark:border-border-dark shadow-sm relative overflow-hidden">
          <div className="absolute top-0 right-0 p-4 opacity-10">
            <Icon name="warning" className="text-orange-500" size={60} />
          </div>
          <div className="text-sm text-slate-500 dark:text-slate-400 mb-2 font-medium">设备故障率</div>
          <div className="text-3xl font-bold text-slate-900 dark:text-white mb-2">1.2%</div>
          <div className="flex items-center gap-1 text-xs font-medium text-emerald-600 dark:text-emerald-400">
            <Icon name="trending_down" size={14} />
            <span>较上月下降 0.5%</span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm">
          <div className="flex justify-between items-center mb-6">
            <h3 className="font-bold text-slate-900 dark:text-white">营收趋势分析</h3>
          </div>
          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={revenueData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3B82F6" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#3B82F6" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <XAxis dataKey="name" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(value) => `¥${value/1000}k`} />
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
                <Tooltip contentStyle={{ backgroundColor: '#1e293b', borderColor: '#334155', color: '#f8fafc', borderRadius: '8px' }} />
                <Area type="monotone" dataKey="value" stroke="#3B82F6" strokeWidth={3} fillOpacity={1} fill="url(#colorRevenue)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="lg:col-span-1 bg-gradient-to-br from-indigo-500 to-purple-600 rounded-xl shadow-lg p-6 text-white relative overflow-hidden">
          <div className="absolute top-0 right-0 -mr-8 -mt-8 w-32 h-32 rounded-full bg-white opacity-10"></div>
          <div className="absolute bottom-0 left-0 -ml-8 -mb-8 w-24 h-24 rounded-full bg-white opacity-10"></div>
          <div className="flex items-center gap-2 mb-6">
            <Icon name="auto_awesome" size={24} />
            <h3 className="font-bold text-lg">AI 深度洞察</h3>
          </div>
          <div className="space-y-4 relative z-10">
            <div className="bg-white/10 backdrop-blur-sm rounded-lg p-4 border border-white/20">
              <h4 className="font-semibold text-sm mb-1 flex items-center gap-2">
                <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
                营收预测
              </h4>
              <p className="text-xs text-indigo-100 leading-relaxed">基于历史数据和当前预订情况，预计下个月营收将达到 <strong>¥1,350,000</strong>，主要增长点在科技园区的LED大屏。</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );

  const renderPublishRate = () => (
    <div className="bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm h-[500px]">
      <h3 className="font-bold text-slate-900 dark:text-white mb-6">各类媒体上刊率</h3>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={publishRateData} layout="vertical" margin={{ top: 5, right: 30, left: 40, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" horizontal={true} vertical={false} stroke="#e2e8f0" />
          <XAxis type="number" domain={[0, 100]} tickFormatter={(val) => `${val}%`} stroke="#94a3b8" fontSize={12} />
          <YAxis dataKey="name" type="category" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
          <Tooltip cursor={{fill: 'transparent'}} contentStyle={{ borderRadius: '8px' }} formatter={(value) => `${value}%`} />
          <Bar dataKey="rate" radius={[0, 4, 4, 0]} barSize={30}>
            {publishRateData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.fill} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );

  const renderContractAmortization = () => (
    <div className="bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm h-[500px]">
      <h3 className="font-bold text-slate-900 dark:text-white mb-6">合同摊销和及时回款率</h3>
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart data={contractAmortizationData} margin={{ top: 20, right: 20, bottom: 20, left: 20 }}>
          <CartesianGrid stroke="#f5f5f5" strokeDasharray="3 3" vertical={false} />
          <XAxis dataKey="month" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
          <YAxis yAxisId="left" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `¥${val}k`} />
          <YAxis yAxisId="right" orientation="right" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `${val}%`} domain={[0, 100]} />
          <Tooltip contentStyle={{ borderRadius: '8px' }} />
          <Legend />
          <Bar yAxisId="left" dataKey="amortization" name="合同摊销金额" barSize={40} fill="#3B82F6" radius={[4, 4, 0, 0]} />
          <Line yAxisId="right" type="monotone" dataKey="collectionRate" name="及时回款率" stroke="#10B981" strokeWidth={3} dot={{ r: 6 }} />
        </ComposedChart>
      </ResponsiveContainer>
    </div>
  );

  const renderWorkerStats = () => (
    <div className="bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm h-[500px]">
      <h3 className="font-bold text-slate-900 dark:text-white mb-6">上刊工人计件统计</h3>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={workerStatsData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
          <XAxis dataKey="name" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
          <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
          <Tooltip cursor={{fill: 'transparent'}} contentStyle={{ borderRadius: '8px' }} />
          <Legend />
          <Bar dataKey="tasks" name="完成任务单数" fill="#8B5CF6" radius={[4, 4, 0, 0]} />
          <Bar dataKey="points" name="完成点位数" fill="#F59E0B" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );

  const renderSalesFunnel = () => (
    <div className="bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm h-[500px] flex flex-col items-center">
      <h3 className="font-bold text-slate-900 dark:text-white mb-6 w-full text-left">销售漏斗</h3>
      <div className="w-full max-w-2xl h-full">
        <ResponsiveContainer width="100%" height="100%">
          <FunnelChart>
            <Tooltip />
            <Funnel dataKey="value" data={funnelData} isAnimationActive>
              <LabelList position="right" fill="#64748b" stroke="none" dataKey="name" />
            </Funnel>
          </FunnelChart>
        </ResponsiveContainer>
      </div>
    </div>
  );

  const renderROI = () => (
    <div className="bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm h-[500px]">
      <h3 className="font-bold text-slate-900 dark:text-white mb-6">资源点位投资回报率 (ROI)</h3>
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={roiData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
          <XAxis dataKey="name" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
          <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `${val}%`} />
          <Tooltip cursor={{fill: 'transparent'}} contentStyle={{ borderRadius: '8px' }} formatter={(value) => `${value}%`} />
          <Bar dataKey="roi" name="ROI (%)" fill="#10B981" radius={[4, 4, 0, 0]} barSize={50}>
            {roiData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.roi > 200 ? '#10B981' : '#3B82F6'} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );

  const renderRevenueAmortization = () => (
    <div className="bg-surface-light dark:bg-surface-dark p-6 rounded-xl border border-border-light dark:border-border-dark shadow-sm h-[500px]">
      <h3 className="font-bold text-slate-900 dark:text-white mb-6">资源点位收入摊销报表</h3>
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={revenueAmortizationData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#10B981" stopOpacity={0.8}/>
              <stop offset="95%" stopColor="#10B981" stopOpacity={0}/>
            </linearGradient>
            <linearGradient id="colorCost" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor="#EF4444" stopOpacity={0.8}/>
              <stop offset="95%" stopColor="#EF4444" stopOpacity={0}/>
            </linearGradient>
          </defs>
          <XAxis dataKey="month" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
          <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(val) => `¥${val}k`} />
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
          <Tooltip contentStyle={{ borderRadius: '8px' }} />
          <Legend />
          <Area type="monotone" dataKey="revenue" name="摊销收入" stroke="#10B981" fillOpacity={1} fill="url(#colorRev)" />
          <Area type="monotone" dataKey="cost" name="摊销成本" stroke="#EF4444" fillOpacity={1} fill="url(#colorCost)" />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <h1 className="text-xl font-bold text-slate-900 dark:text-white">智能数据看板</h1>
        <div className="flex items-center gap-4">
          <div className="relative">
            <select 
              value={selectedReport}
              onChange={(e) => setSelectedReport(e.target.value)}
              className="appearance-none bg-white dark:bg-slate-800 border border-border-light dark:border-border-dark text-slate-700 dark:text-slate-200 text-sm rounded-lg focus:ring-primary focus:border-primary block w-64 p-2 pr-8 shadow-sm cursor-pointer"
            >
              {reportOptions.map(option => (
                <option key={option.id} value={option.id}>{option.name}</option>
              ))}
            </select>
            <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-slate-500">
              <Icon name="expand_more" size={18} />
            </div>
          </div>
          
          <div className="flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-lg">
            <button className="px-3 py-1.5 bg-white dark:bg-slate-700 shadow-sm rounded-md text-sm font-medium text-slate-800 dark:text-slate-200">本月</button>
            <button className="px-3 py-1.5 text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white text-sm font-medium rounded-md transition-colors">本季度</button>
            <button className="px-3 py-1.5 text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white text-sm font-medium rounded-md transition-colors">全年</button>
          </div>
          
          <button className="bg-white dark:bg-surface-dark border border-border-light dark:border-border-dark hover:bg-slate-50 dark:hover:bg-slate-800 text-slate-700 dark:text-slate-300 px-4 py-2 rounded-lg text-sm font-medium shadow-sm transition-colors flex items-center gap-2">
            <Icon name="download" size={18} />
            导出报告
          </button>
        </div>
      </div>

      <div className="flex-1 overflow-y-auto custom-scrollbar p-6">
        <div className="max-w-7xl mx-auto">
          {selectedReport === 'overview' && renderOverview()}
          {selectedReport === 'publish_rate' && renderPublishRate()}
          {selectedReport === 'contract_amortization' && renderContractAmortization()}
          {selectedReport === 'worker_stats' && renderWorkerStats()}
          {selectedReport === 'sales_funnel' && renderSalesFunnel()}
          {selectedReport === 'roi' && renderROI()}
          {selectedReport === 'revenue_amortization' && renderRevenueAmortization()}
        </div>
      </div>
    </div>
  );
}
