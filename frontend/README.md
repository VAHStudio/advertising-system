# 永达传媒前端 - React + TypeScript

## 项目简介

这是永达传媒AI广告投放管理系统的前端部分，使用React + TypeScript + Vite构建。

## 技术栈

- **React 18** - UI框架
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Tailwind CSS** - 样式框架
- **Framer Motion** - 动画库
- **Lucide React** - 图标库

## 快速开始

### 安装依赖

```bash
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

## 项目结构

```
src/
├── components/
│   ├── AgentChatView.tsx      # AI对话界面
│   ├── VoiceInputModal.tsx    # 语音输入弹窗
│   └── index.ts               # 组件导出
├── services/
│   ├── agentService.ts        # Agent API服务
│   ├── agentsApi.ts           # AI Agents API
│   ├── speechService.ts       # 语音识别服务
│   └── index.ts               # 服务导出
├── App.tsx                    # 主应用组件
├── main.tsx                   # 入口文件
├── types.ts                   # TypeScript类型定义
└── index.css                  # 全局样式
```

## 主要功能

### 1. 投小智首页
- 智能输入框（支持打字/语音）
- 永达传媒AI团队展示
- 四个专业AI智能体入口

### 2. AI对话界面
- 多轮对话上下文
- 消息历史展示
- 智能意图识别结果展示

### 3. 方案管理
- 方案列表
- 方案详情
- 通过AI对话创建方案

## 环境变量

如需配置API地址，创建 `.env.local`：

```
VITE_API_BASE_URL=http://localhost:16000
```

## 开发说明

### 添加新页面

在 `App.tsx` 中添加新的 ViewType 和对应的组件。

### 调用后端API

使用 `services/agentService.ts` 中的方法：

```typescript
import { sendAgentMessage, getAgentList } from './services/agentService';

// 发送消息
const response = await sendAgentMessage({
  message: '请帮我创建方案',
  sessionId: conversationId
});

// 获取智能体列表
const agents = await getAgentList();
```

## 注意事项

- 后端服务需运行在 http://localhost:16000
- 确保后端已启动后再启动前端
- 首次使用需安装所有依赖
