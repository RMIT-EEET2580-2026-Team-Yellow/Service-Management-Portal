/**
 * MOCK UI ONLY — no backend wired up yet (3.3.S6, Created → Confirmed).
 * Wire onConfirm up to PATCH /api/bookings/{id}/confirm once the Booking
 * backend is ready — see SMP API for UI Mock.pdf.
 */
export default function ConfirmBookingModal({ booking, onCancel, onConfirm }) {
  if (!booking) return null;

  return (
    <div className="absolute inset-0 bg-[#141632]/40 flex items-center justify-center z-10">
      <div className="w-[480px] bg-white rounded-2xl shadow-2xl overflow-hidden">
        <div className="bg-[#232752] text-white px-6 py-4.5">
          <div className="font-extrabold text-[15px]">Confirm Booking — {booking.id}</div>
          <div className="text-[11.5px] text-indigo-200 mt-0.5">{booking.service}</div>
        </div>

        <div className="px-6 py-5">
          <div className="grid grid-cols-2 gap-3 text-xs mb-4">
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Preferred Date</span>
              <b>{booking.preferredDate}</b>
            </div>
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Available Window</span>
              <b>{booking.availableWindow || "—"}</b>
            </div>
          </div>

          <div className="mb-4">
            <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
              Service Fee (VND)
            </label>
            <input
              type="number"
              defaultValue={booking.suggestedFee}
              className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div>
            <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
              Estimated Duration (hours)
            </label>
            <input
              type="number"
              defaultValue={booking.suggestedDuration}
              className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        <div className="px-6 py-4 flex justify-end gap-2.5">
          <button
            onClick={onCancel}
            className="text-sm font-semibold text-gray-700 bg-white border border-gray-200 rounded-lg px-4 py-2.5"
          >
            Cancel
          </button>
          <button
            onClick={onConfirm}
            className="text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg px-4 py-2.5"
          >
            Confirm Booking →
          </button>
        </div>
      </div>
    </div>
  );
}
