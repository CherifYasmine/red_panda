interface ConflictDetailsProps {
  conflicts: Array<{ course: string; teacher: string; day: string; time: string }>;
}

export function ConflictDetails({ conflicts }: ConflictDetailsProps) {
  if (conflicts.length === 0) {
    return (
      <div className="flex items-center gap-3 text-green-700">
        <svg className="w-5 h-5 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
          <path
            fillRule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
            clipRule="evenodd"
          />
        </svg>
        <span className="font-semibold text-sm">No schedule conflicts</span>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      <p className="text-sm font-semibold text-red-700 mb-3">Schedule Conflicts:</p>
      {conflicts.map((conflict, idx) => (
        <div key={idx} className="p-3 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-sm font-semibold text-red-900">{conflict.course}</p>
          <p className="text-xs text-red-700 mt-1">
            {conflict.day} {conflict.time}
          </p>
          <p className="text-xs text-red-700 mt-1">with {conflict.teacher}</p>
        </div>
      ))}
    </div>
  );
}
