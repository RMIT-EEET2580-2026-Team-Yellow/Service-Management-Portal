import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useBookings } from "../../../common/hooks/useBookings";

const SERVICE_TYPES = ["Plumbing", "Electrical", "Electronics"];

const EMPTY_FORM = {
  customerId: "",
  serviceType: SERVICE_TYPES[0],
  serviceAddress: "",
  preferredDate: "",
  availableStart: "",
  availableEnd: "",
  issueDescription: "",
};

const TODAY = new Date().toISOString().slice(0, 10);

/** Receptionist creates a Service Booking for exactly one Repair Service (3.3.S1/S2). */
export default function BookingCreatePage() {
  const navigate = useNavigate();
  const { create, creating, createError } = useBookings();
  const [form, setForm] = useState(EMPTY_FORM);
  const [formError, setFormError] = useState(null);

  function update(field) {
    return (e) => setForm((prev) => ({ ...prev, [field]: e.target.value }));
  }

  function validate() {
    if (!form.customerId.trim()) return "Customer ID is required.";
    if (!form.serviceAddress.trim()) return 'Service address is required, e.g. "22 Le Loi, District 1, HCMC".';
    if (!form.preferredDate) return "Preferred service date is required.";
    if (form.preferredDate < TODAY) return "Preferred service date cannot be in the past.";
    if (!form.availableStart || !form.availableEnd) return "Available start and end time are both required.";
    if (form.availableStart >= form.availableEnd) return "Available start time must be earlier than the end time.";
    if (!form.issueDescription.trim()) return 'Issue description is required, e.g. "Sink pipe is leaking".';
    return null;
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const validationError = validate();
    if (validationError) {
      setFormError(validationError);
      return;
    }
    setFormError(null);

    const result = await create(form);
    if (result) {
      navigate("/bookings");
    }
  }

  const displayedError = formError || createError;

  return (
    <div className="bg-gray-50 min-h-screen">
      <div className="bg-white border-b border-gray-200 px-8 py-4">
        <h1 className="text-lg font-extrabold text-gray-900">Service Bookings</h1>
        <p className="text-xs text-gray-400 mt-0.5">Bookings / Create New</p>
      </div>

      <form onSubmit={handleSubmit} className="flex justify-center p-8">
        <div className="w-[600px] bg-white rounded-xl shadow-sm overflow-hidden h-fit">
          <div className="px-6 py-4 border-b border-gray-100">
            <div className="text-[10px] font-bold uppercase text-gray-400 mb-1">
              New Service Booking
            </div>
          </div>

          <div className="px-6 py-4 space-y-4">
            {displayedError && (
              <p className="text-sm text-red-600 bg-red-50 border border-red-100 rounded-lg px-3 py-2.5" role="alert">
                {displayedError}
              </p>
            )}

            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Customer ID
              </label>
              <input
                type="text"
                placeholder="Customer's account ID (owned by SBP)"
                value={form.customerId}
                onChange={update("customerId")}
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Service Type
              </label>
              <select
                value={form.serviceType}
                onChange={update("serviceType")}
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              >
                {SERVICE_TYPES.map((type) => (
                  <option key={type} value={type}>
                    {type}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Service Address
              </label>
              <input
                type="text"
                placeholder="22 Le Loi, District 1, HCMC"
                value={form.serviceAddress}
                onChange={update("serviceAddress")}
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>

            <div>
              <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                Preferred Service Date
              </label>
              <input
                type="date"
                min={TODAY}
                value={form.preferredDate}
                onChange={update("preferredDate")}
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
                  value={form.availableStart}
                  onChange={update("availableStart")}
                  className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>
              <div className="flex-1">
                <label className="block text-[10px] font-bold uppercase text-gray-400 mb-1.5">
                  Available End Time
                </label>
                <input
                  type="time"
                  value={form.availableEnd}
                  onChange={update("availableEnd")}
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
                value={form.issueDescription}
                onChange={update("issueDescription")}
                className="w-full text-sm border border-gray-200 rounded-lg px-3 py-2.5 bg-gray-50 resize-none focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div className="px-6 py-4 border-t border-gray-100 flex justify-end gap-2.5">
            <button
              type="button"
              onClick={() => navigate("/bookings")}
              className="text-sm font-semibold text-gray-700 bg-white border border-gray-200 rounded-lg px-4 py-2.5"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={creating}
              className="text-sm font-bold text-white bg-indigo-600 hover:bg-indigo-700 rounded-lg px-4 py-2.5 disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {creating ? "Creating…" : "Create Booking"}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
