import { Icon } from '../components/Icon'
import React, { useState } from 'react';

interface SalesContract {
  id: string;
  customer: string;
  campaign: string;
  startDate: string;
  endDate: string;
  amount: number;
  status: 'draft' | 'signing' | 'active' | 'completed' | 'terminated';
  // Detail fields
  signDate?: string;
  salesperson?: string;
  paymentTerms?: string;
  resources?: { point: string, media: string, count: number }[];
}

const mockContracts: SalesContract[] = [
  { id: 'SC-2024-001', customer: '星空传媒', campaign: '夏季大促主视觉', startDate: '2024-07-01', endDate: '2024-09-30', amount: 450000, status: 'active', signDate: '2024-06-25', salesperson: '王经理', paymentTerms: '分三期：30%预付，40%中期，30%尾款', resources: [{ point: '市中心商业街', media: 'LED大屏', count: 2 }, { point: '地铁1号线', media: '车厢海报', count: 50 }] },
  { id: 'SC-2024-002', customer: '绿生活零售', campaign: '品牌焕新A版', startDate: '2024-08-01', endDate: '2024-10-31', amount: 120000, status: 'signing', signDate: '-', salesperson: '李销售', paymentTerms: '全款预付', resources: [{ point: '翠园社区', media: '电梯框架', count: 20 }] },
  { id: 'SC-2024-003', customer: '海洋广告', campaign: '新车发布预热', startDate: '2024-05-01', endDate: '2024-05-31', amount: 280000, status: 'completed', signDate: '2024-04-20', salesperson: '王经理', paymentTerms: '50%预付，50%尾款', resources: [{ point: '滨海大道跨线桥', media: '户外大牌', count: 1 }] },
];

