import type { ScheduleSlot } from '../pages/Schedule';
import { THEME } from '../constants/theme';

interface ScheduleGridProps {
  scheduleSlots: ScheduleSlot[];
  colors: string[];
  onSlotClick?: (slot: ScheduleSlot) => void;
}

const TIME_SLOTS = [
  '08:00', '09:00', '10:00', '11:00', '12:00',
  '13:00', '14:00', '15:00', '16:00', '17:00'
];

const DAYS = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];

export function ScheduleGrid({ scheduleSlots, colors, onSlotClick }: ScheduleGridProps) {
  const getSlotsAtTime = (dayOfWeek: number, timeStart: string): ScheduleSlot[] => {
    return scheduleSlots.filter((slot) => {
      const startMinutes = timeToMinutes(slot.startTime);
      const endMinutes = timeToMinutes(slot.endTime);
      const startHourMinutes = timeToMinutes(timeStart);
      const endHourMinutes = startHourMinutes + 60; // 1 hour block
      // Slot overlaps with this hour block
      return slot.dayOfWeek === dayOfWeek && startMinutes < endHourMinutes && endMinutes > startHourMinutes;
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
    return Math.ceil(diffMinutes / 60)+1; // Each 1-hour slot = 1 row
  };

  // Calculate vertical offset within the hour
  const calculateTopOffset = (startTime: string, hourStart: string): number => {
    const startMinutes = timeToMinutes(startTime);
    const hourStartMinutes = timeToMinutes(hourStart);
    const offsetMinutes = startMinutes - hourStartMinutes;
    return (offsetMinutes / 60) * 48; // 48px per hour
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
                const allSlots = getSlotsAtTime(dayOfWeek, time);
                // Only render slots that START in this hour
                const slots = allSlots.filter((slot) => {
                  const slotStartMinutes = timeToMinutes(slot.startTime);
                  const hourMinutes = timeToMinutes(time);
                  return slotStartMinutes >= hourMinutes && slotStartMinutes < hourMinutes + 60;
                });

                return (
                  <td
                    key={`${dayOfWeek}-${time}`}
                    className={`p-2 border border-slate-200 ${slots.length === 0 ? 'bg-white' : ''} h-12 align-top relative`}
                  >
                    {slots.map((slot, idx) => {
                      const totalSlots = slots.length;
                      const slotIdx = idx;
                      const slotWidth = 100 / totalSlots;
                      const topOffset = calculateTopOffset(slot.startTime, time);

                      return (
                        <div
                          key={`${slot.courseCode}-${idx}`}
                          className={`p-2 rounded text-xs cursor-pointer group hover:shadow-md transition-all absolute top-0 border-2 ${
                            slot.hasConflict
                              ? 'bg-red-500 border-red-600 text-red-1500'
                              : `${getSlotColor(scheduleSlots.indexOf(slot))} ${
                                  'border-solid'
                                }`
                          }`}
                          style={{
                            height: `${calculateRowSpan(slot.startTime, slot.endTime) * 48}px`,
                            width: `${slotWidth}%`,
                            left: `${slotWidth * slotIdx}%`,
                            top: `${topOffset}px`,
                            opacity: slot.hasConflict ? 0.5 : (slot.isOverlay ? 0.75 : 1),
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
