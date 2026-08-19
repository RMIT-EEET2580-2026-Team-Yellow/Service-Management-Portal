import sendHttpRequest from "../../../http_call/HttpRequest";
import { PROVINCE_SERVICE_URL } from "../../../service_url/ProvinceUrlConfig";

export default async function fetchProvinces() {
  const response = await sendHttpRequest(PROVINCE_SERVICE_URL, "GET");

  if (response.status === 200) {
    return response.json;
  } else {
    console.error("Error fetching provinces:", response);
    return [];
  }
}
