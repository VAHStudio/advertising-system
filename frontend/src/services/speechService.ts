/**
 * 语音交互服务
 * 通过后端API获取阿里云语音识别token
 */

// 后端API配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:16000';

export interface SpeechRecognitionResult {
  text: string;
  isFinal: boolean;
  confidence?: number;
}

export type SpeechRecognitionCallback = (result: SpeechRecognitionResult) => void;
export type SpeechErrorCallback = (error: Error) => void;

// 语音识别配置接口
interface SpeechConfig {
  token: string;
  wsUrl: string;
  appKey: string;
  expiresAt: number;
  sampleRate: number;
  format: string;
}

class AliyunSpeechService {
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];
  private isRecording = false;
  private onResultCallback: SpeechRecognitionCallback | null = null;
  private onErrorCallback: SpeechErrorCallback | null = null;
  private ws: WebSocket | null = null;
  private taskId: string = '';
  private speechConfig: SpeechConfig | null = null;

  // 生成唯一任务ID
  private generateTaskId(): string {
    return 'task_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
  }

  // 生成UUID
  private generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }

  // 从后端获取语音识别配置
  private async fetchSpeechConfig(): Promise<SpeechConfig> {
    try {
      const response = await fetch(`${API_BASE_URL}/api/speech/token`);
      if (!response.ok) {
        throw new Error('获取语音配置失败');
      }
      const result = await response.json();
      if (result.code !== 200) {
        throw new Error(result.message || '获取语音配置失败');
      }
      return result.data;
    } catch (error) {
      throw new Error('获取语音配置失败: ' + (error as Error).message);
    }
  }

  // 开始语音识别
  async startRecognition(
    onResult: SpeechRecognitionCallback,
    onError: SpeechErrorCallback
  ): Promise<void> {
    try {
      this.onResultCallback = onResult;
      this.onErrorCallback = onError;
      this.audioChunks = [];
      this.isRecording = true;
      this.taskId = this.generateTaskId();

      // 从后端获取配置
      this.speechConfig = await this.fetchSpeechConfig();

      // 获取麦克风权限
      const stream = await navigator.mediaDevices.getUserMedia({ 
        audio: {
          sampleRate: this.speechConfig.sampleRate,
          channelCount: 1,
          echoCancellation: true,
          noiseSuppression: true,
        } 
      });

      // 设置MediaRecorder
      this.mediaRecorder = new MediaRecorder(stream, {
        mimeType: 'audio/webm;codecs=opus'
      });

      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          this.audioChunks.push(event.data);
        }
      };

      this.mediaRecorder.onstop = async () => {
        await this.processAudio();
      };

      // 连接阿里云NLS服务
      await this.connectToNLS();

      // 开始录音
      this.mediaRecorder.start(100); // 每100ms收集一次数据

    } catch (error) {
      this.handleError(error as Error);
    }
  }

  // 连接到阿里云NLS服务
  private async connectToNLS(): Promise<void> {
    try {
      if (!this.speechConfig) {
        throw new Error('语音配置未初始化');
      }

      this.ws = new WebSocket(this.speechConfig.wsUrl);
      
      this.ws.onopen = () => {
        // 发送开始指令
        const startCmd = {
          header: {
            message_id: this.generateUUID(),
            task_id: this.taskId,
            namespace: 'SpeechRecognizer',
            name: 'StartRecognition',
            appkey: this.speechConfig!.appKey,
          },
          payload: {
            format: this.speechConfig!.format,
            sample_rate: this.speechConfig!.sampleRate,
            enable_intermediate_result: true,
            enable_punctuation_prediction: true,
            enable_inverse_text_normalization: true,
          }
        };
        this.ws?.send(JSON.stringify(startCmd));
      };

      this.ws.onmessage = (event) => {
        const response = JSON.parse(event.data);
        this.handleNLSResponse(response);
      };

      this.ws.onerror = (error) => {
        this.handleError(new Error('WebSocket连接错误'));
      };

      this.ws.onclose = () => {
        if (this.isRecording) {
          this.handleError(new Error('WebSocket连接已关闭'));
        }
      };

    } catch (error) {
      this.handleError(error as Error);
    }
  }

  // 处理NLS响应
  private handleNLSResponse(response: any): void {
    const { header, payload } = response;
    
    if (header.name === 'RecognitionResultChanged') {
      // 中间结果
      if (this.onResultCallback) {
        this.onResultCallback({
          text: payload.result,
          isFinal: false,
          confidence: payload.confidence,
        });
      }
    } else if (header.name === 'RecognitionCompleted') {
      // 最终结果
      if (this.onResultCallback) {
        this.onResultCallback({
          text: payload.result,
          isFinal: true,
          confidence: payload.confidence,
        });
      }
      this.stopRecognition();
    } else if (header.name === 'RecognitionFailed') {
      this.handleError(new Error(payload.message || '识别失败'));
    }
  }

  // 处理音频数据
  private async processAudio(): Promise<void> {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;

    const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' });
    
    // 将音频数据发送到NLS服务
    // 实际使用时需要将音频转换为PCM格式并分段发送
    // 这里简化处理，实际项目中需要更复杂的音频处理
    
    // 发送停止指令
    const stopCmd = {
      header: {
        message_id: this.generateUUID(),
        task_id: this.taskId,
        namespace: 'SpeechRecognizer',
        name: 'StopRecognition',
        appkey: this.speechConfig!.appKey,
      }
    };
    this.ws.send(JSON.stringify(stopCmd));
  }

  // 停止语音识别
  stopRecognition(): void {
    this.isRecording = false;
    
    if (this.mediaRecorder && this.mediaRecorder.state !== 'inactive') {
      this.mediaRecorder.stop();
      this.mediaRecorder.stream.getTracks().forEach(track => track.stop());
    }

    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }

    this.mediaRecorder = null;
    this.audioChunks = [];
    this.speechConfig = null;
  }

  // 处理错误
  private handleError(error: Error): void {
    this.stopRecognition();
    if (this.onErrorCallback) {
      this.onErrorCallback(error);
    }
  }

  // 检查是否正在录音
  isRecognizing(): boolean {
    return this.isRecording;
  }
}

// 导出单例实例
export const speechService = new AliyunSpeechService();

// 导出简化版语音识别（使用Web Speech API作为备选）
export function useWebSpeechRecognition(): {
  start: (onResult: SpeechRecognitionCallback, onError: SpeechErrorCallback) => void;
  stop: () => void;
  isSupported: boolean;
} {
  const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;
  
  if (!SpeechRecognition) {
    return {
      start: () => {},
      stop: () => {},
      isSupported: false,
    };
  }

  let recognition: any = null;

  return {
    start: (onResult, onError) => {
      try {
        recognition = new SpeechRecognition();
        recognition.lang = 'zh-CN';
        recognition.continuous = true;
        recognition.interimResults = true;

        recognition.onresult = (event: any) => {
          let finalTranscript = '';
          let interimTranscript = '';

          for (let i = event.resultIndex; i < event.results.length; i++) {
            const transcript = event.results[i][0].transcript;
            if (event.results[i].isFinal) {
              finalTranscript += transcript;
            } else {
              interimTranscript += transcript;
            }
          }

          onResult({
            text: finalTranscript || interimTranscript,
            isFinal: !!finalTranscript,
          });
        };

        recognition.onerror = (event: any) => {
          onError(new Error(event.error));
        };

        recognition.start();
      } catch (error) {
        onError(error as Error);
      }
    },
    stop: () => {
      if (recognition) {
        recognition.stop();
      }
    },
    isSupported: true,
  };
}
