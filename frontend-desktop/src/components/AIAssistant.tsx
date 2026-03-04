import { Icon } from '../../components/Icon'
import React, { useState, useRef, useEffect } from 'react';

type AssistantType = 'sales' | 'media' | 'engineering' | 'finance' | null;

const assistants = [
  { id: 'sales', name: '销售AI助理', icon: 'support_agent', desc: '解答销售政策、客户分析', color: 'bg-blue-500' },
  { id: 'media', name: '媒介AI助理', icon: 'perm_media', desc: '点位推荐、排期查询', color: 'bg-purple-500' },
  { id: 'engineering', name: '工程AI助理', icon: 'engineering', desc: '上刊进度、维修指导', color: 'bg-orange-500' },
  { id: 'finance', name: '财务AI助理', icon: 'account_balance', desc: '合同审批、回款查询', color: 'bg-emerald-500' },
];

export default function AIAssistant() {
  const [isOpen, setIsOpen] = useState(false);
  const [activeAssistant, setActiveAssistant] = useState<AssistantType>(null);
  const [messages, setMessages] = useState<{role: 'user'|'ai', text: string}[]>([]);
  const [inputValue, setInputValue] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleOpen = () => {
    setIsOpen(true);
  };

  const handleClose = () => {
    setIsOpen(false);
    setTimeout(() => {
      setActiveAssistant(null);
      setMessages([]);
    }, 300);
  };

  const selectAssistant = (id: AssistantType) => {
    setActiveAssistant(id);
    const assistant = assistants.find(a => a.id === id);
    setMessages([
      { role: 'ai', text: `你好！我是${assistant?.name}，有什么我可以帮您的？` }
    ]);
  };

  const handleSend = () => {
    if (!inputValue.trim()) return;
    
    const newMsg = inputValue.trim();
    setMessages(prev => [...prev, { role: 'user', text: newMsg }]);
    setInputValue('');
    
    // Mock AI response
    setTimeout(() => {
      setMessages(prev => [...prev, { role: 'ai', text: `这是${assistants.find(a => a.id === activeAssistant)?.name}的模拟回复。在实际应用中，这里将接入大模型API来回答关于"${newMsg}"的问题。` }]);
    }, 1000);
  };

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* Chat Window */}
      <div 
        className={`absolute bottom-0 right-0 w-80 sm:w-96 bg-white dark:bg-surface-dark rounded-2xl shadow-2xl border border-border-light dark:border-border-dark overflow-hidden flex flex-col origin-bottom-right transition-all duration-300 ease-out ${
          isOpen ? 'scale-100 opacity-100 translate-y-0 pointer-events-auto' : 'scale-50 opacity-0 translate-y-8 pointer-events-none'
        }`} 
        style={{ height: '500px', maxHeight: 'calc(100vh - 120px)' }}
      >
        {!activeAssistant ? (
          // Assistant Selection
          <div className="flex flex-col h-full">
            <div className="p-4 bg-primary text-white flex justify-between items-center">
              <h3 className="font-bold text-lg">选择 AI 助理</h3>
              <button onClick={handleClose} className="text-white/80 hover:text-white transition-colors">
                <span className="material-icons-outlined">close</span>
              </button>
            </div>
            <div className="p-4 flex-1 overflow-y-auto bg-slate-50 dark:bg-[#0B1120] custom-scrollbar">
              <p className="text-sm text-slate-500 dark:text-slate-400 mb-4">请选择您需要咨询的专业领域助理：</p>
              <div className="space-y-3">
                {assistants.map(assistant => (
                  <button
                    key={assistant.id}
                    onClick={() => selectAssistant(assistant.id as AssistantType)}
                    className="w-full bg-white dark:bg-slate-800 p-4 rounded-xl border border-border-light dark:border-border-dark hover:border-primary dark:hover:border-primary hover:shadow-md transition-all flex items-center gap-4 group text-left"
                  >
                    <div className={`w-12 h-12 rounded-full ${assistant.color} flex items-center justify-center text-white flex-shrink-0 group-hover:scale-110 transition-transform`}>
                      <span className="material-icons-outlined">{assistant.icon}</span>
                    </div>
                    <div>
                      <h4 className="font-bold text-slate-900 dark:text-white group-hover:text-primary transition-colors">{assistant.name}</h4>
                      <p className="text-xs text-slate-500 dark:text-slate-400 mt-1">{assistant.desc}</p>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          </div>
        ) : (
          // Chat Interface
          <div className="flex flex-col h-full">
            <div className="p-4 bg-primary text-white flex justify-between items-center shadow-md z-10">
              <div className="flex items-center gap-3">
                <button onClick={() => setActiveAssistant(null)} className="text-white/80 hover:text-white transition-colors flex items-center">
                  <span className="material-icons-outlined">arrow_back</span>
                </button>
                <div className="flex items-center gap-2">
                  <div className={`w-8 h-8 rounded-full ${assistants.find(a => a.id === activeAssistant)?.color} flex items-center justify-center text-white border-2 border-white/20`}>
                    <span className="material-icons-outlined text-[16px]">{assistants.find(a => a.id === activeAssistant)?.icon}</span>
                  </div>
                  <h3 className="font-bold">{assistants.find(a => a.id === activeAssistant)?.name}</h3>
                </div>
              </div>
              <button onClick={handleClose} className="text-white/80 hover:text-white transition-colors">
                <span className="material-icons-outlined">close</span>
              </button>
            </div>
            
            <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-slate-50 dark:bg-[#0B1120] custom-scrollbar">
              {messages.map((msg, idx) => (
                <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
                  <div className={`max-w-[80%] p-3 rounded-2xl text-sm ${
                    msg.role === 'user' 
                      ? 'bg-primary text-white rounded-tr-sm' 
                      : 'bg-white dark:bg-slate-800 text-slate-800 dark:text-slate-200 border border-border-light dark:border-border-dark rounded-tl-sm shadow-sm'
                  }`}>
                    {msg.text}
                  </div>
                </div>
              ))}
              <div ref={messagesEndRef} />
            </div>
            
            <div className="p-3 bg-white dark:bg-surface-dark border-t border-border-light dark:border-border-dark">
              <div className="flex items-center gap-2 bg-slate-100 dark:bg-slate-800 rounded-full p-1 pr-2">
                <input 
                  type="text" 
                  value={inputValue}
                  onChange={(e) => setInputValue(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                  placeholder="输入您的问题..." 
                  className="flex-1 bg-transparent border-none focus:ring-0 text-sm px-4 py-2 text-slate-900 dark:text-white placeholder-slate-500"
                />
                <button 
                  onClick={handleSend}
                  disabled={!inputValue.trim()}
                  className="w-8 h-8 rounded-full bg-primary text-white flex items-center justify-center disabled:opacity-50 disabled:cursor-not-allowed transition-opacity"
                >
                  <span className="material-icons-outlined text-[16px]">send</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Floating Button */}
      <button 
        onClick={handleOpen}
        className={`absolute bottom-0 right-0 group flex items-center gap-3 bg-white dark:bg-surface-dark p-3 rounded-2xl shadow-lg border border-border-light dark:border-border-dark hover:border-primary dark:hover:border-primary hover:shadow-xl transition-all duration-300 ease-out origin-bottom-right ${
          isOpen ? 'scale-50 opacity-0 pointer-events-none' : 'scale-100 opacity-100 hover:-translate-y-1'
        }`}
      >
        <div className="w-12 h-12 rounded-xl bg-[#0ea5e9] flex items-center justify-center text-white overflow-hidden shadow-inner relative flex-shrink-0">
          <svg viewBox="0 0 100 100" className="w-full h-full text-white fill-current">
            {/* Main board */}
            <polygon points="12,18 88,35 88,62 12,55" />
            {/* Left leg */}
            <polygon points="31,56 35,56 35,80 31,80" />
            {/* Right leg */}
            <polygon points="74,60 77,60 76,80 73,80" />
            {/* Ground curve */}
            <path d="M 8,81 Q 50,79 92,81 Z" />
          </svg>
        </div>
        <div className="flex flex-col justify-center min-w-[80px]">
          <div className="flex justify-center gap-1.5 w-full font-bold text-lg leading-none text-slate-700 dark:text-slate-200 group-hover:text-primary transition-colors">
            <span>投</span>
            <span>小</span>
            <span>智</span>
          </div>
          <span className="text-[10px] text-slate-400 font-medium mt-1.5 whitespace-nowrap text-center">永达传媒AI员工</span>
        </div>
      </button>
    </div>
  );
}
