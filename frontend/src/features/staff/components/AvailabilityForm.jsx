const DAYS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

/**
 * MOCK UI — Step 2 of Staff creation, Technician-only (SRS 3.1.M8 style).
 * Split out of StaffCreatePage.jsx to keep that file short; all state still
 * lives in the parent, this component only renders and calls the setters
 * passed in as props.
 */
export default function AvailabilityForm({
  fullName,
  role,
  selectedDays,
  toggleDay,
  startHour,
  setStartHour,
  endHour,
  setEndHour,
  onBack,
  onCreateAccount,
}) {
  return (
    <div className="w-[460px] bg-white rounded-xl shadow-sm border-2 border-indigo-600 p-6">
      <div className="text-[10px] font-bold uppercase text-gray-400 mb-1">
        Set Availability Schedule
      </div>
      <p className="text-xs text-gray-500 mb-4">
        {fullName || "New Technician"} · {role} — {selectedDays.length} consecutive
        working days, daily window between 08:00–20:00
      </p>

      <div className="flex gap-2 mb-6">
        {DAYS.map((day) => {
          const isWeekend = day === "Sat" || day === "Sun";
          const isSelected = selectedDays.includes(day);
          return (
            <button
              key={day}
              disabled={isWeekend}
              onClick={() => toggleDay(day)}
              className={`flex-1 text-xs font-bold py-2 rounded-lg ${
                isWeekend
                  ? "bg-gray-100 text-gray-300 cursor-not-allowed"
                  : isSelected
                  ? "bg-indigo-600 text-white"
                  : "bg-gray-100 text-gray-500"
              }`}
            >
              {day}
            </button>
          );
        })}
      </div>

      <div className="text-[10px] font-bold uppercase text-gray-400 mb-3">
        Daily Working Hours
      </div>
      <div className="flex items-center gap-3 mb-2">
        <span className="text-xs font-bold text-gray-600">
          {String(startHour).padStart(2, "0")}:00
        </span>
        <input
          type="range"
          min={0}
          max={endHour - 1}
          value={startHour}
          onChange={(e) => setStartHour(Number(e.target.value))}
          className="flex-1 accent-indigo-600"
        />
        <input
          type="range"
          min={startHour + 1}
          max={24}
          value={endHour}
          onChange={(e) => setEndHour(Number(e.target.value))}
          className="flex-1 accent-indigo-600"
        />
        <span className="text-xs font-bold text-gray-600">
          {String(endHour).padStart(2, "0")}:00
        </span>
      </div>
      <p className="text-xs text-gray-400 mb-6">
        Selected: {String(startHour).padStart(2, "0")}:00 – {String(endHour).padStart(2, "0")}:00,{" "}
        {selectedDays[0]}–{selectedDays[selectedDays.length - 1]} ({selectedDays.length}{" "}
        consecutive days)
      </p>

      <div className="flex justify-end gap-2.5">
        <button
          onClick={onBack}
          className="border border-gray-200 text-gray-600 text-sm font-bold px-4 py-2.5 rounded-lg hover:bg-gray-50"
        >
          ← Back
        </button>
        <button
          onClick={onCreateAccount}
          className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2.5 rounded-lg"
        >
          Create Account
        </button>
      </div>
    </div>
  );
}
