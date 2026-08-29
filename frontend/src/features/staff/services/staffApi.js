import sendHttpRequest from "../../../http_call/HttpRequest";
import { STAFF_SERVICE_URL } from "../../../service_url/StaffUrlConfig";

export default async function fetchStaffList() {
  const response = await sendHttpRequest(STAFF_SERVICE_URL, "GET");

  if (response.status === 200) {
    return response.json.data;
  } else {
    console.error("Error fetching staff:", response);
    return [];
  }
}
