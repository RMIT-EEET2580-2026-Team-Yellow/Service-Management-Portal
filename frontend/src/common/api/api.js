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

  // Spring Security's default access-denied response for a missing/expired/invalid JWT
  // has no body, so response.json() below would throw a confusing "Unexpected end of
  // JSON input" instead of surfacing the actual problem. Clear the stale token so the
  // next reload sends the user back to the login page instead of retrying with it.
  if (response.status === 401 || response.status === 403) {
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