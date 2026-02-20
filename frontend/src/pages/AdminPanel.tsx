import { useState } from 'react';
import { THEME } from '../constants/theme';
import { CreateSection } from '../components/admin/CreateSection';
import { ManageSections } from '../components/admin/ManageSections';
import { ScheduleMeetings } from '../components/admin/ScheduleMeetings';

type TabType = 'create' | 'manage' | 'schedule';

interface TabConfig {
  id: TabType;
  label: string;
  icon: string;
}

const TABS: TabConfig[] = [
  { id: 'create', label: 'Create Section', icon: '➕' },
  { id: 'manage', label: 'Manage Sections', icon: '📋' },
  { id: 'schedule', label: 'Schedule Meetings', icon: '📅' },
];

export function AdminPanel() {
  const [activeTab, setActiveTab] = useState<TabType>('create');
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  const handleSectionCreated = () => {
    setRefreshTrigger((prev) => prev + 1);
    setActiveTab('manage');
  };

  return (
    <div className="w-full">
      {/* Header */}
      <div
        className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 mb-8 border-2 ${THEME.colors.borders.light}`}
      >
        <h1 className={`text-3xl font-bold ${THEME.colors.text.primary} mb-2`}>
          🔧 Admin Panel
        </h1>
        <p className={`${THEME.colors.text.secondary}`}>
          Create course sections, manage offerings, and schedule meetings
        </p>
      </div>

      {/* Tabs Navigation */}
      <div className="flex gap-4 mb-8 overflow-x-auto">
        {TABS.map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-6 py-3 rounded-xl font-semibold transition-all whitespace-nowrap ${
              activeTab === tab.id
                ? `bg-gradient-to-r ${THEME.colors.gradients.button} text-white shadow-lg`
                : `${THEME.colors.backgrounds.card} ${THEME.colors.text.secondary} border-2 ${THEME.colors.borders.light} hover:border-cyan-400`
            }`}
          >
            <span className="mr-2">{tab.icon}</span>
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      <div
        className={`${THEME.colors.backgrounds.card} rounded-2xl p-8 border-2 ${THEME.colors.borders.light}`}
      >
        {activeTab === 'create' && <CreateSection onSectionCreated={handleSectionCreated} />}
        {activeTab === 'manage' && <ManageSections refreshTrigger={refreshTrigger} />}
        {activeTab === 'schedule' && <ScheduleMeetings refreshTrigger={refreshTrigger} />}
      </div>
    </div>
  );
}
