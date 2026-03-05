/**
 * 方案社区关联服务
 */
import request from './api';

export interface PlanCommunity {
  id: number;
  planId: number;
  communityId: number;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  barrierRequiredQty?: number;
  frameRequiredQty?: number;
  plan?: {
    id: number;
    planNo: string;
    planName: string;
  };
  community?: {
    id: number;
    communityNo: string;
    buildingName: string;
  };
}

export const planCommunityService = {
  // 获取所有方案社区关联
  getAll: () => request<PlanCommunity[]>('/plan-community/list'),

  // 根据ID获取
  getById: (id: number) => request<PlanCommunity>(`/plan-community/${id}`),

  // 根据方案ID和社区ID查询
  getByPlanAndCommunity: (planId: number, communityId: number) =>
    request<PlanCommunity>(`/plan-community/query?planId=${planId}&communityId=${communityId}`),

  // 分页查询
  getPage: (page: number = 1, size: number = 10) =>
    request<PlanCommunity[]>('/plan-community/page', {
      method: 'POST',
      body: JSON.stringify({ page, size }),
    }),

  // 根据方案ID获取
  getByPlan: (planId: number) =>
    request<PlanCommunity[]>(`/plan-community/plan/${planId}`),

  // 根据社区ID获取
  getByCommunity: (communityId: number) =>
    request<PlanCommunity[]>(`/plan-community/community/${communityId}`),

  // 创建
  create: (data: Partial<PlanCommunity>) =>
    request<PlanCommunity>('/plan-community', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 批量创建
  createBatch: (data: Partial<PlanCommunity>[]) =>
    request<PlanCommunity[]>('/plan-community/batch', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 更新
  update: (data: Partial<PlanCommunity>) =>
    request<PlanCommunity>('/plan-community', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // 删除
  delete: (id: number) =>
    request<void>(`/plan-community/${id}`, { method: 'DELETE' }),

  // 批量删除
  deleteBatch: (ids: number[]) =>
    request<void>('/plan-community/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids),
    }),
};

export default planCommunityService;
