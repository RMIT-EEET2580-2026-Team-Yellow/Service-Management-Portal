import { useState } from "react";

/**
 * Wired to PATCH /api/v1/technicians/{technicianId}/bookings/{bookingId}/complete (3.3.M7).
 * WORKING -> COMPLETED. There is no image-upload endpoint yet, so the signed bill is entered
 * as a URL for now (same caveat as CancelBookingModal's evidence upload).
 */
export default function CompleteBookingModal({ booking, onBack, onConfirm, submitting, submitError }) {
  const [serviceFee, setServiceFee] = useState("");
  const [materialCost, setMaterialCost] = useState("");
  const [signedBillImageUrl, setSignedBillImageUrl] = useState("");

  if (!booking) return null;

  const feeValid = serviceFee !== "" && Number(serviceFee) >= 0;
  const costValid = materialCost !== "" && Number(materialCost) >= 0;
  const urlValid = signedBillImageUrl.trim().length > 0;
  const canSubmit = feeValid && costValid && urlValid && !submitting;

  function handleConfirm() {
    onConfirm({
      serviceFee: Number(serviceFee),
      materialCost: Number(materialCost),
      signedBillImageUrl: signedBillImageUrl.trim(),
    });
  }

  return (
    <div className="fixed inset-0 bg-[#141632]/40 flex items-center justify-center z-20">
      <div className="w-[480px] bg-white rounded-2xl shadow-2xl overflow-hidden">
        <div className="bg-[#232752] text-white px-6 py-4.5">
          <div className="font-extrabold text-[15px]">Complete Booking — {booking.bookingId}</div>
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

          <div className="bg-indigo-50 text-indigo-700 text-[11.5px] rounded-lg px-3 py-2.5 mb-4">
            Status will change to <b>"COMPLETED"</b> once submitted.
          </div>

          <div className="grid grid-cols-2 gap-3 mb-4">
            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Service Fee
              </label>
              <input
                type="number"
                min="0"
                value={serviceFee}
                onChange={(e) => setServiceFee(e.target.value)}
                placeholder="0"
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Material Cost
              </label>
              <input
                type="number"
                min="0"
                value={materialCost}
                onChange={(e) => setMaterialCost(e.target.value)}
                placeholder="0"
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
              Signed Bill Image URL
            </label>
            <input
              type="text"
              value={signedBillImageUrl}
              onChange={(e) => setSignedBillImageUrl(e.target.value)}
              placeholder="https://…"
              className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
            <p className="text-[11px] text-gray-400 mt-1">
              No image upload endpoint yet — paste the URL of the signed bill photo.
            </p>
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
            disabled={!canSubmit}
            onClick={handleConfirm}
            className="text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-40 disabled:cursor-not-allowed rounded-lg px-4 py-2.5"
          >
            {submitting ? "Completing…" : "Confirm Complete"}
          </button>
        </div>
      </div>
    </div>
  );
}
