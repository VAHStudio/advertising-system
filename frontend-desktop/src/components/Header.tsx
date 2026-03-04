import React, { useState, useEffect } from 'react';
import { Icon } from './Icon';

interface HeaderProps {
  title: string;
  subtitle?: string;
  children?: React.ReactNode;
}

export default function Header({ title, subtitle, children }: HeaderProps) {
  const [isDarkMode, setIsDarkMode] = useState(false);

  useEffect(() => {
    // Initialize state based on current document class
    setIsDarkMode(document.documentElement.classList.contains('dark'));
  }, []);

  const toggleDarkMode = () => {
    const isDark = document.documentElement.classList.toggle('dark');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    setIsDarkMode(isDark);
  };

  return (
    <header className="h-16 bg-surface-light dark:bg-surface-dark/80 backdrop-blur-md sticky top-0 z-10 border-b border-gray-200 dark:border-gray-700 px-6 lg:px-8 flex items-center justify-between flex-shrink-0">
      <div>
        <h2 className="text-xl font-display font-bold text-text-light dark:text-text-dark">{title}</h2>
        {subtitle && <p className="text-xs text-subtext-light dark:text-subtext-dark">{subtitle}</p>}
      </div>
      <div className="flex items-center gap-4">
        {children}
        <div className="hidden md:flex items-center gap-2 border-l border-gray-200 dark:border-gray-700 pl-4 ml-2">
          <button className="relative p-2 text-subtext-light dark:text-subtext-dark hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors">
            <Icon name="notifications" size={20} />
            <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 rounded-full border-2 border-white dark:border-surface-dark"></span>
          </button>
          <button className="p-2 text-subtext-light dark:text-subtext-dark hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors flex items-center justify-center w-10 h-10" onClick={toggleDarkMode}>
            <Icon name={isDarkMode ? 'light_mode' : 'dark_mode'} size={20} />
          </button>
        </div>
      </div>
    </header>
  );
}
