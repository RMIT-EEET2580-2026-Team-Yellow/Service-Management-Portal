import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to GET /api/v1/statistics/revenue (3.4.S3, M3). */
export function fetchRevenueStats() {
  return api(`${API_ROUTES.STATISTICS}/revenue`);
}

/** Service Tier — talks to GET /api/v1/statistics/ratings/service-types (3.4.M3, U1). */
export function fetchServiceTypeRatings(period = "monthly") {
  return api(`${API_ROUTES.STATISTICS}/ratings/service-types?period=${period}`);
}

/** Service Tier — talks to GET /api/v1/statistics/ratings/technicians (3.4.U2). */
export function fetchTechnicianRatings(period = "monthly") {
  return api(`${API_ROUTES.STATISTICS}/ratings/technicians?period=${period}`);
}

export function fetchBookingReview(bookingId) {
  return api(`${API_ROUTES.STATISTICS}/bookings/${bookingId}/review`);
}