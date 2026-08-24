import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to POST /api/v1/bookings (3.3.S1/S2). No Redux/state logic here. */
export function createBooking(bookingData) {
  return api(API_ROUTES.BOOKINGS, {
    method: "POST",
    body: JSON.stringify(bookingData),
  });
}
