import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to GET /api/v1/technicians/by-staff/{staffId}. No Redux/state logic here. */
export function fetchTechnicianByStaffId(staffId) {
  return api(`${API_ROUTES.TECHNICIANS}/by-staff/${staffId}`);
}

/** Service Tier — talks to GET /api/v1/technicians (ADMIN-only roster, dashboard use). */
export function fetchAllTechnicians() {
  return api(API_ROUTES.TECHNICIANS);
}

/** Service Tier — talks to POST /api/v1/technicians/{technicianId}/schedule-requests. */
export function submitScheduleChangeRequest(technicianId, { workingDays }) {
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/schedule-requests`, {
    method: "POST",
    body: JSON.stringify({ workingDays }),
  });
}

/** Service Tier — talks to GET /api/v1/technicians/{technicianId}/schedule-requests. */
export function fetchMyScheduleRequests(technicianId) {
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/schedule-requests`);
}

/** Service Tier — talks to GET /api/v1/technicians/schedule-requests (Admin queue). */
export function fetchPendingScheduleRequests() {
  return api(`${API_ROUTES.TECHNICIANS}/schedule-requests`);
}

/** Service Tier — talks to PATCH /api/v1/technicians/schedule-requests/{requestId}/approve. */
export function approveScheduleRequest(requestId) {
  return api(`${API_ROUTES.TECHNICIANS}/schedule-requests/${requestId}/approve`, {
    method: "PATCH",
  });
}

/** Service Tier — talks to PATCH /api/v1/technicians/schedule-requests/{requestId}/reject. */
export function rejectScheduleRequest(requestId, reason) {
  const query = reason ? `?reason=${encodeURIComponent(reason)}` : "";
  return api(`${API_ROUTES.TECHNICIANS}/schedule-requests/${requestId}/reject${query}`, {
    method: "PATCH",
  });
}

export function fetchAvailableTechnicians({ date, start, end, durationMinutes }) {
    const params = new URLSearchParams({ date, start, end, durationMinutes });
    return api(`${API_ROUTES.TECHNICIANS}/available?${params.toString()}`);
}

/** Service Tier — talks to GET /api/v1/technicians/{technicianId}/bookings. */
export function fetchMyBookings(technicianId) {
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/bookings`);
}

/** Service Tier — talks to PATCH /api/v1/technicians/{technicianId}/bookings/{bookingId}/accept. */
export function acceptAssignment(technicianId, bookingId) {
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/bookings/${bookingId}/accept`, {
    method: "PATCH",
  });
}

/** Service Tier — talks to PATCH /api/v1/technicians/{technicianId}/bookings/{bookingId}/reject. */
export function rejectAssignment(technicianId, bookingId) {
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/bookings/${bookingId}/reject`, {
    method: "PATCH",
  });
}

/** Service Tier — talks to PATCH /api/v1/technicians/{technicianId}/bookings/{bookingId}/start. */
export function startWork(technicianId, bookingId) {
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/bookings/${bookingId}/start`, {
    method: "PATCH",
  });
}

/** Service Tier — talks to PATCH .../bookings/{bookingId}/complete?serviceFee=&materialCost=&signedBillImageUrl=. */
export function completeBooking(technicianId, bookingId, { serviceFee, materialCost, signedBillImageUrl }) {
  const params = new URLSearchParams({ serviceFee, materialCost, signedBillImageUrl });
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/bookings/${bookingId}/complete?${params.toString()}`, {
    method: "PATCH",
  });
}

/** Service Tier — talks to PATCH .../bookings/{bookingId}/abort?reason=. */
export function abortBooking(technicianId, bookingId, reason) {
  const params = new URLSearchParams({ reason });
  return api(`${API_ROUTES.TECHNICIANS}/${technicianId}/bookings/${bookingId}/abort?${params.toString()}`, {
    method: "PATCH",
  });
}
