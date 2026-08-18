import { useState } from "react";

/**
 * MOCK UI ONLY — no backend wired up yet (3.3.S7, M3).
 * The available/not-available split below is hardcoded; wire it up to
 * POST /api/bookings/{id}/assign (which internally checks Technician
 * availability, see SMP API for UI Mock.pdf) once the backend is ready.
 */
export default function AssignTechnicianModal({ booking, onCancel, onAssign }) {
  const [selected, setSelected] = useState([]);

  if (!booking) return null;

  const available = booking.availableTechnicians || [];
  const unavailable = booking.unavailableTechnicians || [];

  function toggle(name) {
    setSelected((prev) =>
      prev.includes(name) ? prev.filter((n) => n !== name) : [...prev, name]
    );
  }

  return (
    <div className="absolute inset-0 bg-[#141632]/40 flex items-center justify-center z-10">
      <div className="w-[480px] bg-white rounded-2xl shadow-2xl overflow-hidden">
        <div className="bg-[#232752] text-white px-6 py-4.5">
          <div className="font-extrabold text-[15px]">Assign Technician — {booking.id}</div>
          <div className="text-[11.5px] text-indigo-200 mt-0.5">{booking.service}</div>
        </div>

        <div className="px-6 py-5">
          <div className="grid grid-cols-2 gap-3 text-xs mb-4">
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Estimated Duration</span>
              <b>{booking.estDuration}</b>
            </div>
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Preferred Date</span>
              <b>{booking.preferredDate}</b>
            </div>
          </div>

          {available.length === 0 && unavailable.length === 0 && (
            <div className="text-xs text-gray-400 italic py-2">
              No technician availability data mocked for this booking yet.
            </div>
          )}

          {available.length > 0 && (
            <div className="text-[10px] font-bold uppercase text-green-600 mb-2">
              ✅ Available
            </div>
          )}
          {available.map((t) => (
            <label
              key={t.name}
              className="flex justify-between items-center bg-green-50 rounded-lg px-3.5 py-2.5 mb-1.5 text-xs cursor-pointer"
            >
              <span className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={selected.includes(t.name)}
                  onChange={() => toggle(t.name)}
                  className="accent-indigo-600"
                />
                {t.name}
              </span>
              <span className="text-green-600 font-bold">{t.freeWindow}</span>
            </label>
          ))}

          {unavailable.length > 0 && (
            <>
              <div className="text-[10px] font-bold uppercase text-red-500 mt-3.5 mb-2">
                🚫 Not Available
              </div>
              {unavailable.map((t) => (
                <div
                  key={t.name}
                  className="flex justify-between items-center bg-gray-100 rounded-lg px-3.5 py-2.5 mb-1.5 text-xs text-gray-400"
                >
                  <span>{t.name}</span>
                  <span>Busy: {t.busyWith}</span>
                </div>
              ))}
            </>
          )}
        </div>

        <div className="px-6 py-4 flex justify-end gap-2.5">
          <button
            onClick={onCancel}
            className="text-sm font-semibold text-gray-700 bg-white border border-gray-200 rounded-lg px-4 py-2.5"
          >
            Cancel
          </button>
          <button
            disabled={selected.length === 0}
            onClick={() => onAssign(selected)}
            className="text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed rounded-lg px-4 py-2.5"
          >
            Assign Selected →
          </button>
        </div>
      </div>
    </div>
  );
}
