import { Icon } from '../components/Icon'
import React, { useState } from 'react';

interface DevContract {
  id: string;
  provider: string;
  startDate: string;
  endDate: string;
  amount: number;
  status: 'active' | 'expiring' | 'expired';
  // Detail fields
  signDate?: string;
  manager?: string;
  paymentTerms?: string;
  resources?: { name: string, type: string, count: number }[];
}

const mockContracts: DevContract[] = [
  { id: 'DEV-2023-045', provider: '市地铁运营公司', startDate: '2023-01-01', endDate: '2025-12-31', amount: 5000000, status: 'active', signDate: '2022-12-15', manager: '刘总监', paymentTerms: '按年支付，每年1月15日前支付当年费用', resources: [{ name: '地铁1号线', type: '线路', count: 120 }, { name: '高新园地铁站', type: '站点', count: 38 }] },
  { id: 'DEV-2024-012', provider: '翠园物业管理处', startDate: '2024-03-01', endDate: '2026-02-28', amount: 150000, status: 'active', signDate: '2024-02-20', manager: '赵经理', paymentTerms: '半年付', resources: [{ name: '翠园社区', type: '社区', count: 25 }] },
  { id: 'DEV-2021-088', provider: '城管局', startDate: '2021-06-01', endDate: '2024-05-31', amount: 800000, status: 'expired', signDate: '2021-05-10', manager: '刘总监', paymentTerms: '一次性支付', resources: [{ name: '滨海大道跨线桥', type: '大牌', count: 2 }] },
];

export default function DevelopmentContracts() {
  const [selectedContract, setSelectedContract] = useState<DevContract | null>(null);

  const closeDrawer = () => setSelectedContract(null);

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white">开发合同</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">管理资源采买与经营权合同。</p>
        </div>
        <button className="bg-primary hover:bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
          <Icon name="add" size={18} />
          新建合同
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
                    <th className="px-6 py-4">资源提供方</th>
                    <th className="px-6 py-4">经营权周期</th>
                    <th className="px-6 py-4">合同金额</th>
                    <th className="px-6 py-4">状态</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border-light dark:divide-border-dark">
                  {mockContracts.map(contract => (
                    <tr key={contract.id} onClick={() => setSelectedContract(contract)} className="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer group">
                      <td className="px-6 py-4 font-mono font-medium text-slate-900 dark:text-white group-hover:text-primary transition-colors">{contract.id}</td>
                      <td className="px-6 py-4 font-bold text-slate-900 dark:text-white">{contract.provider}</td>
                      <td className="px-6 py-4 text-xs">
                        <div>{contract.startDate}</div>
                        <div className="text-slate-400">至 {contract.endDate}</div>
                      </td>
                      <td className="px-6 py-4 font-medium text-slate-900 dark:text-white">¥ {contract.amount.toLocaleString()}</td>
                      <td className="px-6 py-4">
                        <span className={`inline-flex items-center px-2 py-1 rounded text-xs font-medium ${
                          contract.status === 'active' ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400' :
                          contract.status === 'expiring' ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400' :
                          'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
                        }`}>
                          {contract.status === 'active' ? '生效中' : contract.status === 'expiring' ? '即将到期' : '已过期'}
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
              <Icon name="handshake" className="text-primary" size={20} />
              开发合同详情
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
                    selectedContract.status === 'expiring' ? 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400' :
                    'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'
                  }`}>
                    {selectedContract.status === 'active' ? '生效中' : selectedContract.status === 'expiring' ? '即将到期' : '已过期'}
                  </span>
                  <span className="text-xs font-mono text-slate-500">{selectedContract.id}</span>
                </div>
                <h3 className="text-2xl font-bold text-slate-900 dark:text-white mb-1">{selectedContract.provider}</h3>
              </div>

              <div className="grid grid-cols-2 gap-4 bg-slate-50 dark:bg-slate-800/30 p-4 rounded-xl border border-border-light dark:border-border-dark">
                <div>
                  <div className="text-xs text-slate-500 mb-1">采买总额</div>
                  <div className="font-bold text-emerald-600 dark:text-emerald-400 text-lg">¥ {selectedContract.amount.toLocaleString()}</div>
                </div>
                <div>
                  <div className="text-xs text-slate-500 mb-1">开发负责人</div>
                  <div className="font-medium text-slate-900 dark:text-white">{selectedContract.manager}</div>
                </div>
                <div>
                  <div className="text-xs text-slate-500 mb-1">经营权起</div>
                  <div className="font-medium text-slate-900 dark:text-white font-mono">{selectedContract.startDate}</div>
                </div>
                <div>
                  <div className="text-xs text-slate-500 mb-1">经营权止</div>
                  <div className="font-medium text-slate-900 dark:text-white font-mono">{selectedContract.endDate}</div>
                </div>
                <div className="col-span-2">
                  <div className="text-xs text-slate-500 mb-1">付款条款</div>
                  <div className="font-medium text-slate-900 dark:text-white text-sm">{selectedContract.paymentTerms}</div>
                </div>
              </div>

              <div>
                <h4 className="text-sm font-semibold text-slate-900 dark:text-white mb-3 flex items-center gap-2">
                  <Icon name="inventory_2" className="text-slate-400" size={18} />
                  获取资源清单
                </h4>
                <div className="space-y-2">
                  {selectedContract.resources?.map((res, idx) => (
                    <div key={idx} className="bg-white dark:bg-slate-800 p-3 rounded-lg border border-border-light dark:border-border-dark flex justify-between items-center">
                      <div>
                        <div className="font-medium text-sm text-slate-900 dark:text-white">{res.name}</div>
                        <div className="text-xs text-slate-500">{res.type}点位</div>
                      </div>
                      <div className="font-bold text-slate-700 dark:text-slate-300 bg-slate-100 dark:bg-slate-700 px-3 py-1 rounded-md">
                        共 {res.count} 个广告位
                      </div>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex gap-3 pt-4">
                <button className="flex-1 py-2 bg-slate-100 hover:bg-slate-200 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 rounded-lg text-sm font-medium transition-colors flex items-center justify-center gap-2">
                  <Icon name="receipt_long" size={18} />
                  付款记录
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
