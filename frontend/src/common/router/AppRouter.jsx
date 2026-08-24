import { Routes, Route } from "react-router-dom";

import AppLayout from "../../components/reusable/AppLayout";
import LoginPage from "../../features/auth/pages/LoginPage";
import DashboardPage from "../../features/dashboard/pages/DashboardPage";
import BookingListPage from "../../features/booking/pages/BookingListPage";
import BookingDetailPage from "../../features/booking/pages/BookingDetailPage";
import BookingCreatePage from "../../features/booking/pages/BookingCreatePage";
import StaffListPage from "../../features/staff/pages/StaffListPage";
import TechnicianBookingPage from "../../features/technician/pages/TechnicianBookingPage";
import StatisticsPage from "../../features/statistics/pages/StatisticsPage";
import RatingDetailPage from "../../features/statistics/pages/RatingDetailPage";
import NotificationPage from "../../features/notification/pages/NotificationPage";
import ProtectedRoute from "./ProtectedRoute";

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />

      <Route element={<ProtectedRoute><AppLayout /></ProtectedRoute>}>
        <Route path="/dashboard" element={<DashboardPage />} />

        <Route path="/bookings" element={<BookingListPage />} />
        <Route path="/bookings/create" element={<BookingCreatePage />} />
        <Route path="/bookings/detail" element={<BookingDetailPage />} />

        <Route path="/staff" element={<StaffListPage />} />

        <Route path="/technician" element={<TechnicianBookingPage />} />

        <Route path="/statistics" element={<StatisticsPage />} />
        <Route path="/statistics/rating/:bookingId" element={<RatingDetailPage />} />

        <Route path="/notifications" element={<NotificationPage />} />
      </Route>
    </Routes>
  );
}