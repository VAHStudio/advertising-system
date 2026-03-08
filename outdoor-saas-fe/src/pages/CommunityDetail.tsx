import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import communityService, { Community } from '../services/communityService';
import barrierGateService, { BarrierGate } from '../services/barrierGateService';
import frameService, { Frame } from '../services/frameService';

export default function CommunityDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [community, setCommunity] = useState<Community | null>(null);
  const [barrierGates, setBarrierGates] = useState<BarrierGate[]>([]);
  const [frames, setFrames] = useState<Frame[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (id) {
      loadCommunityDetail(parseInt(id));
    }
  }, [id]);

  const loadCommunityDetail = async (communityId: number) => {
    try {
      setLoading(true);
      
      // 并行加载社区详情、道闸列表、框架列表
      const [communityData, barriersData, framesData] = await Promise.all([
        communityService.getById(communityId),
        barrierGateService.getByCommunity(communityId),
        frameService.getByCommunity(communityId),
      ]);

      setCommunity(communityData);
      setBarrierGates(barriersData);
      setFrames(framesData);
    } catch (err) {
      setError(err instanceof Error ? err.message : '加载失败');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-subtext-light dark:text-subtext-dark">加载中...</div>
      </div>
    );
  }

  if (error || !community) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-red-500">{error || '社区不存在'}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 页面标题和操作按钮 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <button
            onClick={() => navigate('/communities')}
            className="text-subtext-light dark:text-subtext-dark hover:text-text-light dark:hover:text-text-dark transition-colors"
          >
            ← 返回
          </button>
          <h1 className="text-2xl font-bold text-text-light dark:text-text-dark">
            社区详情
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

      {/* 基本信息卡片 */}
      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 className="text-lg font-semibold text-text-light dark:text-text-dark mb-4">基本信息</h2>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">社区编号</label>
            <p className="text-text-light dark:text-text-dark font-medium">{community.communityNo}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">楼宇名称</label>
            <p className="text-text-light dark:text-text-dark font-medium">{community.buildingName}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">地址</label>
            <p className="text-text-light dark:text-text-dark font-medium">{community.buildingAddress}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">城市</label>
            <p className="text-text-light dark:text-text-dark font-medium">{community.city}</p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">坐标</label>
            <p className="text-text-light dark:text-text-dark font-medium">
              {community.coordLat}, {community.coordLng}
            </p>
          </div>
          <div>
            <label className="text-sm text-subtext-light dark:text-subtext-dark">媒体数量</label>
            <p className="text-text-light dark:text-text-dark font-medium">
              道闸: {barrierGates.length} | 框架: {frames.length}
            </p>
          </div>
        </div>
      </div>

      {/* 道闸列表 */}
      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-text-light dark:text-text-dark">
            道闸列表 ({barrierGates.length})
          </h2>
        </div>
        <table className="w-full">
          <thead className="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">编号</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">设备号</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">位置</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">状态</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
            {barrierGates.map((gate) => (
              <tr key={gate.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/50">
                <td className="px-6 py-4 text-sm text-text-light dark:text-text-dark">{gate.gateNo}</td>
                <td className="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{gate.deviceNo}</td>
                <td className="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{gate.doorLocation}</td>
                <td className="px-6 py-4 text-sm">
                  <span className={`px-2 py-1 rounded text-xs ${
                    gate.releaseStatus === 1 
                      ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' 
                      : 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300'
                  }`}>
                    {gate.releaseStatus === 1 ? '可用' : '占用'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* 框架列表 */}
      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-text-light dark:text-text-dark">
            框架列表 ({frames.length})
          </h2>
        </div>
        <table className="w-full">
          <thead className="bg-gray-50 dark:bg-gray-800">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">编号</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">楼栋</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">单元</th>
              <th className="px-6 py-3 text-left text-xs font-semibold text-subtext-light dark:text-subtext-dark uppercase">电梯</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
            {frames.map((frame) => (
              <tr key={frame.id} className="hover:bg-gray-50 dark:hover:bg-gray-800/50">
                <td className="px-6 py-4 text-sm text-text-light dark:text-text-dark">{frame.frameNo}</td>
                <td className="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{frame.building}</td>
                <td className="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{frame.unit}</td>
                <td className="px-6 py-4 text-sm text-subtext-light dark:text-subtext-dark">{frame.elevator}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
