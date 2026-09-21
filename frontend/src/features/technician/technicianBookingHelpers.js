// Pure helpers used by TechnicianBookingPage — no React here.

import { formatDate } from "../../common/utils/formatDate";

function formatTime(value) {
  // LocalTime serializes as "HH:mm:ss" — trim to "HH:mm" for display.
  return value ? value.slice(0, 5) : null;
}

/** Maps a TechnicianBookingResponse (GET /api/v1/technicians/{id}/bookings, 3.3.M3-M8) into the
 *  display shape the My Bookings page renders. */
export function toDisplayTechnicianBooking(booking) {
  return {
    assignmentId: booking.assignmentId,
    bookingId: booking.bookingId,
    assignmentStatus: booking.assignmentStatus,
    bookingStatus: booking.bookingStatus,
    service: booking.serviceType,
    issueDescription: booking.issueDescription,
    address: booking.serviceAddress,
    preferredDate: formatDate(booking.preferredDate),
    preferredDateISO: booking.preferredDate,
    availableWindow:
      booking.availableStart && booking.availableEnd
        ? `${formatTime(booking.availableStart)} – ${formatTime(booking.availableEnd)}`
        : null,
    totalCost: booking.totalCost != null ? Number(booking.totalCost).toLocaleString("en-US") : null,
  };
}

export const ASSIGNMENT_STATUS_STYLES = {
  PENDING: "bg-amber-100 text-amber-700",
  ACCEPTED: "bg-green-100 text-green-700",
  REJECTED: "bg-red-100 text-red-700",
};

export const BOOKING_STATUS_STYLES = {
  CREATED: "bg-amber-100 text-amber-700",
  CONFIRMED: "bg-blue-100 text-blue-700",
  SCHEDULED: "bg-green-100 text-green-700",
  WORKING: "bg-red-100 text-red-700",
  COMPLETED: "bg-indigo-100 text-indigo-700",
  "CANCELLED BY CUSTOMER": "bg-red-100 text-red-700",
  "TERMINATED BY CUSTOMER": "bg-red-100 text-red-700",
  "ABORTED BY TECHNICIAN": "bg-purple-100 text-purple-700",
};

const HISTORY_BOOKING_STATUSES = new Set([
  "COMPLETED",
  "CANCELLED BY CUSTOMER",
  "TERMINATED BY CUSTOMER",
  "ABORTED BY TECHNICIAN",
]);

/** Which of the 3 tabs (Assigned/Working/History) a booking belongs in, from this technician's
 *  point of view — driven by their own assignment status plus the booking's overall status. */
export function technicianBookingTab(booking) {
  if (booking.assignmentStatus === "REJECTED" || HISTORY_BOOKING_STATUSES.has(booking.bookingStatus)) {
    return "history";
  }
  if (booking.assignmentStatus === "PENDING") {
    return "assigned";
  }
  return "working";
}

export function matchesSearch(booking, query) {
  if (!query.trim()) return true;
  const needle = query.trim().toLowerCase();
  return (
    booking.bookingId?.toLowerCase().includes(needle) ||
    booking.service?.toLowerCase().includes(needle) ||
    booking.address?.toLowerCase().includes(needle)
  );
}
