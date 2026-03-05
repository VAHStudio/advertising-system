/**
 * 方案框架明细服务
 */
import request from './api';

export interface PlanFrame {
  id: number;
  planId: number;
  frameId: number;
  planCommunityId: number;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
  plan?: {
    id: number;
    planNo: string;
    planName: string;
  };
  frame?: {
    id: number;
    frameNo: string;
    building: string;
  };
  planCommunity?: {
    id: number;
    communityId: number;
  };
}

export const planFrameService = {
  getAll: () => request<PlanFrame[]>('/plan-frame/list'),
  getById: (id: number) => request<PlanFrame>(`/plan-frame/${id}`),
  getPage: (page: number = 1, size: number = 10) =>
    request<PlanFrame[]>('/plan-frame/page', {
      method: 'POST',
      body: JSON.stringify({ page, size }),
    }),
  getByPlan: (planId: number) =>
    request<PlanFrame[]>(`/plan-frame/plan/${planId}`),
  getByFrame: (frameId: number) =>
    request<PlanFrame[]>(`/plan-frame/frame/${frameId}`),
  getByPlanCommunity: (planCommunityId: number) =>
    request<PlanFrame[]>(`/plan-frame/plan-community/${planCommunityId}`),
  getByStatus: (releaseStatus: number) =>
    request<PlanFrame[]>(`/plan-frame/status/${releaseStatus}`),
  create: (data: Partial<PlanFrame>) =>
    request<PlanFrame>('/plan-frame', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  createBatch: (data: Partial<PlanFrame>[]) =>
    request<PlanFrame[]>('/plan-frame/batch', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  update: (data: Partial<PlanFrame>) =>
    request<PlanFrame>('/plan-frame', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  delete: (id: number) =>
    request<void>(`/plan-frame/${id}`, { method: 'DELETE' }),
  deleteBatch: (ids: number[]) =>
    request<void>('/plan-frame/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids),
    }),
};

export default planFrameService;
