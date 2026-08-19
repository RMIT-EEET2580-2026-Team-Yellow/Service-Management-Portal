import { useState } from "react";

/**
 * MOCK UI ONLY — no backend wired up yet (3.3.M1).
 * Real version calls POST /api/bookings/{id}/cancel (creates a new
 * BookingCancellation record, same pattern as /assign — see SMP API for UI
 * Mock.pdf). Status transition rule: Created/Confirmed/Scheduled → Cancelled
 * by Customer; Working → Terminated by Customer.
 */
export default function CancelBookingModal({ booking, onBack, onConfirm }) {
  const [reason, setReason] = useState("");

  if (!booking) return null;

  const targetStatus =
    booking.status === "Working" ? "Terminated by Customer" : "Cancelled by Customer";

  return (
    <div className="absolute inset-0 bg-[#141632]/40 flex items-center justify-center z-10">
      <div className="w-[480px] bg-white rounded-2xl shadow-2xl overflow-hidden">
        <div className="bg-[#232752] text-white px-6 py-4.5">
          <div className="font-extrabold text-[15px]">Cancel Booking — {booking.id}</div>
          <div className="text-[11.5px] text-indigo-200 mt-0.5">
            {booking.service} · {booking.status}
          </div>
        </div>

        <div className="px-6 py-5">
          <div className="bg-amber-50 text-amber-700 text-[11.5px] rounded-lg px-3 py-2.5 mb-4">
            ⚠ Status will change to <b>"{targetStatus}"</b> (current status: {booking.status})
          </div>

          <div className="mb-4">
            <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
              Cancellation Reason
            </label>
            <textarea
              rows={3}
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Describe the reason for cancellation…"
              className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div>
            <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
              Evidence (photo)
            </label>
            <div className="border-2 border-dashed border-indigo-200 bg-indigo-50 text-indigo-600 text-xs font-semibold rounded-lg px-4 py-5 text-center cursor-pointer">
              📷 Upload evidence image
            </div>
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
            disabled={!reason.trim()}
            onClick={() => onConfirm(reason, targetStatus)}
            className="text-sm font-bold text-white bg-red-500 hover:bg-red-600 disabled:opacity-40 disabled:cursor-not-allowed rounded-lg px-4 py-2.5"
          >
            Confirm Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
