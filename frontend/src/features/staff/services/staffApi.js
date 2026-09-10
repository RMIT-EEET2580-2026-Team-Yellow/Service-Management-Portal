import sendHttpRequest from "../../../http_call/HttpRequest";
import { STAFF_SERVICE_URL } from "../../../service_url/StaffUrlConfig";
import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to GET /api/v1/staff/{staffId} (ADMIN-only, dashboard header). */
export function fetchStaffById(staffId) {
  return api(`${API_ROUTES.STAFF}/${staffId}`);
}

export default async function fetchStaffList() {
  const response = await sendHttpRequest(STAFF_SERVICE_URL, "GET");

  if (response.status === 200) {
    return response.json.data;
  } else {
    console.error("Error fetching staff:", response);
    return [];
  }
}
