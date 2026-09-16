import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

/** Service Tier — talks to POST /api/v1/auth/login. No Redux/state logic here. */
export function login(emailOrPhone, password) {
  return api(API_ROUTES.LOGIN, {
    method: "POST",
    body: JSON.stringify({ emailOrPhone, password }),
  });
}

/** Service Tier — talks to POST /api/v1/auth/logout. No Redux/state logic here. */
export function logout() {
  return api(API_ROUTES.LOGOUT, { method: "POST" });
}

/** Service Tier — talks to POST /api/v1/auth/google. */
export function loginWithGoogle(googleIdToken) {
  return api(API_ROUTES.GOOGLE_LOGIN, {
    method: "POST",
    body: JSON.stringify({ idToken: googleIdToken }),
  });
}
