import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to GET /api/v1/statistics/revenue (ADMIN-only, 3.4.S3). */
export function fetchRevenueStats() {
  return api(`${API_ROUTES.STATISTICS}/revenue`);
}
