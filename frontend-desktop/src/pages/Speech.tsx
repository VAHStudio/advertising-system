import { useState, useEffect } from 'react';
import { speechService } from '../services/speechService';
import { Icon } from '../components/Icon';

export default function Speech() {
  const [config, setConfig] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadConfig();
  }, []);

  const loadConfig = async () => {
    try {
      setLoading(true);
      const data = await speechService.getConfig();
      setConfig(data);
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

  if (error) {
    return (
      <div className="flex items-center justify-center h-full">
        <div className="text-red-500">{error}</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text-light dark:text-text-dark">语音交互</h1>
        <p className="text-subtext-light dark:text-subtext-dark mt-1">使用语音与系统交互</p>
      </div>

      <div className="bg-surface-light dark:bg-surface-dark rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-8">
        <div className="text-center">
          <div className="w-24 h-24 mx-auto mb-6 rounded-full bg-gradient-to-br from-primary to-accent flex items-center justify-center">
            <Icon name="mic" size={40} className="text-white" />
          </div>
          <h2 className="text-xl font-semibold text-text-light dark:text-text-dark mb-2">语音输入</h2>
          <p className="text-subtext-light dark:text-subtext-dark mb-6">点击按钮开始语音输入</p>
          <button className="px-8 py-3 bg-primary text-white rounded-full hover:bg-primary/90 transition-colors shadow-lg hover:shadow-xl">
            开始录音
          </button>
        </div>

        {config && (
          <div className="mt-8 pt-6 border-t border-gray-200 dark:border-gray-700">
            <h3 className="font-semibold text-text-light dark:text-text-dark mb-4">配置信息</h3>
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-subtext-light dark:text-subtext-dark">应用Key:</span>
                <span className="ml-2 text-text-light dark:text-text-dark">{config.appKey || '未配置'}</span>
              </div>
              <div>
                <span className="text-subtext-light dark:text-subtext-dark">区域:</span>
                <span className="ml-2 text-text-light dark:text-text-dark">{config.region || '未配置'}</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
