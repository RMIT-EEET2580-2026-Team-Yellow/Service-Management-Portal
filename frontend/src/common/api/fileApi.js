import { uploadFile, resolveFileUrl } from "./api";
import { API_ROUTES } from "./apiRoutes";

/** Service Tier — talks to POST /api/v1/files. Shared by every feature that attaches an image
 *  (cancel-booking evidence 3.3.M1, complete-booking signed bill 3.3.M7). Resolves the returned
 *  path to an absolute URL so callers can drop it straight into an <img src>. */
export async function uploadImage(file) {
  const { url } = await uploadFile(API_ROUTES.FILES, file);
  return { url: resolveFileUrl(url) };
}
