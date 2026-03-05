/**
 * 方案道闸明细服务
 */
import request from './api';

export interface PlanBarrier {
  id: number;
  planId: number;
  barrierGateId: number;
  planCommunityId: number;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
  plan?: {
    id: number;
    planNo: string;
    planName: string;
  };
  barrierGate?: {
    id: number;
    gateNo: string;
    deviceNo: string;
  };
  planCommunity?: {
    id: number;
    communityId: number;
  };
}

export const planBarrierService = {
  // 获取所有方案道闸明细
  getAll: () => request<PlanBarrier[]>('/plan-barrier/list'),

  // 根据ID获取
  getById: (id: number) => request<PlanBarrier>(`/plan-barrier/${id}`),

  // 分页查询
  getPage: (page: number = 1, size: number = 10) =>
    request<PlanBarrier[]>('/plan-barrier/page', {
      method: 'POST',
      body: JSON.stringify({ page, size }),
    }),

  // 根据方案ID获取
  getByPlan: (planId: number) =>
    request<PlanBarrier[]>(`/plan-barrier/plan/${planId}`),

  // 根据道闸ID获取
  getByBarrier: (barrierGateId: number) =>
    request<PlanBarrier[]>(`/plan-barrier/barrier/${barrierGateId}`),

  // 根据方案社区ID获取
  getByPlanCommunity: (planCommunityId: number) =>
    request<PlanBarrier[]>(`/plan-barrier/plan-community/${planCommunityId}`),

  // 根据发布状态获取
  getByStatus: (releaseStatus: number) =>
    request<PlanBarrier[]>(`/plan-barrier/status/${releaseStatus}`),

  // 创建
  create: (data: Partial<PlanBarrier>) =>
    request<PlanBarrier>('/plan-barrier', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 批量创建
  createBatch: (data: Partial<PlanBarrier>[]) =>
    request<PlanBarrier[]>('/plan-barrier/batch', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 更新
  update: (data: Partial<PlanBarrier>) =>
    request<PlanBarrier>('/plan-barrier', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // 删除
  delete: (id: number) =>
    request<void>(`/plan-barrier/${id}`, { method: 'DELETE' }),

  // 批量删除
  deleteBatch: (ids: number[]) =>
    request<void>('/plan-barrier/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids),
    }),
};

export default planBarrierService;
