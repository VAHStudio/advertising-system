import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Resources from './pages/Resources';
import DevelopmentContracts from './pages/DevelopmentContracts';
import Customers from './pages/Customers';
import Plans from './pages/Plans';
import SalesContracts from './pages/SalesContracts';
import Inventory from './pages/Inventory';
import Execution from './pages/Execution';
import Analytics from './pages/Analytics';
import Settings from './pages/Settings';
import AIAssistant from './components/AIAssistant';
import Layout from './components/Layout';
import Communities from './pages/Communities';
import BarrierGates from './pages/BarrierGates';
import Frames from './pages/Frames';
import PlanCommunities from './pages/PlanCommunities';
import PlanBarriers from './pages/PlanBarriers';
import PlanFrames from './pages/PlanFrames';
import AISelection from './pages/AISelection';
import AIAssistantPage from './pages/AIAssistantPage';
import Speech from './pages/Speech';

export default function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background-light dark:bg-background-dark transition-colors duration-200">
        <Routes>
          <Route element={<Layout />}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/resources" element={<Resources />} />
            <Route path="/dev-contracts" element={<DevelopmentContracts />} />
            <Route path="/customers" element={<Customers />} />
            <Route path="/plans" element={<Plans />} />
            <Route path="/sales-contracts" element={<SalesContracts />} />
            <Route path="/inventory" element={<Inventory />} />
            <Route path="/execution" element={<Execution />} />
            <Route path="/analytics" element={<Analytics />} />
            <Route path="/settings" element={<Settings />} />
            <Route path="/communities" element={<Communities />} />
            <Route path="/barrier-gates" element={<BarrierGates />} />
            <Route path="/frames" element={<Frames />} />
            <Route path="/plan-communities" element={<PlanCommunities />} />
            <Route path="/plan-barriers" element={<PlanBarriers />} />
            <Route path="/plan-frames" element={<PlanFrames />} />
            <Route path="/ai-selection" element={<AISelection />} />
            <Route path="/ai-assistant" element={<AIAssistantPage />} />
            <Route path="/speech" element={<Speech />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
        <AIAssistant />
      </div>
    </Router>
  );
}
