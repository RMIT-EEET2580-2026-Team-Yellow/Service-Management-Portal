// Pure helpers used by BookingListPage / BookingTable — no React here.
// Split out of BookingListPage.jsx to keep that file short.

import { formatDate } from "../../common/utils/formatDate";

function formatMoney(value) {
  return value == null ? null : Number(value).toLocaleString("en-US");
}

function formatTime(value) {
  // LocalTime serializes as "HH:mm:ss" — trim to "HH:mm" for display.
  return value ? value.slice(0, 5) : null;
}

/**
 * Maps a BookingResponse from GET /api/v1/bookings/active|past (3.3.S3/S4) into the display
 * shape BookingTable, BookingDetailPanel, and the Confirm/Assign/Cancel modals already render.
 * estimatedServiceFee/estimatedDurationMinutes (set at confirm time, 3.3.S6) map to
 * serviceFee/estDuration; the final completion-time fee/cost (3.3.M7/S8) map to
 * serviceFeeAmt/materialCost/totalCost — kept separate since a booking can carry both at once.
 */
export function toDisplayBooking(booking) {
  return {
    id: booking.id,
    status: booking.status,
    service: booking.serviceType,
    address: booking.serviceAddress,
    issueDescription: booking.issueDescription,
    preferredDate: formatDate(booking.preferredDate),
    preferredDateISO: booking.preferredDate,
    creationDate: formatDate(booking.createdAt),
    creationDateISO: booking.createdAt,
    availableWindow:
      booking.availableStart && booking.availableEnd
        ? `${formatTime(booking.availableStart)} – ${formatTime(booking.availableEnd)}`
        : null,
    suggestedFee: booking.estimatedServiceFee,
    suggestedDuration: booking.estimatedDurationMinutes,
    serviceFee: booking.estimatedServiceFee,
    estDuration: booking.estimatedDurationMinutes,
    technicians: booking.assignedTechnicianNames?.length
      ? booking.assignedTechnicianNames.map((name) => ({ name, response: "Assigned" }))
      : undefined,
    serviceFeeAmt: formatMoney(booking.serviceFee),
    materialCost: formatMoney(booking.materialCost),
    totalCost: formatMoney(booking.totalCost),
  };
}

export const STATUS_STYLES = {
  Created: "bg-amber-100 text-amber-700",
  Confirmed: "bg-blue-100 text-blue-700",
  Scheduled: "bg-green-100 text-green-700",
  Working: "bg-red-100 text-red-700",
  Completed: "bg-indigo-100 text-indigo-700",
  "Cancelled by Customer": "bg-red-100 text-red-700",
  "Terminated by Customer": "bg-red-100 text-red-700",
  "Aborted by Technician": "bg-purple-100 text-purple-700",
};

export function matchesSearch(booking, query) {
  if (!query.trim()) return true;
  const q = query.trim().toLowerCase();
  if (booking.id.toLowerCase().includes(q)) return true;
  if (booking.service.toLowerCase().includes(q)) return true;
  if (booking.technicians?.some((t) => t.name.toLowerCase().includes(q))) return true;
  return false;
}

export const ACTIVE_STATUSES = ["Created", "Confirmed", "Scheduled", "Working"];

export function dateRangeToFrom(range) {
  const days = { last7: 7, last30: 30 }[range];
  if (!days) return undefined;
  const from = new Date();
  from.setDate(from.getDate() - days);
  return from.toISOString().slice(0, 10);
}

export function sortOrderToParam(sortOrder) {
  return sortOrder === "oldest" ? "asc" : "desc";
}

export function matchesDateRange(booking, range) {
  if (!range) return true;
  const days = { last7: 7, last30: 30 }[range];
  if (!days) return true;
  const cutoff = new Date();
  cutoff.setDate(cutoff.getDate() - days);
  return new Date(booking.preferredDateISO) >= cutoff;
}
