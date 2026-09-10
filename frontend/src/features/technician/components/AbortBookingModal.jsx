import { useState } from "react";

/** Wired to PATCH /api/v1/technicians/{technicianId}/bookings/{bookingId}/abort (3.3.M8).
 *  WORKING -> "ABORTED BY TECHNICIAN". */
export default function AbortBookingModal({ booking, onBack, onConfirm, submitting, submitError }) {
  const [reason, setReason] = useState("");

  if (!booking) return null;

  return (
    <div className="fixed inset-0 bg-[#141632]/40 flex items-center justify-center z-20">
      <div className="w-[480px] bg-white rounded-2xl shadow-2xl overflow-hidden">
        <div className="bg-[#232752] text-white px-6 py-4.5">
          <div className="font-extrabold text-[15px]">Abort Booking — {booking.bookingId}</div>
          <div className="text-[11.5px] text-indigo-200 mt-0.5">
            {booking.service} · {booking.bookingStatus}
          </div>
        </div>

        <div className="px-6 py-5">
          {submitError && (
            <p
              className="text-xs text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2.5 mb-4"
              role="alert"
            >
              {submitError}
            </p>
          )}

          <div className="bg-amber-50 text-amber-700 text-[11.5px] rounded-lg px-3 py-2.5 mb-4">
            ⚠ Status will change to <b>"ABORTED BY TECHNICIAN"</b>. Use this only when the job
            genuinely cannot be finished.
          </div>

          <div>
            <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
              Reason
            </label>
            <textarea
              rows={3}
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Describe why this job is being aborted…"
              className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        <div className="px-6 py-4 flex justify-end gap-2.5">
          <button
            onClick={onBack}
            className="text-sm font-semibold text-gray-700 bg-white border border-gray-200 rounded-lg px-4 py-2.5"
          >
            Back
          </button>
          <button
            disabled={!reason.trim() || submitting}
            onClick={() => onConfirm(reason)}
            className="text-sm font-bold text-white bg-red-500 hover:bg-red-600 disabled:opacity-40 disabled:cursor-not-allowed rounded-lg px-4 py-2.5"
          >
            {submitting ? "Aborting…" : "Confirm Abort"}
          </button>
        </div>
      </div>
    </div>
  );
}
