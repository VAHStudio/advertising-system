import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from './Sidebar';
import Header from './Header';

const routeTitles: Record<string, { title: string; subtitle?: string }> = {
  '/': { title: '工作台', subtitle: '早上好，Alex。这是您的每日概览。' },
  '/resources': { title: '库存资源' },
  '/communities': { title: '社区管理' },
  '/barrier-gates': { title: '道闸管理' },
  '/frames': { title: '框架管理' },
  '/dev-contracts': { title: '开发合同' },
  '/customers': { title: '客户管理' },
  '/plans': { title: '方案列表' },
  '/plan-communities': { title: '方案社区关联' },
  '/plan-barriers': { title: '方案道闸明细' },
  '/plan-frames': { title: '方案框架明细' },
  '/sales-contracts': { title: '销售合同' },
  '/inventory': { title: '销控查询' },
  '/execution': { title: '上刊管理' },
  '/analytics': { title: '数据看板' },
  '/ai-selection': { title: '智能选点' },
  '/ai-assistant': { title: 'AI助手' },
  '/speech': { title: '语音交互' },
  '/settings': { title: '系统设置' },
};

export default function Layout() {
  const location = useLocation();
  const routeInfo = routeTitles[location.pathname] || { title: 'Outdoor 4.0' };

  return (
    <div className="flex h-screen bg-background-light dark:bg-background-dark">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header title={routeInfo.title} subtitle={routeInfo.subtitle} />
        <main className="flex-1 overflow-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
