/**
 * 语音服务
 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:16000/api';

export interface SpeechConfig {
  appKey?: string;
  region?: string;
  token?: string;
}

export const speechService = {
  getToken: async (): Promise<{ token: string; expireTime: number }> => {
    const response = await fetch(`${API_BASE_URL}/speech/token`);
    const result = await response.json();
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message || '获取Token失败');
  },

  getConfig: async (): Promise<SpeechConfig> => {
    const response = await fetch(`${API_BASE_URL}/speech/config`);
    const result = await response.json();
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message || '获取配置失败');
  },
};

export default speechService;
