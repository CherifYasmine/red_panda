import type { ScheduleSlot } from '../pages/Schedule';
import { THEME } from '../constants/theme';

interface ScheduleGridProps {
  scheduleSlots: ScheduleSlot[];
  colors: string[];
  onSlotClick?: (slot: ScheduleSlot) => void;
}

const TIME_SLOTS = [
  '08:00', '08:30', '09:00', '09:30', '10:00', '10:30',
  '11:00', '11:30', '12:00', '12:30', '13:00', '13:30',
  '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00'
];

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];

export function ScheduleGrid({ scheduleSlots, colors, onSlotClick }: ScheduleGridProps) {
  const getSlotsAtTime = (dayOfWeek: number, time: string): ScheduleSlot[] => {
    return scheduleSlots.filter((slot) => {
      const startMinutes = timeToMinutes(slot.startTime);
      const endMinutes = timeToMinutes(slot.endTime);
      const currentMinutes = timeToMinutes(time);
      return slot.dayOfWeek === dayOfWeek && currentMinutes >= startMinutes && currentMinutes < endMinutes;
    });
  };

  const timeToMinutes = (time: string): number => {
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 60 + minutes;
  };

  const calculateRowSpan = (startTime: string, endTime: string): number => {
    const startMinutes = timeToMinutes(startTime);
    const endMinutes = timeToMinutes(endTime);
    const diffMinutes = endMinutes - startMinutes;
    return diffMinutes / 30 + 1; // Each 30-minute slot = 1 row
  };

  const getSlotColor = (index: number): string => {
    // Don't use color index for conflicting or overlay slots
    return colors[index % colors.length];
  };

  return (
    <div className={`${THEME.colors.backgrounds.card} rounded-2xl p-6 mb-8 border-2 ${THEME.colors.borders.light} overflow-x-auto`}>
      <table className="w-full border-collapse">
        <thead>
          <tr>
            <th className={`p-3 text-sm font-semibold ${THEME.colors.text.primary} border border-slate-200 bg-slate-50`}>
              Time
            </th>
            {DAYS.map((day) => (
              <th
                key={day}
                className={`p-3 text-sm font-semibold ${THEME.colors.text.primary} border border-slate-200 bg-slate-50 text-center`}
              >
                {day}
              </th>
            ))}
          </tr>
        </thead>

        <tbody>
          {TIME_SLOTS.map((time) => (
            <tr key={time}>
              <td className={`p-3 text-xs font-semibold ${THEME.colors.text.muted} border border-slate-200 bg-slate-50 whitespace-nowrap`}>
                {time}
              </td>
              {DAYS.map((_, dayIdx) => {
                const dayOfWeek = dayIdx + 1;
                const slots = getSlotsAtTime(dayOfWeek, time);
                const slotKey = `${dayOfWeek}-${time}`;
                const slotsToRender = slots.filter((slot) => slot.startTime === time);

                return (
                  <td
                    key={slotKey}
                    className={`p-2 border border-slate-200 ${slots.length === 0 ? 'bg-white' : ''} h-10 align-top relative`}
                  >
                    {slotsToRender.map((slot, idx) => {
                      const totalSlots = slotsToRender.length;
                      const slotWidth = 100 / totalSlots;
                      return (
                        <div
                          key={`${slot.courseCode}-${idx}`}
                          className={`p-2 rounded text-xs cursor-pointer group hover:shadow-md transition-all absolute top-0 ${
                            slot.hasConflict
                              ? 'bg-red-500/50 border-red-500 text-red-900 border-2'
                              : `${getSlotColor(scheduleSlots.indexOf(slot))} ${
                                  slot.isOverlay ? 'border-dashed opacity-75' : 'border-2'
                                }`
                          }`}
                          style={{
                            height: `${calculateRowSpan(slot.startTime, slot.endTime) * 40}px`,
                            width: `${slotWidth}%`,
                            left: `${slotWidth * idx}%`,
                            zIndex: slot.hasConflict ? 15 : (slot.isOverlay ? 5 : 10),
                          }}
                          onClick={() => onSlotClick?.(slot)}
                          title={`${slot.courseName} - ${slot.teacherName}${slot.hasConflict ? ' (CONFLICT)' : ''}`}
                        >
                          <p className="font-bold line-clamp-1">{slot.courseCode}</p>
                          <p className="line-clamp-1">{slot.classroom}</p>
                          <p className="text-xs line-clamp-1 opacity-75">
                            {slot.startTime}-{slot.endTime}
                          </p>
                        </div>
                      );
                    })}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
