import { api } from "../../../common/api/api";
import { API_ROUTES } from "../../../common/api/apiRoutes";

export function fetchNotifications(staffId) {
  return api(`${API_ROUTES.NOTIFICATIONS}/${staffId}`);
}

export function markNotificationRead(notificationId) {
  return api(`${API_ROUTES.NOTIFICATIONS}/${notificationId}/read`, {
    method: "PATCH",
  });
}

export function clearNotifications(staffId) {
  return api(`${API_ROUTES.NOTIFICATIONS}/${staffId}`, {
    method: "DELETE",
  });
}
