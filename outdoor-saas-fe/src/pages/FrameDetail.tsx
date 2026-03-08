import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import frameService, { Frame } from '../services/frameService';
import communityService, { Community } from '../services/communityService';

export default function FrameDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [frame, setFrame] = useState<Frame | null>(null);
  const [community, setCommunity] = useState<Community | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (id) {
      loadFrameDetail(parseInt(id));
    }
  }, [id]);

  const loadFrameDetail = async (frameId: number) => {
    try {
      setLoading(true);
      
      const frameData = await frameService.getById(frameId);
      setFrame(frameData);

      if (frameData.communityId) {
        const communityData = await communityService.getById(frameData.communityId);
        setCommunity(communityData);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

  const getInnerPositionText = (position?: number) => {
    switch (position) {
      case 1: return '内';
      case 2: return '外';
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

  if (error || !frame) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-red-500">{error || '框架不存在'}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 页面标题 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/frames')}
            className="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 className="text-2xl font-bold text-text-light dark:text-text-dark">
            框架详情
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
            <label className="text-sm text-subtext-light dark:text-subtext-dark">框架编号</label>
            <p className="text-text-light dark:text-text-dark font-medium">{frame.frameNo}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">楼栋</label>
            <p className="text-text-light dark:text-text-dark font-medium">{frame.building}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">单元</label>
            <p className="text-text-light dark:text-text-dark font-medium">{frame.unit}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">电梯</label>
            <p className="text-text-light dark:text-text-dark font-medium">{frame.elevator}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">内外位置</label>
            <p className="text-text-light dark:text-text-dark font-medium">
              {getInnerPositionText(frame.innerPosition)}
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
