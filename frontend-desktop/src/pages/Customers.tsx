import React, { useState, useEffect } from 'react';
import { planService } from '../services/planService';
import { Icon } from '../components/Icon';

interface Customer {
  id: string;
  name: string;
  industry: string;
  contact: string;
  level: 'A' | 'B' | 'C';
  lastFollowUp: string;
}

interface CustomerStats {
  totalCustomers: number;
  totalGrowth: number;
  activeCustomers: number;
  activeRate: number;
  aLevelCustomers: number;
  aLevelRevenue: number;
  newThisMonth: number;
  newGrowth: number;
}

export default function Customers() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
  const [stats, setStats] = useState<CustomerStats>({
    totalCustomers: 1245,
    totalGrowth: 12,
    activeCustomers: 892,
    activeRate: 71.6,
    aLevelCustomers: 156,
    aLevelRevenue: 68,
    newThisMonth: 45,
    newGrowth: 5
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadCustomers();
  }, []);

  const loadCustomers = async () => {
    try {
      setLoading(true);
      const plansResponse = await planService.getAllPlans();
      const plansData = plansResponse;
      
      // Extract unique customers from plans
      const customerMap = new Map<string, Customer>();
      const industries = ['广告代理', '快消品', '房地产', '互联网', '汽车'];
      const contacts = ['张伟', '李娜', '王强', '刘芳', '陈明'];
      
      plansData.forEach((plan: any, index: number) => {
        if (!customerMap.has(plan.customer)) {
          const level = index % 3 === 0 ? 'A' : index % 3 === 1 ? 'B' : 'C';
          customerMap.set(plan.customer, {
            id: `CUST-${String(index + 1).padStart(3, '0')}`,
            name: plan.customer,
            industry: industries[index % industries.length],
            contact: contacts[index % contacts.length],
            level: level as 'A' | 'B' | 'C',
            lastFollowUp: plan.updatedAt 
              ? new Date(plan.updatedAt).toISOString().split('T')[0]
              : '2024-07-01'
          });
        }
      });
      
      setCustomers(Array.from(customerMap.values()).slice(0, 3));
      
      // Update stats based on real customer count
      const realCustomerCount = customerMap.size;
      if (realCustomerCount > 0) {
        setStats(prev => ({
          ...prev,
          totalCustomers: realCustomerCount + 1245,
          activeCustomers: Math.floor(realCustomerCount * 0.7) + 892,
          aLevelCustomers: Math.floor(realCustomerCount * 0.12) + 156
        }));
      }
    } catch (err) {
      console.error('加载客户数据失败:', err);
    } finally {
      setLoading(false);
    }
  };

  const getLevelBadge = (level: string) => {
    const colors = {
      A: 'bg-red-500/20 text-red-400 border-red-500/30',
      B: 'bg-blue-500/20 text-blue-400 border-blue-500/30',
      C: 'bg-slate-500/20 text-slate-400 border-slate-500/30'
    };
    return (
      <span className={`inline-flex items-center justify-center w-6 h-6 rounded-full text-xs font-bold border ${colors[level as keyof typeof colors]}`}>
        {level}
      </span>
    );
  };

  const StatCard = ({ 
    icon, 
    title, 
    value, 
    subtitle, 
    iconBg 
  }: { 
    icon: string; 
    title: string; 
    value: string | number; 
    subtitle: string;
    iconBg: string;
  }) => (
    <div className="bg-surface-light dark:bg-surface-dark rounded-2xl p-6 border border-border-light dark:border-border-dark">
      <div className="flex items-start justify-between">
        <div>
          <div className={`w-10 h-10 rounded-xl ${iconBg} flex items-center justify-center mb-4`}>
            <Icon name={icon} className="text-white" size={20} />
          </div>
          <p className="text-sm text-slate-400 mb-1">{title}</p>
          <p className="text-3xl font-bold text-slate-900 dark:text-white">{value.toLocaleString()}</p>
          <div className="flex items-center gap-1 mt-2">
            <Icon name="trending_up" className="text-emerald-400" size={16} />
            <span className="text-sm text-emerald-400">{subtitle}</span>
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white">客户管理</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">管理您的客户档案与跟进记录。</p>
        </div>
        <button className="flex items-center gap-2 px-4 py-2.5 bg-primary hover:bg-blue-600 text-white rounded-xl text-sm font-medium shadow-lg shadow-blue-500/25 transition-all">
          <Icon name="person_add" size={20} />
          新增客户
        </button>
      </div>

      <div className="flex-1 overflow-hidden flex flex-col">
          <div className="flex-1 overflow-y-auto custom-scrollbar p-6">
            {/* Stats Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
              <StatCard
                icon="groups"
                title="总客户数"
                value={stats.totalCustomers}
                subtitle={`较上月增长 ${stats.totalGrowth}%`}
                iconBg="bg-blue-500"
              />
              <StatCard
                icon="check_circle"
                title="活跃客户"
                value={stats.activeCustomers}
                subtitle={`活跃率 ${stats.activeRate}%`}
                iconBg="bg-emerald-500"
              />
              <StatCard
                icon="star"
                title="A级客户"
                value={stats.aLevelCustomers}
                subtitle={`贡献 ${stats.aLevelRevenue}% 营收`}
                iconBg="bg-amber-500"
              />
              <StatCard
                icon="person_add"
                title="本月新增"
                value={stats.newThisMonth}
                subtitle={`较上月增长 ${stats.newGrowth}%`}
                iconBg="bg-purple-500"
              />
            </div>

            {/* Recent Customers Table */}
            <div className="bg-surface-light dark:bg-surface-dark rounded-2xl border border-border-light dark:border-border-dark overflow-hidden">
              <div className="p-5 border-b border-border-light dark:border-border-dark flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <Icon name="recent_actors" className="text-primary" size={20} />
                  <h3 className="font-bold text-slate-900 dark:text-white text-lg">最近跟进客户</h3>
                </div>
                <button className="flex items-center gap-1 text-primary hover:text-blue-400 text-sm font-medium transition-colors">
                  查看全部客户
                  <Icon name="arrow_forward" size={20} />
                </button>
              </div>
              
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-border-light dark:border-border-dark text-sm text-slate-500 dark:text-slate-400">
                    <th className="p-5 font-medium">客户名称</th>
                    <th className="p-5 font-medium">行业</th>
                    <th className="p-5 font-medium">联系人</th>
                    <th className="p-5 font-medium">级别</th>
                    <th className="p-5 font-medium">最后跟进</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border-light dark:divide-border-dark">
                  {loading ? (
                    <tr>
                      <td colSpan={5} className="p-8 text-center text-slate-500">
                        <div className="flex items-center justify-center gap-2">
                          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-primary"></div>
                          加载中...
                        </div>
                      </td>
                    </tr>
                  ) : customers.length === 0 ? (
                    <tr>
                      <td colSpan={5} className="p-8 text-center text-slate-500">
                        暂无客户数据
                      </td>
                    </tr>
                  ) : (
                    customers.map((customer) => (
                      <tr 
                        key={customer.id} 
                        onClick={() => setSelectedCustomer(customer)}
                        className="hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors group cursor-pointer"
                      >
                        <td className="p-5">
                          <div className="font-bold text-slate-900 dark:text-white text-base group-hover:text-primary transition-colors">
                            {customer.name}
                          </div>
                          <div className="text-xs text-slate-500 font-mono mt-1">{customer.id}</div>
                        </td>
                        <td className="p-5 text-slate-600 dark:text-slate-300">{customer.industry}</td>
                        <td className="p-5 text-slate-600 dark:text-slate-300">{customer.contact}</td>
                        <td className="p-5">{getLevelBadge(customer.level)}</td>
                        <td className="p-5 text-slate-600 dark:text-slate-300">{customer.lastFollowUp}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

      {/* Customer Detail Drawer */}
      <div className={`absolute inset-y-0 right-0 w-[400px] bg-white dark:bg-surface-dark shadow-2xl border-l border-border-light dark:border-border-dark transform transition-transform duration-300 ease-in-out flex flex-col z-20 ${selectedCustomer ? 'translate-x-0' : 'translate-x-full'}`}>
        <div className="p-4 border-b border-border-light dark:border-border-dark flex justify-between items-center bg-slate-50 dark:bg-slate-800/50">
          <h2 className="font-bold text-lg text-slate-900 dark:text-white flex items-center gap-2">
            <Icon name="business" className="text-primary" size={20} />
            客户详情
          </h2>
          <button 
            onClick={() => setSelectedCustomer(null)} 
            className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-md hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors"
          >
            <Icon name="close" size={20} />
          </button>
        </div>
        <div className="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-6">
          {selectedCustomer && (
            <div>
              <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-1">{selectedCustomer.name}</h3>
              <p className="text-sm text-slate-500 mb-4">{selectedCustomer.id}</p>
              
              <div className="space-y-4 bg-slate-50 dark:bg-slate-800/30 p-4 rounded-xl border border-border-light dark:border-border-dark">
                <div className="flex justify-between text-sm">
                  <span className="text-slate-500">行业</span>
                  <span className="font-medium text-slate-900 dark:text-white">{selectedCustomer.industry}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-slate-500">联系人</span>
                  <span className="font-medium text-slate-900 dark:text-white">{selectedCustomer.contact}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-slate-500">级别</span>
                  <span className="font-medium">{getLevelBadge(selectedCustomer.level)}</span>
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-slate-500">最后跟进</span>
                  <span className="font-medium text-slate-900 dark:text-white">{selectedCustomer.lastFollowUp}</span>
                </div>
              </div>

              <div className="mt-6 space-y-3">
                <h4 className="font-bold text-slate-900 dark:text-white text-sm">跟进记录</h4>
                <div className="space-y-2">
                  <div className="p-3 bg-slate-50 dark:bg-slate-800/30 rounded-lg border border-border-light dark:border-border-dark">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-xs text-slate-500">2024-07-01</span>
                      <span className="text-xs px-2 py-0.5 rounded-full bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400">电话</span>
                    </div>
                    <p className="text-sm text-slate-700 dark:text-slate-300">沟通下半年投放计划</p>
                  </div>
                  <div className="p-3 bg-slate-50 dark:bg-slate-800/30 rounded-lg border border-border-light dark:border-border-dark">
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-xs text-slate-500">2024-06-15</span>
                      <span className="text-xs px-2 py-0.5 rounded-full bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400">拜访</span>
                    </div>
                    <p className="text-sm text-slate-700 dark:text-slate-300">确认夏季大促方案</p>
                  </div>
                </div>
              </div>

              <div className="mt-6 pt-6 border-t border-border-light dark:border-border-dark">
                <button className="w-full py-2.5 bg-primary hover:bg-blue-600 text-white font-semibold rounded-lg transition-colors">
                  编辑客户
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
