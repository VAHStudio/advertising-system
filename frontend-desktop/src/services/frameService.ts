/**
 * 框架媒体管理服务
 */
import request from './api';

export interface Frame {
  id: number;
  frameNo: string;
  communityId: number;
  communityName?: string;
  building: string;
  unit: string;
  elevator: string;
  innerPosition?: number;
  releaseDateBegin?: string;
  releaseDateEnd?: string;
  releaseStatus?: number;
}

export const frameService = {
  // 获取所有框架
  getAll: () => request<Frame[]>('/frame/list'),

  // 根据ID获取框架
  getById: (id: number) => request<Frame>(`/frame/${id}`),

  // 根据框架编号获取
  getByNo: (frameNo: string) => request<Frame>(`/frame/no/${frameNo}`),

  // 分页查询框架
  getPage: (page: number = 1, size: number = 10) =>
    request<Frame[]>('/frame/page', {
      method: 'POST',
      body: JSON.stringify({ page, size }),
    }),

  // 根据社区ID获取框架
  getByCommunity: (communityId: number) =>
    request<Frame[]>(`/frame/community/${communityId}`),

  // 创建框架
  create: (data: Partial<Frame>) =>
    request<Frame>('/frame', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  // 更新框架
  update: (data: Partial<Frame>) =>
    request<Frame>('/frame', {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // 删除框架
  delete: (id: number) =>
    request<void>(`/frame/${id}`, { method: 'DELETE' }),

  // 批量删除框架
  deleteBatch: (ids: number[]) =>
    request<void>('/frame/batch', {
      method: 'DELETE',
      body: JSON.stringify(ids),
    }),
};

export default frameService;
