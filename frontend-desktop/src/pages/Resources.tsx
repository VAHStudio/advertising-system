import React, { useState, useEffect } from 'react';
import { Icon } from '../components/Icon';
import { communityService, Community, BarrierGate, Frame } from '../services/communityService';
import { barrierGateService } from '../services/barrierGateService';
import { frameService } from '../services/frameService';

// --- Types ---
interface Point {
  id: string;
  name: string;
  type: '社区' | '线路' | '站点' | '大牌';
  slotTypes: string[];
  totalSlots: number;
  availableSlots: number;
  address: string;
  photos: string[];
  lat?: number;
  lng?: number;
}

interface AdSlot {
  id: string;
  pointName: string;
  mediaType: string;
  location: string;
  specifications: string;
  price: number;
  unit: string;
  status: '正常' | '故障' | '停用' | '到期';
  photos: string[];
  building?: string;
  unitNo?: string;
  elevator?: string;
  direction?: string;
  orientation?: string;
  inOut?: string;
  format?: string;
  illumination?: string;
  traffic?: string;
}

const mediaTypes = ['电梯框架', '道闸', '户外大牌'];

export default function Resources() {
  const [activeTab, setActiveTab] = useState<'points' | 'slots'>('points');
  const [isMapMode, setIsMapMode] = useState(false);
  const [selectedPoint, setSelectedPoint] = useState<Point | null>(null);
  const [selectedSlot, setSelectedSlot] = useState<AdSlot | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedMediaType, setSelectedMediaType] = useState('电梯框架');
  
  // Real data states
  const [communities, setCommunities] = useState<Community[]>([]);
  const [barrierGates, setBarrierGates] = useState<BarrierGate[]>([]);
  const [frames, setFrames] = useState<Frame[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Load real data
  useEffect(() => {
    loadResourcesData();
  }, []);

  const loadResourcesData = async () => {
    try {
      setLoading(true);
      const [communitiesData, barriersData, framesData] = await Promise.all([
        communityService.getAll(),
        barrierGateService.getAll(),
        frameService.getAll()
      ]);
      
      // Handle API response
      const communities = Array.isArray(communitiesData) ? communitiesData : [];
      const barriers = Array.isArray(barriersData) ? barriersData : [];
      const frames = Array.isArray(framesData) ? framesData : [];
      
      setCommunities(communities);
      setBarrierGates(barriers);
      setFrames(frames);
    } catch (err) {
      setError('加载资源数据失败');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Transform real data to Point format
  const points: Point[] = communities.map(community => {
    const communityBarriers = barrierGates.filter(b => b.communityId === community.id);
    const communityFrames = frames.filter(f => f.communityId === community.id);
    const slotTypes: string[] = [];
    if (communityBarriers.length > 0) slotTypes.push('道闸');
    if (communityFrames.length > 0) slotTypes.push('电梯框架');
    
    return {
      id: community.communityNo,
      name: community.buildingName,
      type: '社区' as const,
      slotTypes: slotTypes.length > 0 ? slotTypes : ['电梯框架'],
      totalSlots: communityBarriers.length + communityFrames.length,
      availableSlots: Math.floor((communityBarriers.length + communityFrames.length) * 0.6),
      address: community.buildingAddress,
      photos: ['https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?auto=format&fit=crop&w=800&q=80'],
      lat: community.coordLat,
      lng: community.coordLng
    };
  });

  // Transform real data to AdSlot format
  const allSlots: AdSlot[] = [
    ...frames.map(frame => {
      const community = communities.find(c => c.id === frame.communityId);
      return {
        id: frame.frameNo,
        pointName: community?.buildingName || '未知社区',
        mediaType: '电梯框架',
        location: `${frame.building} ${frame.unit}`,
        specifications: '45cm x 60cm',
        price: 300,
        unit: '周',
        status: (frame.releaseStatus === 0 ? '正常' : frame.releaseStatus === 1 ? '到期' : '停用') as '正常' | '故障' | '停用' | '到期',
        photos: ['https://images.unsplash.com/photo-1559136555-9303baea8ebd?auto=format&fit=crop&w=800&q=80'],
        building: frame.building,
        unitNo: frame.unit,
        elevator: frame.elevator
      };
    }),
    ...barrierGates.map(barrier => {
      const community = communities.find(c => c.id === barrier.communityId);
      return {
        id: barrier.gateNo,
        pointName: community?.buildingName || '未知社区',
        mediaType: '道闸',
        location: barrier.doorLocation,
        specifications: '300cm x 80cm',
        price: 1200,
        unit: '月',
        status: (barrier.releaseStatus === 0 ? '正常' : barrier.releaseStatus === 1 ? '到期' : '停用') as '正常' | '故障' | '停用' | '到期',
        photos: ['https://images.unsplash.com/photo-1621831336117-74b882eb03f3?auto=format&fit=crop&w=800&q=80'],
        direction: barrier.doorLocation,
        orientation: '朝外',
        inOut: '入口'
      };
    })
  ];
  
  const closeDrawer = () => {
    setSelectedPoint(null);
    setSelectedSlot(null);
  };

  const renderDynamicHeaders = () => {
    switch (selectedMediaType) {
      case '电梯框架':
        return <><th className="p-4 font-medium">楼栋</th><th className="p-4 font-medium">单元</th><th className="p-4 font-medium">电梯</th></>;
      case '道闸':
        return <><th className="p-4 font-medium">方位</th><th className="p-4 font-medium">朝向</th><th className="p-4 font-medium">进出口</th></>;
      case '户外大牌':
        return <><th className="p-4 font-medium">形式</th><th className="p-4 font-medium">打光</th><th className="p-4 font-medium">车流量</th></>;
      default:
        return null;
    }
  };

  const renderDynamicCells = (slot: AdSlot) => {
    switch (selectedMediaType) {
      case '电梯框架':
        return <><td className="p-4">{slot.building || '-'}</td><td className="p-4">{slot.unitNo || '-'}</td><td className="p-4">{slot.elevator || '-'}</td></>;
      case '道闸':
        return <><td className="p-4">{slot.direction || '-'}</td><td className="p-4">{slot.orientation || '-'}</td><td className="p-4">{slot.inOut || '-'}</td></>;
      case '户外大牌':
        return <><td className="p-4">{slot.format || '-'}</td><td className="p-4">{slot.illumination || '-'}</td><td className="p-4">{slot.traffic || '-'}</td></>;
      default:
        return null;
    }
  };

  const filteredSlots = allSlots.filter(s => 
    s.mediaType === selectedMediaType &&
    (s.id.includes(searchTerm) || s.pointName.includes(searchTerm))
  );

  if (loading) {
    return (
      <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-slate-500 dark:text-slate-400">加载资源数据中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative items-center justify-center">
        <div className="text-center">
          <p className="text-red-500 mb-4">{error}</p>
          <button 
            onClick={loadResourcesData}
            className="px-4 py-2 bg-primary text-white rounded-lg hover:bg-primary-dark transition-colors"
          >
            重新加载
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      {!isMapMode && (
        <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
          <div className="flex items-center gap-4">
            <h1 className="text-xl font-bold text-slate-900 dark:text-white">资源管理中心</h1>
            <div className="hidden md:flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-lg">
              <button 
                onClick={() => { setActiveTab('points'); closeDrawer(); }}
                className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${activeTab === 'points' ? 'bg-white dark:bg-slate-700 shadow-sm text-primary' : 'text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'}`}
              >
                点位 ({points.length})
              </button>
              <button 
                onClick={() => { setActiveTab('slots'); closeDrawer(); setIsMapMode(false); }}
                className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${activeTab === 'slots' ? 'bg-white dark:bg-slate-700 shadow-sm text-primary' : 'text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white'}`}
              >
                广告位 ({allSlots.length})
              </button>
            </div>
            {activeTab === 'slots' && (
              <div className="flex items-center gap-2 bg-white dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-lg px-3 py-1.5 shadow-sm">
                <Icon name="category" className="text-slate-400" size={18} />
                <select 
                  value={selectedMediaType}
                  onChange={(e) => setSelectedMediaType(e.target.value)}
                  className="bg-transparent border-none text-sm font-medium text-slate-700 dark:text-slate-300 focus:ring-0 p-0 pr-6 cursor-pointer"
                >
                  {mediaTypes.map(type => (
                    <option key={type} value={type}>{type}</option>
                  ))}
                </select>
              </div>
            )}
          </div>
          <button className="bg-primary hover:bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium shadow-md shadow-blue-500/20 transition-colors flex items-center gap-2">
            <Icon name="add" size={18} />
            新增{activeTab === 'points' ? '点位' : '广告位'}
          </button>
        </div>
      )}

      <div className="flex-1 overflow-hidden flex relative">
          {activeTab === 'points' && isMapMode ? (
             <div className="flex-1 flex overflow-hidden">
               <div className="w-80 bg-white dark:bg-surface-dark border-r border-border-light dark:border-border-dark flex flex-col z-10">
                 <div className="p-4 border-b border-border-light dark:border-border-dark flex items-center gap-3">
                   <button onClick={() => setIsMapMode(false)} className="p-1.5 text-slate-500 hover:text-slate-800 dark:hover:text-white hover:bg-slate-100 dark:hover:bg-slate-800 rounded-lg transition-colors">
                     <Icon name="arrow_back" size={20} />
                   </button>
                   <h2 className="font-bold text-lg text-slate-900 dark:text-white">点位分布地图</h2>
                 </div>
                 <div className="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
                   {points.map(point => (
                     <div key={point.id} onClick={() => setSelectedPoint(point)} className={`p-3 rounded-lg cursor-pointer transition-colors ${selectedPoint?.id === point.id ? 'bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800/50' : 'hover:bg-slate-50 dark:hover:bg-slate-800/50 border border-transparent'}`}>
                       <h4 className="font-bold text-sm text-slate-900 dark:text-white">{point.name}</h4>
                       <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">{point.address}</p>
                     </div>
                   ))}
                 </div>
               </div>
               <div className="flex-1 relative bg-[#e5e7eb] dark:bg-[#1e293b]">
                 <div className="w-full h-full flex items-center justify-center text-slate-400">
                   <div className="text-center">
                     <Icon name="map" size={48} className="mb-4" />
                     <p>地图模式 - {points.length} 个点位</p>
                   </div>
                 </div>
               </div>
             </div>
          ) : (
            <div className="flex-1 overflow-y-auto p-6 custom-scrollbar">
              <div className="bg-white dark:bg-surface-dark rounded-xl shadow-sm border border-border-light dark:border-border-dark overflow-hidden flex flex-col">
                <div className="p-4 border-b border-border-light dark:border-border-dark flex justify-between items-center bg-slate-50/50 dark:bg-slate-800/30">
                  <div className="relative w-64">
                    <Icon name="search" className="absolute left-3 top-2 text-slate-400" size={18} />
                    <input type="text" placeholder={`搜索${activeTab === 'points' ? '点位名称...' : '广告位或点位...'}`} className="w-full pl-9 pr-3 py-1.5 bg-white dark:bg-slate-800 border border-border-light dark:border-border-dark rounded-lg text-sm focus:ring-2 focus:ring-primary text-slate-900 dark:text-white" value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} />
                  </div>
                  {activeTab === 'points' && (
                    <button onClick={() => setIsMapMode(true)} className="px-3 py-1.5 text-sm font-medium text-slate-600 dark:text-slate-300 bg-white dark:bg-slate-800 border border-border-light dark:border-border-dark rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors flex items-center gap-1.5">
                      <Icon name="map" size={16} />地图模式
                    </button>
                  )}
                </div>

                <div className="overflow-x-auto">
                  {activeTab === 'points' ? (
                    <table className="w-full text-left border-collapse">
                      <thead>
                        <tr className="bg-slate-50 dark:bg-slate-800/50 border-b border-border-light dark:border-border-dark text-xs uppercase tracking-wider text-slate-500 dark:text-slate-400">
                          <th className="p-4 font-medium">点位名称</th>
                          <th className="p-4 font-medium">类型</th>
                          <th className="p-4 font-medium">包含广告位类型</th>
                          <th className="p-4 font-medium text-center">总数量</th>
                          <th className="p-4 font-medium text-center">空位数</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border-light dark:divide-border-dark text-sm">
                        {points.filter(p => p.name.includes(searchTerm) || p.address.includes(searchTerm)).map(point => (
                          <tr key={point.id} onClick={() => setSelectedPoint(point)} className="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer group">
                            <td className="p-4 font-bold text-slate-900 dark:text-white group-hover:text-primary transition-colors">{point.name}<div className="text-xs font-normal text-slate-500 font-mono mt-0.5">{point.id}</div></td>
                            <td className="p-4"><span className="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300">{point.type}</span></td>
                            <td className="p-4"><div className="flex flex-wrap gap-1">{point.slotTypes.map((type, idx) => (<span key={idx} className="text-[10px] bg-indigo-50 text-indigo-700 dark:bg-indigo-900/30 dark:text-indigo-400 px-1.5 py-0.5 rounded">{type}</span>))}</div></td>
                            <td className="p-4 text-center font-medium text-slate-700 dark:text-slate-300">{point.totalSlots}</td>
                            <td className="p-4 text-center"><span className={`font-bold ${point.availableSlots > 0 ? 'text-emerald-600 dark:text-emerald-400' : 'text-slate-400'}`}>{point.availableSlots}</span></td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  ) : (
                    <table className="w-full text-left border-collapse whitespace-nowrap">
                      <thead>
                        <tr className="bg-slate-50 dark:bg-slate-800/50 border-b border-border-light dark:border-border-dark text-xs uppercase tracking-wider text-slate-500 dark:text-slate-400">
                          <th className="p-4 font-medium">广告位编号</th>
                          <th className="p-4 font-medium">所在点位</th>
                          <th className="p-4 font-medium">媒体类型</th>
                          {renderDynamicHeaders()}
                          <th className="p-4 font-medium">规格</th>
                          <th className="p-4 font-medium">刊例价</th>
                          <th className="p-4 font-medium">状态</th>
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-border-light dark:divide-border-dark text-sm">
                        {filteredSlots.map(slot => (
                          <tr key={slot.id} onClick={() => setSelectedSlot(slot)} className="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors cursor-pointer group">
                            <td className="p-4 font-mono text-xs font-medium text-slate-900 dark:text-white group-hover:text-primary transition-colors">{slot.id}</td>
                            <td className="p-4 font-medium text-slate-700 dark:text-slate-300">{slot.pointName}</td>
                            <td className="p-4"><span className="inline-flex items-center px-2 py-1 rounded text-xs font-medium bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400">{slot.mediaType}</span></td>
                            {renderDynamicCells(slot)}
                            <td className="p-4 text-slate-500 dark:text-slate-400 text-xs">{slot.specifications}</td>
                            <td className="p-4 font-medium text-slate-900 dark:text-white">¥ {slot.price.toLocaleString()} <span className="text-xs text-slate-500 font-normal">/{slot.unit}</span></td>
                            <td className="p-4"><span className={`inline-flex items-center px-2 py-1 rounded text-xs font-medium ${slot.status === '正常' ? 'bg-emerald-100 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-400' : slot.status === '故障' ? 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400' : slot.status === '停用' ? 'bg-slate-200 text-slate-700 dark:bg-slate-700 dark:text-slate-300' : 'bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400'}`}>{slot.status}</span></td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  )}
                </div>
              </div>
            </div>
          )}

        {/* Slide-out Drawer */}
        <div className={`absolute inset-y-0 right-0 w-[400px] bg-white dark:bg-surface-dark shadow-2xl border-l border-border-light dark:border-border-dark transform transition-transform duration-300 ease-in-out flex flex-col z-20 ${selectedPoint || selectedSlot ? 'translate-x-0' : 'translate-x-full'}`}>
          <div className="p-4 border-b border-border-light dark:border-border-dark flex justify-between items-center bg-slate-50 dark:bg-slate-800/50">
            <h2 className="font-bold text-lg text-slate-900 dark:text-white flex items-center gap-2">
              <Icon name={selectedPoint ? "place" : "ad_units"} className="text-primary" size={20} />
              {selectedPoint ? '点位详情' : '广告位详情'}
            </h2>
            <button onClick={closeDrawer} className="p-1.5 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 rounded-md hover:bg-slate-200 dark:hover:bg-slate-700 transition-colors">
              <Icon name="close" size={20} />
            </button>
          </div>
          <div className="flex-1 overflow-y-auto custom-scrollbar p-6 space-y-6">
            {selectedPoint && (
              <div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-1">{selectedPoint.name}</h3>
                <p className="text-sm text-slate-500 mb-4">{selectedPoint.address}</p>
                <div className="aspect-video rounded-lg overflow-hidden border border-border-light dark:border-border-dark mb-4">
                  <img src={selectedPoint.photos[0]} alt="Point" className="w-full h-full object-cover" referrerPolicy="no-referrer" />
                </div>
                <div className="space-y-3 bg-slate-50 dark:bg-slate-800/30 p-4 rounded-xl border border-border-light dark:border-border-dark">
                  <div className="flex justify-between text-sm"><span className="text-slate-500">类型</span><span className="font-medium text-slate-900 dark:text-white">{selectedPoint.type}</span></div>
                  <div className="flex justify-between text-sm"><span className="text-slate-500">总广告位</span><span className="font-medium text-slate-900 dark:text-white">{selectedPoint.totalSlots}</span></div>
                  <div className="flex justify-between text-sm"><span className="text-slate-500">可用空位</span><span className="font-medium text-emerald-600 dark:text-emerald-400">{selectedPoint.availableSlots}</span></div>
                  <div className="flex justify-between text-sm"><span className="text-slate-500">广告位类型</span><span className="font-medium text-slate-900 dark:text-white">{selectedPoint.slotTypes.join(', ')}</span></div>
                </div>
              </div>
            )}
            {selectedSlot && (
              <div>
                <h3 className="text-xl font-bold text-slate-900 dark:text-white mb-1">{selectedSlot.pointName}</h3>
                <p className="text-sm text-primary font-medium mb-4">{selectedSlot.mediaType} - {selectedSlot.id}</p>
                <div className="aspect-video rounded-lg overflow-hidden border border-border-light dark:border-border-dark mb-4">
                  <img src={selectedSlot.photos[0]} alt="Current Ad" className="w-full h-full object-cover" referrerPolicy="no-referrer" />
                </div>
                <div className="space-y-3 bg-slate-50 dark:bg-slate-800/30 p-4 rounded-xl border border-border-light dark:border-border-dark">
                  <div className="flex justify-between text-sm"><span className="text-slate-500">规格尺寸</span><span className="font-medium text-slate-900 dark:text-white">{selectedSlot.specifications}</span></div>
                  <div className="flex justify-between text-sm"><span className="text-slate-500">刊例价</span><span className="font-medium text-emerald-600 dark:text-emerald-400">¥ {selectedSlot.price.toLocaleString()} / {selectedSlot.unit}</span></div>
                  <div className="flex justify-between text-sm"><span className="text-slate-500">位置</span><span className="font-medium text-slate-900 dark:text-white">{selectedSlot.location}</span></div>
                  <div className="flex justify-between text-sm"><span className="text-slate-500">状态</span><span className="font-medium text-slate-900 dark:text-white">{selectedSlot.status}</span></div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
