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

  const result = await response.json();

  if (!response.ok || result.success === false) {
    throw new Error(result.message || "API request failed");
  }

  return result;
}