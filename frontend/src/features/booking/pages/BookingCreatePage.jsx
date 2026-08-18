import { useNavigate } from "react-router-dom";

/**
 * MOCK UI ONLY — no backend wired up yet (3.3.S1, S2).
 * "Create Booking" currently just navigates back to the list; wire this up to
 * POST /api/bookings via useBookings() once the Booking backend is ready.
 */
export default function BookingCreatePage() {
  const navigate = useNavigate();

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4">
        <h1 className="text-lg font-extrabold text-gray-900">Service Bookings</h1>
        <p className="text-xs text-gray-400 mt-0.5">Bookings / Create New</p>
      </div>

      <div className="flex justify-center p-8">
        <div className="w-[600px] bg-white rounded-xl shadow-sm overflow-hidden h-fit">
          <div className="px-6 py-4 border-b border-gray-100">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-1">
              New Service Booking
            </div>
          </div>

          <div className="px-6 py-4 space-y-4">
            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Service Address
              </label>
              <input
                type="text"
                placeholder="22 Le Loi, District 1, HCMC"
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Preferred Service Date
              </label>
              <input
                type="date"
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div className="flex gap-3">
              <div className="flex-1">
                <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                  Available Start Time
                </label>
                <input
                  type="time"
                  className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div className="flex-1">
                <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                  Available End Time
                </label>
                <input
                  type="time"
                  className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Issue Description
              </label>
              <textarea
                rows={3}
                placeholder="Describe the issue…"
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div className="px-6 py-4 border-t border-gray-100 flex justify-end gap-2.5">
            <button
              onClick={() => navigate("/bookings")}
              className="text-sm font-semibold text-gray-700 bg-white border border-gray-200 rounded-lg px-4 py-2.5"
            >
              Cancel
            </button>
            <button
              onClick={() => navigate("/bookings")}
              className="text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg px-4 py-2.5"
            >
              Create Booking
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
