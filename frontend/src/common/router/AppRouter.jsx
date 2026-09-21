import { Routes, Route } from "react-router-dom";

import AppLayout from "../../components/reusable/AppLayout";
import LoginPage from "../../features/auth/pages/LoginPage";
import DashboardPage from "../../features/dashboard/pages/DashboardPage";
import BookingListPage from "../../features/booking/pages/BookingListPage";
import BookingDetailPage from "../../features/booking/pages/BookingDetailPage";
import BookingCreatePage from "../../features/booking/pages/BookingCreatePage";
import StaffListPage from "../../features/staff/pages/StaffListPage";
import TechnicianBookingPage from "../../features/technician/pages/TechnicianBookingPage";
import TechnicianSchedulePage from "../../features/technician/pages/TechnicianSchedulePage";
import StatisticsPage from "../../features/statistics/pages/StatisticsPage";
import RatingDetailPage from "../../features/statistics/pages/RatingDetailPage";
import NotificationPage from "../../features/notification/pages/NotificationPage";
import ProtectedRoute from "./ProtectedRoute";
import { ROUTE_ROLES } from "../utils/roleAccess";

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />

      <Route element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
        <Route
          path="/dashboard"
          element={<ProtectedRoute roles={ROUTE_ROLES["/dashboard"]}><DashboardPage /></ProtectedRoute>}
        />

        <Route
          path="/bookings"
          element={<ProtectedRoute roles={ROUTE_ROLES["/bookings"]}><BookingListPage /></ProtectedRoute>}
        />
        <Route
          path="/bookings/create"
          element={<ProtectedRoute roles={ROUTE_ROLES["/bookings/create"]}><BookingCreatePage /></ProtectedRoute>}
        />
        <Route
          path="/bookings/detail"
          element={<ProtectedRoute roles={ROUTE_ROLES["/bookings/detail"]}><BookingDetailPage /></ProtectedRoute>}
        />

        <Route
          path="/staff"
          element={<ProtectedRoute roles={ROUTE_ROLES["/staff"]}><StaffListPage /></ProtectedRoute>}
        />

        <Route
          path="/technician"
          element={<ProtectedRoute roles={ROUTE_ROLES["/technician"]}><TechnicianBookingPage /></ProtectedRoute>}
        />
        <Route
          path="/technician/schedule"
          element={<ProtectedRoute roles={ROUTE_ROLES["/technician/schedule"]}><TechnicianSchedulePage /></ProtectedRoute>}
        />

        <Route
          path="/statistics"
          element={<ProtectedRoute roles={ROUTE_ROLES["/statistics"]}><StatisticsPage /></ProtectedRoute>}
        />
        <Route
          path="/statistics/rating/:bookingId"
          element={<ProtectedRoute roles={ROUTE_ROLES["/statistics/rating"]}><RatingDetailPage /></ProtectedRoute>}
        />

        <Route
          path="/notifications"
          element={<ProtectedRoute roles={ROUTE_ROLES["/notifications"]}><NotificationPage /></ProtectedRoute>}
        />
      </Route>
    </Routes>
  );
}