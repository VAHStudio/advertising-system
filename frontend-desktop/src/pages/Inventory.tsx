import React, { useState, useEffect } from 'react';
import { communityService } from '../services/communityService';
import { frameService } from '../services/frameService';
import { Icon } from '../components/Icon';

interface SlotItem {
  id: string;
  frameNo: string;
  communityName: string;
  building: string;
  unit: string;
  elevator: string;
  currentClient: string;
  bookingInfo: string;
  nextAvailableDate: string;
  status: 'in-use' | 'available' | 'booked';
  bookings: Array<{
    client: string;
    startDate: number;
    endDate: number;
    type: 'in-use' | 'booked';
  }>;
}

export default function Inventory() {
  const [viewMode, setViewMode] = useState<'list' | 'calendar'>('list');
  const [mediaType, setMediaType] = useState('电梯框架');
  const [searchTerm, setSearchTerm] = useState('');
  const [slots, setSlots] = useState<SlotItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadInventoryData();
  }, []);

  const loadInventoryData = async () => {
    try {
      setLoading(true);
      const [communitiesData, framesData] = await Promise.all([
        communityService.getAll(),
        frameService.getAll()
      ]);

      const communities = Array.isArray(communitiesData) ? communitiesData : [];
      const frames = Array.isArray(framesData) ? framesData : [];

      const items: SlotItem[] = frames.slice(0, 20).map((frame: any, idx: number) => {
        const community = communities.find((c: any) => c.id === frame.communityId);
        const status = frame.releaseStatus === 0 ? 'available' : frame.releaseStatus === 1 ? 'in-use' : 'booked';
        
        // Mock booking data for calendar view
        const bookings = [];
        if (status === 'in-use') {
          bookings.push({ client: '星空传媒', startDate: 3, endDate: 15, type: 'in-use' as const });
        } else if (status === 'booked') {
          bookings.push({ client: '绿生活', startDate: 16, endDate: 25, type: 'booked' as const });
        }
        
        return {
          id: frame.frameNo || `FR-${String(idx + 1).padStart(3, '0')}`,
          frameNo: frame.frameNo || `FR-${String(idx + 1).padStart(3, '0')}`,
          communityName: community?.buildingName || '未知社区',
          building: frame.building || 'A栋',
          unit: frame.unit || '1单元',
          elevator: frame.elevator || '客梯1',
          currentClient: status === 'in-use' ? '星空传媒' : '-',
          bookingInfo: status === 'booked' ? '绿生活 (08.15起)' : status === 'in-use' ? '无' : '-',
          nextAvailableDate: status === 'available' ? '现在' : '2024-09-01',
          status,
          bookings
        };
      });

      setSlots(items);
    } catch (err) {
      console.error('加载库存数据失败:', err);
    } finally {
      setLoading(false);
    }
  };

  const filteredSlots = slots.filter(slot => 
    slot.communityName.includes(searchTerm) || 
    slot.frameNo.includes(searchTerm)
  );

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'in-use':
        return <span className="px-3 py-1 rounded-full text-xs font-medium bg-blue-500/20 text-blue-400">在刊</span>;
      case 'available':
        return <span className="px-3 py-1 rounded-full text-xs font-medium bg-emerald-500/20 text-emerald-400">空闲</span>;
      case 'booked':
        return <span className="px-3 py-1 rounded-full text-xs font-medium bg-amber-500/20 text-amber-400">预定</span>;
      default:
        return null;
    }
  };

  // Generate 30 days for calendar
  const days = Array.from({ length: 30 }, (_, i) => i + 1);

  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white">销控查询</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">实时查询媒体资源的销售状态与档期。</p>
        </div>
        <div className="flex items-center gap-4">
          {/* Media Type Dropdown */}
          <div className="flex items-center gap-2 bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-lg px-3 py-2">
            <Icon name="layers" className="text-slate-400" size={20} />
            <select 
              value={mediaType}
              onChange={(e) => setMediaType(e.target.value)}
              className="bg-transparent border-none text-sm font-medium text-slate-700 dark:text-slate-300 focus:ring-0 p-0 pr-6 cursor-pointer"
            >
              <option value="电梯框架">电梯框架</option>
              <option value="道闸">道闸</option>
              <option value="户外大牌">户外大牌</option>
            </select>
          </div>

          {/* View Toggle */}
          <div className="flex items-center bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-lg p-1">
            <button
              onClick={() => setViewMode('list')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'list' 
                  ? 'bg-primary text-white' 
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
              }`}
            >
              <Icon name="list" size={16} />
              列表视图
            </button>
            <button
              onClick={() => setViewMode('calendar')}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-md text-sm font-medium transition-colors ${
                viewMode === 'calendar' 
                  ? 'bg-primary text-white' 
                  : 'text-slate-500 dark:text-slate-400 hover:text-slate-700 dark:hover:text-slate-200'
              }`}
            >
              <Icon name="calendar_view_month" size={16} />
              档期图
            </button>
          </div>
        </div>
      </div>

      <div className="flex-1 overflow-hidden flex flex-col">
          {/* Search Bar */}
          <div className="p-6 pb-0">
            <div className="flex items-center gap-4">
              <div className="relative flex-1 max-w-md">
                <Icon name="search" className="absolute left-3 top-2.5 text-slate-400" size={20} />
                <input 
                  type="text" 
                  placeholder="搜索点位名称或编号..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-10 pr-4 py-2.5 bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-xl text-sm focus:ring-2 focus:ring-primary text-slate-900 dark:text-white placeholder-slate-400"
                />
              </div>
              <button className="flex items-center gap-2 px-4 py-2.5 bg-surface-light dark:bg-surface-dark border border-border-light dark:border-border-dark rounded-xl text-sm font-medium text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors">
                <Icon name="filter_list" size={20} />
                高级筛选
              </button>
            </div>
          </div>

          {/* Content */}
          <div className="flex-1 overflow-y-auto custom-scrollbar p-6">
            {viewMode === 'list' ? (
              /* List View */
              <div className="bg-surface-light dark:bg-surface-dark rounded-2xl border border-border-light dark:border-border-dark overflow-hidden">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="bg-slate-50 dark:bg-slate-800/50 border-b border-border-light dark:border-border-dark text-xs uppercase tracking-wider text-slate-500 dark:text-slate-400">
                      <th className="p-4 font-medium">广告位编号</th>
                      <th className="p-4 font-medium">点位名称</th>
                      <th className="p-4 font-medium">楼栋</th>
                      <th className="p-4 font-medium">单元</th>
                      <th className="p-4 font-medium">电梯</th>
                      <th className="p-4 font-medium">当前在刊客户</th>
                      <th className="p-4 font-medium">方案预定信息</th>
                      <th className="p-4 font-medium">最近可订时间</th>
                      <th className="p-4 font-medium">状态</th>
                      <th className="p-4 font-medium">操作</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-border-light dark:divide-border-dark text-sm">
                    {filteredSlots.map((slot) => (
                      <tr key={slot.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/50 transition-colors">
                        <td className="p-4 font-mono text-xs text-slate-900 dark:text-white">{slot.frameNo}</td>
                        <td className="p-4 font-bold text-slate-900 dark:text-white">{slot.communityName}</td>
                        <td className="p-4 text-slate-600 dark:text-slate-300">{slot.building}</td>
                        <td className="p-4 text-slate-600 dark:text-slate-300">{slot.unit}</td>
                        <td className="p-4 text-slate-600 dark:text-slate-300">{slot.elevator}</td>
                        <td className="p-4">
                          {slot.currentClient !== '-' ? (
                            <span className="text-blue-400">{slot.currentClient}</span>
                          ) : (
                            <span className="text-slate-400">-</span>
                          )}
                        </td>
                        <td className="p-4 text-slate-600 dark:text-slate-300">{slot.bookingInfo}</td>
                        <td className="p-4">
                          {slot.nextAvailableDate === '现在' ? (
                            <span className="text-emerald-400">现在</span>
                          ) : (
                            <span className="text-slate-600 dark:text-slate-300">{slot.nextAvailableDate}</span>
                          )}
                        </td>
                        <td className="p-4">{getStatusBadge(slot.status)}</td>
                        <td className="p-4">
                          <button className="text-blue-400 hover:text-blue-300 text-sm font-medium transition-colors">
                            加入预定
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                {filteredSlots.length === 0 && (
                  <div className="p-8 text-center text-slate-500 dark:text-slate-400">
                    暂无数据
                  </div>
                )}
              </div>
            ) : (
              /* Calendar View (Gantt) */
              <div className="bg-surface-light dark:bg-surface-dark rounded-2xl border border-border-light dark:border-border-dark overflow-hidden">
                {/* Legend */}
                <div className="p-4 border-b border-border-light dark:border-border-dark flex items-center gap-6">
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 rounded-full bg-blue-500"></span>
                    <span className="text-sm text-slate-600 dark:text-slate-300">在刊档期</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 rounded-full bg-amber-500"></span>
                    <span className="text-sm text-slate-600 dark:text-slate-300">预定档期</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="w-3 h-3 rounded-full border-2 border-slate-400"></span>
                    <span className="text-sm text-slate-600 dark:text-slate-300">空闲档期</span>
                  </div>
                </div>

                {/* Gantt Chart */}
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse" style={{ minWidth: '1000px' }}>
                    <thead>
                      <tr className="bg-slate-50 dark:bg-slate-800/50 border-b border-border-light dark:border-border-dark">
                        <th className="p-4 font-medium text-slate-500 dark:text-slate-400 text-sm w-48">广告位</th>
                        {days.map(day => (
                          <th key={day} className="p-2 text-center text-xs text-slate-500 dark:text-slate-400 w-8">
                            {day}日
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-border-light dark:divide-border-dark">
                      {filteredSlots.map((slot) => (
                        <tr key={slot.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/30 transition-colors">
                          <td className="p-4 border-r border-border-light dark:border-border-dark">
                            <div className="font-bold text-slate-900 dark:text-white text-sm">{slot.communityName}</div>
                            <div className="text-xs text-slate-500 font-mono mt-0.5">{slot.frameNo}</div>
                          </td>
                          {days.map(day => {
                            const booking = slot.bookings.find(b => day >= b.startDate && day <= b.endDate);
                            return (
                              <td key={day} className="p-0 relative h-12">
                                {booking && (
                                  <div 
                                    className={`absolute inset-0.5 rounded flex items-center justify-center text-xs font-medium text-white overflow-hidden ${
                                      booking.type === 'in-use' ? 'bg-blue-500' : 'bg-amber-500'
                                    }`}
                                    style={{
                                      left: day === booking.startDate ? '4px' : '0',
                                      right: day === booking.endDate ? '4px' : '0'
                                    }}
                                  >
                                    {day === Math.floor((booking.startDate + booking.endDate) / 2) && booking.client}
                                  </div>
                                )}
                              </td>
                            );
                          })}
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                {filteredSlots.length === 0 && (
                  <div className="p-8 text-center text-slate-500 dark:text-slate-400">
                    暂无数据
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
  );
}
