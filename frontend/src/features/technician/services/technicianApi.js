import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to GET /api/v1/technicians/by-staff/{staffId}. No Redux/state logic here. */
export function fetchTechnicianByStaffId(staffId) {
  return api(`${API_ROUTES.TECHNICIANS}/by-staff/${staffId}`);
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
