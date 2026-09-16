import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to POST /api/v1/bookings (3.3.S1/S2). No Redux/state logic here. */
export function createBooking(bookingData) {
  return api(API_ROUTES.BOOKINGS, {
    method: "POST",
    body: JSON.stringify(bookingData),
  });
}

/** Service Tier — talks to GET /api/v1/bookings/active (3.3.S3/S5, M2). */
export function fetchActiveBookings() {
  return api(`${API_ROUTES.BOOKINGS}/active`);
}

/** Service Tier — talks to GET /api/v1/bookings/past (3.3.S3, M2). */
export function fetchPastBookings() {
  return api(`${API_ROUTES.BOOKINGS}/past`);
}

/** Service Tier — talks to PATCH /api/v1/bookings/{bookingId}/confirm (3.3.S6). */
export function confirmBooking(bookingId, payload) {
  return api(`${API_ROUTES.BOOKINGS}/${bookingId}/confirm`, {
    method: "PATCH",
    body: JSON.stringify(payload),
  });
}

/** Service Tier — talks to PATCH /api/v1/bookings/{bookingId}/cancel (3.3.M1). */
export function cancelBooking(bookingId, payload) {
  return api(`${API_ROUTES.BOOKINGS}/${bookingId}/cancel`, {
    method: "PATCH",
    body: JSON.stringify(payload),
  });
}

/** Service Tier — talks to GET /api/v1/bookings/filter (3.3.M10).*/
export function fetchFilteredBookings({ serviceType, from, sort } = {}) {
  const params = new URLSearchParams();
  if (serviceType) params.set("serviceType", serviceType);
  if (from) params.set("from", from);
  if (sort) params.set("sort", sort);
  const query = params.toString();
  return api(`${API_ROUTES.BOOKINGS}/filter${query ? `?${query}` : ""}`);
}

/** Service Tier — talks to GET /api/v1/bookings/search (3.3.M9). */
export function searchBookings(query) {
  return api(`${API_ROUTES.BOOKINGS}/search?query=${encodeURIComponent(query)}`);
}

export function assignTechnicians(bookingId, technicianIds) {
  return api(`${API_ROUTES.BOOKINGS}/${bookingId}/assign`, {
    method: "PATCH",
    body: JSON.stringify(technicianIds)
  });
}

/** Service Tier — talks to GET /api/v1/bookings/customers/{customerId} (4.M1). Lets the
 *  Receptionist confirm they have the right customer before creating a booking on their behalf. */
export function lookupCustomer(customerId) {
  return api(`${API_ROUTES.BOOKINGS}/customers/${encodeURIComponent(customerId)}`);
}
