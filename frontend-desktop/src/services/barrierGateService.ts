/**
 * 道闸设备管理服务
 */
import request from './api';

export interface BarrierGate {
  id: number;
  gateNo: string;
  deviceNo: string;
  doorLocation: string;
  devicePosition?: number;
  screenPosition?: number;
  lightboxDirection?: number;
  communityId: number;
  communityName?: string;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
}

export const barrierGateService = {
  // 获取所有道闸
  getAll: () => request<BarrierGate[]>('/barrier-gate/list'),

  // 根据ID获取道闸
  getById: (id: number) => request<BarrierGate>(`/barrier-gate/${id}`),

  // 根据道闸编号获取
  getByNo: (gateNo: string) => request<BarrierGate>(`/barrier-gate/no/${gateNo}`),

  // 分页查询道闸
  getPage: (page: number = 1, size: number = 10) =>
    request<BarrierGate[]>('/barrier-gate/page', {
      method: 'POST',
      body: JSON.stringify({ page, size }),
    }),

  // 根据社区ID获取道闸
  getByCommunity: (communityId: number) =>
    request<BarrierGate[]>(`/barrier-gate/community/${communityId}`),

  // 创建道闸
  create: (data: Partial<BarrierGate>) =>
    request<BarrierGate>('/barrier-gate', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 更新道闸
  update: (data: Partial<BarrierGate>) =>
    request<BarrierGate>('/barrier-gate', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // 删除道闸
  delete: (id: number) =>
    request<void>(`/barrier-gate/${id}`, { method: 'DELETE' }),

  // 批量删除道闸
  deleteBatch: (ids: number[]) =>
    request<void>('/barrier-gate/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids),
    }),
};

export default barrierGateService;
