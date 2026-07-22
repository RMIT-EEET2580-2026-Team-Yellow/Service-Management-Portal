import { Routes, Route } from "react-router-dom";

import LoginPage from "../../features/auth/pages/LoginPage";
import BookingListPage from "../../features/booking/pages/BookingListPage";
import BookingDetailPage from "../../features/booking/pages/BookingDetailPage";
import StaffListPage from "../../features/staff/pages/StaffListPage";
import StaffCreatePage from "../../features/staff/pages/StaffCreatePage";
import StaffEditPage from "../../features/staff/pages/StaffEditPage";
import TechnicianBookingPage from "../../features/technician/pages/TechnicianBookingPage";

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />

      <Route path="/bookings" element={<BookingListPage />} />
      <Route path="/bookings/detail" element={<BookingDetailPage />} />

      <Route path="/staff" element={<StaffListPage />} />
      <Route path="/staff/create" element={<StaffCreatePage />} />
      <Route path="/staff/edit" element={<StaffEditPage />} />

      <Route path="/technician" element={<TechnicianBookingPage />} />
    </Routes>
  );
}