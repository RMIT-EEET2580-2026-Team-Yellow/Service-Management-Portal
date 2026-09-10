import { useEffect, useState } from "react";
import { fetchAvailableTechnicians } from "../../technician/services/technicianApi";

/** Wired to real backend: GET /api/v1/technicians/available + PATCH /api/v1/bookings/{id}/assign (3.3.M3). */
export default function AssignTechnicianModal({ booking, assigning, assignError, onCancel, onAssign }) {
  const [selected, setSelected] = useState([]);
  const [available, setAvailable] = useState([]);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState(null);

  useEffect(() => {
    if (!booking) return;
    setSelected([]);
    setLoading(true);
    setLoadError(null);
    fetchAvailableTechnicians({
      date: booking.preferredDateISO,
      start: booking.availableStartRaw,
      end: booking.availableEndRaw,
      durationMinutes: booking.estDuration,
    })
      .then((res) => setAvailable(res.data))
      .catch((err) => setLoadError(err.message))
      .finally(() => setLoading(false));
  }, [booking]);

  if (!booking) return null;

  function toggle(id) {
    setSelected((prev) => (prev.includes(id) ? prev.filter((x) => x !== id) : [...prev, id]));
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
              <b>{booking.estDuration} min</b>
            </div>
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Preferred Date</span>
              <b>{booking.preferredDate}</b>
            </div>
          </div>

          {(assignError || loadError) && (
            <div className="text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2 mb-3">
              {assignError || loadError}
            </div>
          )}

          {loading && (
            <div className="text-xs text-gray-400 italic py-2">Loading available technicians…</div>
          )}

          {!loading && available.length === 0 && !loadError && (
            <div className="text-xs text-gray-400 italic py-2">
              No technician is available for this window.
            </div>
          )}

          {available.length > 0 && (
            <div className="text-[10px] font-bold uppercase text-green-600 mb-2">✅ Available</div>
          )}
          {available.map((t) => (
            <label
              key={t.technicianId}
              className="flex justify-between items-center bg-green-50 rounded-lg px-3.5 py-2.5 mb-1.5 text-xs cursor-pointer"
            >
              <span className="flex items-center gap-2">
                <input
                  type="checkbox"
                  checked={selected.includes(t.technicianId)}
                  onChange={() => toggle(t.technicianId)}
                  className="accent-indigo-600"
                />
                {t.fullName}
              </span>
            </label>
          ))}
        </div>

        <div className="px-6 py-4 flex justify-end gap-2.5">
          <button
            onClick={onCancel}
            className="text-sm font-semibold text-gray-700 bg-white border border-gray-200 rounded-lg px-4 py-2.5"
          >
            Cancel
          </button>
          <button
            disabled={selected.length === 0 || assigning}
            onClick={() => onAssign(selected)}
            className="text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed rounded-lg px-4 py-2.5"
          >
            {assigning ? "Assigning…" : "Assign Selected →"}
          </button>
        </div>
      </div>
    </div>
  );
}