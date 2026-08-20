import StatusBadge from "./StatusBadge";

/**
 * Right column of BookingListPage — booking detail card + action buttons.
 * All state still lives in the parent; this component only renders and
 * calls the handlers passed in as props.
 */
export default function BookingDetailPanel({
  selected,
  tab,
  onConfirmBooking,
  onAssignTechnician,
  onCancelBooking,
}) {
  return (
    <div className="w-[400px] shrink-0 bg-white rounded-xl shadow-sm border-2 border-indigo-600 flex flex-col overflow-hidden">
      <div className="px-5 py-4 border-b border-gray-100 flex items-center justify-between">
        <span className="font-extrabold text-sm">Booking {selected.id}</span>
        <StatusBadge status={selected.status} />
      </div>

      <div className="px-5 py-4 border-b border-gray-100">
        <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">Booking Info</div>
        <div className="grid grid-cols-2 gap-3 text-xs">
          <div>
            <span className="text-gray-400 block text-[10px] mb-0.5">Service</span>
            <b>{selected.service}</b>
          </div>
          <div>
            <span className="text-gray-400 block text-[10px] mb-0.5">Preferred Date</span>
            <b>{selected.preferredDate}</b>
          </div>
          <div>
            <span className="text-gray-400 block text-[10px] mb-0.5">Creation Date</span>
            <b>{selected.creationDate}</b>
          </div>
          {selected.address && (
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Address</span>
              <b>{selected.address}</b>
            </div>
          )}
        </div>
        {selected.issueDescription && (
          <div className="mt-3 text-xs leading-relaxed">
            <span className="text-gray-400 block text-[10px] mb-0.5">Issue Description</span>
            {selected.issueDescription}
          </div>
        )}
      </div>

      {/* Completed cost summary */}
      {selected.status === "Completed" && (
        <div className="px-5 py-4 border-b border-gray-100 bg-gray-50">
          <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">Cost Summary</div>
          <div className="grid grid-cols-3 gap-2.5 text-center">
            <div className="bg-white rounded-lg p-3">
              <span className="block text-[10px] text-gray-400 mb-1">Service Fee</span>
              <b className="text-sm">{selected.serviceFeeAmt}</b>
            </div>
            <div className="bg-white rounded-lg p-3">
              <span className="block text-[10px] text-gray-400 mb-1">Material Cost</span>
              <b className="text-sm">{selected.materialCost}</b>
            </div>
            <div className="bg-indigo-900 rounded-lg p-3">
              <span className="block text-[10px] text-indigo-200 mb-1">Total Cost</span>
              <b className="text-sm text-white">{selected.totalCost}</b>
            </div>
          </div>
        </div>
      )}

      {/* Cancelled / Aborted reason */}
      {(selected.status === "Cancelled by Customer" ||
        selected.status === "Terminated by Customer" ||
        selected.status === "Aborted by Technician") && (
        <div className="px-5 py-4 border-b border-gray-100 bg-gray-50">
          <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">Reason</div>
          <p className="text-xs leading-relaxed">{selected.reason}</p>
        </div>
      )}

      {/* Confirmation + Assigned technicians */}
      {selected.serviceFee && (
        <div className="px-5 py-4 border-b border-gray-100 bg-gray-50">
          <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">Confirmation</div>
          <div className="grid grid-cols-2 gap-3 text-xs">
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Service Fee</span>
              <b>{selected.serviceFee}</b>
            </div>
            <div>
              <span className="text-gray-400 block text-[10px] mb-0.5">Est. Duration</span>
              <b>{selected.estDuration}</b>
            </div>
          </div>
        </div>
      )}

      {selected.technicians && (
        <div className="px-5 py-4 border-b border-gray-100">
          <div className="text-[10px] font-bold uppercase text-gray-400 mb-2.5">
            Schedule &amp; Technician
          </div>
          {selected.scheduledTime && (
            <div className="mb-2.5 text-xs">
              <span className="text-gray-400 block text-[10px] mb-0.5">Scheduled Time</span>
              <b>{selected.scheduledTime}</b>
            </div>
          )}
          {selected.technicians.map((t) => (
            <div
              key={t.name}
              className="flex justify-between items-center bg-gray-50 rounded-lg px-3 py-2.5 mb-1.5 text-xs"
            >
              <span>👤 {t.name}</span>
              <StatusBadge status={t.response} />
            </div>
          ))}
        </div>
      )}

      <div className="mt-auto px-5 py-4 flex justify-end gap-2.5">
        {tab === "active" && selected.status === "Created" && (
          <button
            onClick={onConfirmBooking}
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2 rounded-lg"
          >
            Confirm Booking
          </button>
        )}
        {tab === "active" && selected.status === "Confirmed" && (
          <button
            onClick={onAssignTechnician}
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-bold px-4 py-2 rounded-lg"
          >
            Assign Technician
          </button>
        )}
        {tab === "active" &&
          ["Created", "Confirmed", "Scheduled", "Working"].includes(selected.status) && (
            <button
              onClick={onCancelBooking}
              className="border border-red-500 text-red-500 text-sm font-bold px-4 py-2 rounded-lg hover:bg-red-50"
            >
              Cancel Booking
            </button>
          )}
      </div>
    </div>
  );
}
