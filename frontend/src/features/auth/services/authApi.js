import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to POST /api/v1/auth/login. No Redux/state logic here. */
export function login(emailOrPhone, password) {
  return api(API_ROUTES.LOGIN, {
    method: "POST",
    body: JSON.stringify({ emailOrPhone, password }),
  });
}
