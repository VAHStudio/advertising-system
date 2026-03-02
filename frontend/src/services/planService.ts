/**
 * 方案管理服务
 * 调用后端 /api/plan 接口
 */

export interface Plan {
  id: number;
  planNo: string;
  planName: string;
  customer: string;
  releaseDateBegin: string;
  releaseDateEnd: string;
  releaseStatus: number;  // 1-意向, 2-锁位, 3-执行中, 4-执行完毕, 5-档
  salesType: number;      // 1-销售, 2-公益, 3-置换, 4-赠送, 5-余位, 6-其他
  mediaRequirements: string;
  // 扩展字段（需要后端支持）
  salesPerson?: string;   // 负责销售姓名
  sampleImage?: string;   // 小样设计图URL
  budget?: number;        // 预算
  estimatedReach?: number; // 预计触达人数
  updatedAt?: string;     // 更新时间
}

// 状态映射
export const planStatusMap: Record<number, { label: string; color: string }> = {
  1: { label: '意向', color: 'bg-slate-500' },
  2: { label: '锁位', color: 'bg-yellow-500' },
  3: { label: '执行中', color: 'bg-primary' },
  4: { label: '已结案', color: 'bg-green-500' },
  5: { label: '档', color: 'bg-red-500' },
};

/**
 * 获取所有方案
 */
export const getPlanList = async (): Promise<Plan[]> => {
  const response = await fetch('/api/plan/list');
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取方案列表失败');
};

/**
 * 分页获取方案
 */
export const getPlanPage = async (pageNum: number = 1, pageSize: number = 10): Promise<{
  list: Plan[];
  total: number;
  pages: number;
}> => {
  const response = await fetch(`/api/plan/page?pageNum=${pageNum}&pageSize=${pageSize}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({}),
  });
  
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取方案分页失败');
};

/**
 * 根据客户查询方案
 */
export const getPlansByCustomer = async (customer: string): Promise<Plan[]> => {
  const response = await fetch(`/api/plan/customer/${encodeURIComponent(customer)}`);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取客户方案失败');
};

/**
 * 根据状态查询方案
 */
export const getPlansByStatus = async (status: number): Promise<Plan[]> => {
  const response = await fetch(`/api/plan/status/${status}`);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取状态方案失败');
};

/**
 * 获取方案详情
 */
export const getPlanDetail = async (id: number): Promise<Plan> => {
  const response = await fetch(`/api/plan/${id}`);
  const result = await response.json();
  
  if (result.code === 200) {
    return result.data;
  }
  
  throw new Error(result.message || '获取方案详情失败');
};

/**
 * 格式化预算显示
 */
export const formatBudget = (budget?: number): string => {
  if (!budget) return '¥0';
  
  if (budget >= 1000000) {
    return `¥${(budget / 1000000).toFixed(1)}M`;
  } else if (budget >= 1000) {
    return `¥${(budget / 1000).toFixed(0)}k`;
  }
  return `¥${budget}`;
};

/**
 * 格式化触达人数
 */
export const formatReach = (reach?: number): string => {
  if (!reach) return '0';
  
  if (reach >= 1000000) {
    return `${(reach / 1000000).toFixed(1)}M+`;
  } else if (reach >= 1000) {
    return `${(reach / 1000).toFixed(0)}k+`;
  }
  return `${reach}+`;
};

/**
 * 格式化时间显示
 */
export const formatTimeAgo = (dateStr?: string): string => {
  if (!dateStr) return '';
  
  const date = new Date(dateStr);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  
  if (minutes < 60) {
    return `${minutes}分钟前`;
  } else if (hours < 24) {
    return `${hours}小时前`;
  } else if (days < 30) {
    return `${days}天前`;
  } else {
    return date.toLocaleDateString('zh-CN');
  }
};
