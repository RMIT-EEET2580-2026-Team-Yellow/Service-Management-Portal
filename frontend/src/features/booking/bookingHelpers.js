// Pure helpers used by BookingListPage / BookingTable — no React here.
// Split out of BookingListPage.jsx to keep that file short.

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

export function matchesDateRange(booking, range) {
  if (!range) return true;
  const days = { last7: 7, last30: 30 }[range];
  if (!days) return true;
  const cutoff = new Date();
  cutoff.setDate(cutoff.getDate() - days);
  return new Date(booking.preferredDateISO) >= cutoff;
}
