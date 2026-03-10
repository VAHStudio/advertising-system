import type { NavigationAction, ToastMessage } from '../../types/aiAssistant';

export type { NavigationAction, ToastMessage };

export interface QuickCommand {
  label: string;
  command: string;
  icon: string;
}

export interface AIChatPageProps {
  title?: string;
  subtitle?: string;
  quickCommands?: QuickCommand[];
  placeholder?: string;
}
