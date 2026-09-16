// Base includes /v1 — matches every backend controller (@RequestMapping("/api/v1/..."))
// NOTE: GET requests now go through src/http_call/HttpRequest.js instead — this file
// is kept only for the existing POST-based login flow (authApi.js).
// Relative (same-origin as the frontend, 5173) on purpose — vite.config.js proxies /api/* to the
// backend server-side, so the browser never has to trust the backend's own self-signed cert.
const BASE_URL = "/api/v1";

export async function api(endpoint, options = {}) {
  const token = localStorage.getItem("token");

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    ...options,
  });

  // 401 while a token was attached means THAT token is expired/invalid — clear it so the
  // next reload sends the user back to the login page instead of retrying with it. A 401
  // with no token attached is a failed login attempt itself (wrong password, unlinked
  // Google account, ...) — fall through so the real backend message reaches the caller.
  // 403 means the token is valid but the role doesn't allow this action, which is
  // not a reason to log the user out — fall through and surface the real message.
  if (response.status === 401 && token) {
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

// BASE_URL is "<origin>/api/v1" — files come back from the backend as an app-relative path
// (e.g. "/api/v1/files/<uuid>.jpg") rather than a full URL, so an <img src> needs this prefix
// to reach port 8080 instead of resolving against the frontend's own origin on 5173.
const API_ORIGIN = BASE_URL.replace(/\/api\/v1$/, "");

/** Resolves a backend-relative file URL (as returned by uploadFile) into one a browser can load
 *  directly, e.g. in an <img src>. Already-absolute URLs pass through unchanged. */
export function resolveFileUrl(url) {
  return /^https?:\/\//i.test(url) ? url : `${API_ORIGIN}${url}`;
}

/**
 * Multipart upload — kept separate from api() because that helper always forces
 * "Content-Type: application/json", which would break the multipart boundary fetch needs to set
 * itself from the FormData body.
 */
export async function uploadFile(endpoint, file) {
  const token = localStorage.getItem("token");
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    method: "POST",
    headers: {
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: formData,
  });

  if (response.status === 401) {
    localStorage.removeItem("token");
    throw new Error("Your session has expired. Please sign in again.");
  }

  let result;
  try {
    result = await response.json();
  } catch {
    throw new Error(`Upload failed (HTTP ${response.status})`);
  }

  if (!response.ok || result.success === false) {
    throw new Error(result.message || "Upload failed");
  }

  return result.data;
}