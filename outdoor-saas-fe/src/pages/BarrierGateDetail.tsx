import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import barrierGateService, { BarrierGate } from '../services/barrierGateService';
import communityService, { Community } from '../services/communityService';

export default function BarrierGateDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [barrierGate, setBarrierGate] = useState<BarrierGate | null>(null);
  const [community, setCommunity] = useState<Community | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (id) {
      loadBarrierGateDetail(parseInt(id));
    }
  }, [id]);

  const loadBarrierGateDetail = async (barrierId: number) => {
    try {
      setLoading(true);
      
      const barrierData = await barrierGateService.getById(barrierId);
      setBarrierGate(barrierData);

      if (barrierData.communityId) {
        const communityData = await communityService.getById(barrierData.communityId);
        setCommunity(communityData);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

  const getDevicePositionText = (position?: number) => {
    switch (position) {
      case 1: return '入口';
      case 2: return '出口';
      default: return '未知';
    }
  };

  const getScreenPositionText = (position?: number) => {
    switch (position) {
      case 1: return '左侧';
      case 2: return '右侧';
      default: return '未知';
    }
  };

  const getLightboxDirectionText = (direction?: number) => {
    switch (direction) {
      case 1: return '正面';
      case 2: return '反面';
      default: return '未知';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-subtext-light dark:text-subtext-dark">加载中...</div>
      </div>
    );
  }

  if (error || !barrierGate) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-red-500">{error || '道闸不存在'}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/barrier-gates')}
            className="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 className="text-2xl font-bold text-text-light dark:text-text-dark">
            道闸详情
          </h1>
        </div>
        <div className="space-x-2">
          <button className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors">
            编辑
          </button>
          <button className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors">
            删除
          </button>
        </div>
      </div>

      {/* 基本信息 */}
      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 className="text-lg font-semibold text-text-light dark:text-text-dark mb-4">基本信息</h2>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">道闸编号</label>
            <p className="text-text-light dark:text-text-dark font-medium">{barrierGate.gateNo}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">设备编号</label>
            <p className="text-text-light dark:text-text-dark font-medium">{barrierGate.deviceNo}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">门岗位置</label>
            <p className="text-text-light dark:text-text-dark font-medium">{barrierGate.doorLocation}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">设备位置</label>
            <p className="text-text-light dark:text-text-dark font-medium">
              {getDevicePositionText(barrierGate.devicePosition)}
            </p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">屏幕位置</label>
            <p className="text-text-light dark:text-text-dark font-medium">
              {getScreenPositionText(barrierGate.screenPosition)}
            </p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">灯箱方向</label>
            <p className="text-text-light dark:text-text-dark font-medium">
              {getLightboxDirectionText(barrierGate.lightboxDirection)}
            </p>
          </div>
        </div>
      </div>

      {/* 所属社区信息 */}
      {community && (
        <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <h2 className="text-lg font-semibold text-text-light dark:text-text-dark mb-4">所属社区</h2>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="text-sm text-subtext-light dark:text-subtext-dark">社区编号</label>
              <p className="text-text-light dark:text-text-dark font-medium">{community.communityNo}</p>
            </div>
            <div>
              <label className="text-sm text-subtext-light dark:text-subtext-dark">社区名称</label>
              <p className="text-text-light dark:text-text-dark font-medium">{community.buildingName}</p>
            </div>
            <div>
              <label className="text-sm text-subtext-light dark:text-subtext-dark">城市</label>
              <p className="text-text-light dark:text-text-dark font-medium">{community.city}</p>
            </div>
            <div>
              <label className="text-sm text-subtext-light dark:text-subtext-dark">地址</label>
              <p className="text-text-light dark:text-text-dark font-medium">{community.buildingAddress}</p>
            </div>
          </div>
          <div className="mt-4">
            <button
              onClick={() => navigate(`/communities/${community.id}`)}
              className="text-primary hover:text-primary/80 transition-colors"
            >
              查看社区详情 →
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
