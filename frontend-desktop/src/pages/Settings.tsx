import React from 'react';

export default function Settings() {
  return (
    <div className="flex-1 flex flex-col min-w-0 bg-slate-50 dark:bg-[#0B1120] relative">
      <div className="flex items-center justify-between px-6 py-4 bg-white dark:bg-surface-dark border-b border-border-light dark:border-border-dark">
        <div>
          <h1 className="text-xl font-bold text-slate-900 dark:text-white">系统设置</h1>
          <p className="text-sm text-slate-500 dark:text-slate-400">管理您的账户和系统偏好设置。</p>
        </div>
      </div>
      
      <div className="flex-1 overflow-y-auto custom-scrollbar p-8">
        <div className="max-w-4xl mx-auto bg-white dark:bg-surface-dark rounded-2xl shadow-sm border border-border-light dark:border-border-dark p-8">
          <h2 className="text-xl font-semibold mb-4">设置选项</h2>
          <p className="text-slate-500 dark:text-slate-400">设置页面内容正在开发中...</p>
        </div>
      </div>
    </div>
  );
}
