import { USER_ROLE } from "./constants";

// Single source of truth for which roles may reach which top-level route.
// Used by both ProtectedRoute (router guard) and Sidebar (menu filtering)
// so they can't drift apart.
export const ROUTE_ROLES = {
  "/dashboard": [USER_ROLE.ADMIN, USER_ROLE.RECEPTIONIST],
  "/bookings": [USER_ROLE.ADMIN, USER_ROLE.RECEPTIONIST],
  "/bookings/create": [USER_ROLE.ADMIN, USER_ROLE.RECEPTIONIST],
  "/bookings/detail": [USER_ROLE.ADMIN, USER_ROLE.RECEPTIONIST],
  "/staff": [USER_ROLE.ADMIN],
  "/technician": [USER_ROLE.ADMIN, USER_ROLE.TECHNICIAN],
  "/statistics": [USER_ROLE.ADMIN],
  "/statistics/rating": [USER_ROLE.ADMIN],
  "/notifications": [USER_ROLE.ADMIN, USER_ROLE.RECEPTIONIST, USER_ROLE.TECHNICIAN],
};

export function getDefaultRouteForRole(role) {
  return role === USER_ROLE.TECHNICIAN ? "/technician" : "/dashboard";
}
