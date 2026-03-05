/**
 * 社区/资源管理服务
 */
import request from './api';

export interface Community {
  id: number;
  communityNo: string;
  buildingName: string;
  buildingAddress: string;
  city: string;
  district?: string;
  province?: string;
  mediaType?: string;
  houseCount?: number;
  rent?: number;
  signTime?: string;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
}

export interface BarrierGate {
  id: number;
  gateNo: string;
  deviceNo: string;
  doorLocation: string;
  communityId: number;
  communityName?: string;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
}

export interface Frame {
  id: number;
  frameNo: string;
  building: string;
  unit: string;
  elevator: string;
  communityId: number;
  communityName?: string;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
}

export const communityService = {
  // 获取所有社区
  getAll: () => request<Community[]>('/community/list'),

  // 根据ID获取社区
  getById: (id: number) => request<Community>(`/community/${id}`),

  // 根据社区编号获取
  getByNo: (communityNo: string) => request<Community>(`/community/no/${communityNo}`),

  // 获取社区列表（分页）
  getPage: (page: number = 1, size: number = 10) =>
    request<Community[]>('/community/page', {
      method: 'POST',
      body: JSON.stringify({ page, size }),
    }),

  // 根据城市获取社区
  getByCity: (city: string) => request<Community[]>(`/community/city/${city}`),

  // 创建社区
  create: (data: Partial<Community>) =>
    request<Community>('/community', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 更新社区
  update: (data: Partial<Community>) =>
    request<Community>('/community', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // 删除社区
  delete: (id: number) =>
    request<void>(`/community/${id}`, { method: 'DELETE' }),

  // 批量删除社区
  deleteBatch: (ids: number[]) =>
    request<void>('/community/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids),
    }),
};

export default communityService;
