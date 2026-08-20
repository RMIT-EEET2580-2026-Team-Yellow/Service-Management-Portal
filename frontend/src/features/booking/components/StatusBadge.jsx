import { STATUS_STYLES } from "../bookingHelpers";

/** Small reusable badge — used by both BookingTable and BookingDetailPanel. */
export default function StatusBadge({ status }) {
  return (
    <span
      className={`text-xs font-semibold px-2.5 py-1 rounded-full whitespace-nowrap ${
        STATUS_STYLES[status] || "bg-gray-100 text-gray-700"
      }`}
    >
      {status}
    </span>
  );
}