export default function SalesContracts() {
  const [selectedContract, setSelectedContract] = useState<SalesContract | null>(null);

  const closeDrawer = () => setSelectedContract(null);

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white">销售合同管理</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">管理广告业务销售合同及归档。</p>
        </div>
        <button className="bg-primary hover:bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
          <Icon name="post_add" size={18} />
          起草合同
        </button>
      </div>

      <div className="flex-1 overflow-hidden flex relative">
        <div className="flex-1 overflow-y-auto p-6 custom-scrollbar">
          <div className="bg-white dark:bg-surface-dark rounded-xl shadow-sm border border-border-light dark:border-border-dark overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-600 dark:text-slate-400">
                <thead className="bg-slate-50 dark:bg-slate-800/50 text-xs uppercase font-semibold text-slate-500 dark:text-slate-400 border-b border-border-light dark:border-border-dark">
                  <tr>
                    <th className="px-6 py-4">合同编号</th>
                    <th className="px-6 py-4">客户名称</th>
                    <th className="px-6 py-4">广告活动</th>
                    <th className="px-6 py-4">合同周期</th>
                    <th className="px-6 py-4">合同金额</th>
                    <th className="px-6 py-4">状态</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border-light dark:divide-border-dark">
                  {mockContracts.map(contract => (
                    <tr key={contract.id} onClick={() => setSelectedContract(contract)} className="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer group">
                      <td className="px-6 py-4 font-mono font-medium text-slate-900 dark:text-white group-hover:text-primary transition-colors">{contract.id}</td>
                      <td className="px-6 py-4 font-bold text-slate-900 dark:text-white">{contract.customer}</td>
                      <td className="px-6 py-4">{contract.campaign}</td>
                      <td className="px-6 py-4 text-xs">
                        <div>{contract.startDate}</div>
                        <div className="text-slate-400">至 {contract.endDate}</div>
                      </td>
                      <td className="px-6 py-4 font-medium text-slate-900 dark:text-white">¥ {contract.amount.toLocaleString()}</td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center px-2 py-1 rounded text-xs font-medium ${
                          contract.status === 'active' ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400' :
                          contract.status === 'signing' ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' :
                          contract.status === 'completed' ? 'bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300' :
                          'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                        }`}>
                          {contract.status === 'active' ? '执行中' : contract.status === 'signing' ? '签署中' : contract.status === 'completed' ? '已完成' : '草稿'}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Slide-out Drawer */}
        <div className={`absolute inset-y-0 right-0 w-[450px] bg-white dark:bg-surface-dark shadow-2xl border-l border-border-light dark:border-border-dark transform transition-transform duration-300 ease-in-out flex flex-col z-20 ${selectedContract ? 'translate-x-0' : 'translate-x-full'}`}>
          <div className="p-4 border-b border-border-light dark:border-border-dark flex justify-between items-center bg-slate-50 dark:bg-slate-800/50">
            <h2 className="font-bold text-lg text-slate-900 dark:text-white flex items-center gap-2">
              <Icon name="description" className="text-primary" size={20} />
              销售合同详情
            </h2>
            <button onClick={closeDrawer} className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-md hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">
              <Icon name="close" size={20} />
            </button>
          </div>
          
          {selectedContract && (
            <div className="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-6">
              <div>
                <div className="flex items-center gap-2 mb-2">
                  <span className={`inline-flex items-center px-2 py-0.5 rounded text-[10px] font-medium ${
                    selectedContract.status === 'active' ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400' :
                    selectedContract.status === 'signing' ? 'bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400' :
                    selectedContract.status === 'completed' ? 'bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300' :
                    'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'
                  }`}>
                    {selectedContract.status === 'active' ? '执行中' : selectedContract.status === 'signing' ? '签署中' : selectedContract.status === 'completed' ? '已完成' : '草稿'}
                  </span>
                  <span className="text-xs font-mono text-slate-500">{selectedContract.id}</span>
                </div>
                <h3 className="text-2xl font-bold text-slate-900 dark:text-white mb-1">{selectedContract.customer}</h3>
                <p className="text-sm text-primary font-medium">{selectedContract.campaign}</p>
              </div>

              <div className="grid grid-cols-2 gap-4 bg-slate-50 dark:bg-slate-800/30 p-4 rounded-xl border border-border-light dark:border-border-dark">
                <div>
                  <div className="text-xs text-slate-500 mb-1">合同总额</div>
                  <div className="font-bold text-emerald-600 dark:text-emerald-400 text-lg">¥ {selectedContract.amount.toLocaleString()}</div>
                </div>
                <div>
                  <div className="text-xs text-slate-500 mb-1">销售负责人</div>
                  <div className="font-medium text-slate-900 dark:text-white">{selectedContract.salesperson}</div>
                </div>
                <div>
                  <div className="text-xs text-slate-500 mb-1">开始日期</div>
                  <div className="font-medium text-slate-900 dark:text-white font-mono">{selectedContract.startDate}</div>
                </div>
                <div>
                  <div className="text-xs text-slate-500 mb-1">结束日期</div>
                  <div className="font-medium text-slate-900 dark:text-white font-mono">{selectedContract.endDate}</div>
                </div>
                <div className="col-span-2">
                  <div className="text-xs text-slate-500 mb-1">付款条款</div>
                  <div className="font-medium text-slate-900 dark:text-white text-sm">{selectedContract.paymentTerms}</div>
                </div>
              </div>

              <div>
                <h4 className="text-sm font-semibold text-slate-900 dark:text-white mb-3 flex items-center gap-2">
                  <Icon name="view_list" className="text-slate-400" size={18} />
                  约定资源清单
                </h4>
                <div className="space-y-2">
                  {selectedContract.resources?.map((res, idx) => (
                    <div key={idx} className="bg-white dark:bg-slate-800 p-3 rounded-lg border border-border-light dark:border-border-dark flex justify-between items-center">
                      <div>
                        <div className="font-medium text-sm text-slate-900 dark:text-white">{res.point}</div>
                        <div className="text-xs text-slate-500">{res.media}</div>
                      </div>
                      <div className="font-bold text-slate-700 dark:text-slate-300 bg-slate-100 dark:bg-slate-700 px-3 py-1 rounded-md">
                        {res.count} 个
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex gap-3 pt-4">
                <button className="flex-1 py-2 bg-primary hover:bg-blue-600 text-white rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center justify-center gap-2">
                  <Icon name="download" size={18} />
                  下载合同原件
                </button>
              </div>
            </div>
          )}
        </div>
        
        {selectedContract && (
          <div className="absolute inset-0 bg-black/20 dark:bg-black/40 z-10 md:hidden" onClick={closeDrawer} />
        )}
      </div>
    </div>
  );
}
