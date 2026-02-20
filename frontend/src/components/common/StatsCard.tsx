import { THEME } from '../../constants/theme';

interface StatsCardProps {
  icon: React.ReactNode;
  title: string;
  value: string | number;
  subtitle: string;
  valueColor?: string;
  iconGradient?: string;
}

export function StatsCard({
  icon,
  title,
  value,
  subtitle,
  valueColor = THEME.colors.text.accent,
  iconGradient = 'from-blue-100 to-cyan-100',
}: StatsCardProps) {
  const iconColorMap: Record<string, string> = {
    'from-blue-100 to-cyan-100': 'text-cyan-600',
    'from-emerald-100 to-teal-100': 'text-teal-600',
    'from-orange-100 to-amber-100': 'text-orange-600',
  };

  const iconColor = iconColorMap[iconGradient] || 'text-cyan-600';

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 border-2 ${THEME.colors.borders.light}`}>
      <div className={`inline-flex items-center justify-center w-12 h-12 bg-gradient-to-br ${iconGradient} rounded-xl mb-4`}>
        <div className={`w-6 h-6 ${iconColor}`}>
          {icon}
        </div>
      </div>
      <h3 className={`text-lg font-semibold ${THEME.colors.text.primary} mb-2`}>{title}</h3>
      <p className={`text-3xl font-bold ${valueColor}`}>{value}</p>
      <p className={`${THEME.colors.text.muted} text-sm mt-1`}>{subtitle}</p>
    </div>
  );
}
