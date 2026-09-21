// Base includes /v1 — matches every backend controller (@RequestMapping("/api/v1/..."))
// NOTE: GET requests now go through src/http_call/HttpRequest.js instead — this file
// is kept only for the existing POST-based login flow (authApi.js).
const BASE_URL = "http://localhost:8080/api/v1";

export async function api(endpoint, options = {}) {
  const token = localStorage.getItem("token");

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...options,
  });

  // 401 means the token itself is missing/expired/invalid — clear it so the next
  // reload sends the user back to the login page instead of retrying with it.
  // 403 means the token is valid but the role doesn't allow this action, which is
  // not a reason to log the user out — fall through and surface the real message.
  if (response.status === 401) {
    localStorage.removeItem("token");
    throw new Error("Your session has expired. Please sign in again.");
  }

  let result;
  try {
    result = await response.json();
  } catch {
    throw new Error(`Request failed (HTTP ${response.status})`);
  }

  if (!response.ok || result.success === false) {
    throw new Error(result.message || "API request failed");
  }

  return result;
}