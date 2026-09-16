import { Navigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { getDefaultRouteForRole } from "../utils/roleAccess";

export default function ProtectedRoute({ children, roles }) {
  const isAuthenticated = useSelector((state) => state.auth.isAuthenticated);
  const user = useSelector((state) => state.auth.user);

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  if (roles && !roles.includes(user?.role)) {
    return <Navigate to={getDefaultRouteForRole(user?.role)} replace />;
  }

  return children;
}