// src/common/hooks/useBookings.js
//
// Hook Tier for the Booking feature.
// Page components (BookingListPage, BookingDetailPage) must not call
// useSelector/useDispatch directly — they only call this hook, which
// wraps bookingSlice and exposes a clean, page-friendly API.
//
// Depends on bookingSlice exporting the thunks below (Milestone 2 — the
// slice file currently exists but is empty).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchActiveBookings,
  fetchPastBookings,
  confirmBooking,
  assignTechnician,
  cancelBooking,
} from '../store/bookingSlice';

export function useBookings() {
  const dispatch = useDispatch();
  const { active, past, loading, error } = useSelector((state) => state.booking);

  const loadActive = useCallback(() => dispatch(fetchActiveBookings()), [dispatch]);
  const loadPast = useCallback(() => dispatch(fetchPastBookings()), [dispatch]);

  const confirm = useCallback(
    (bookingId) => dispatch(confirmBooking(bookingId)),
    [dispatch]
  );

  const assign = useCallback(
    (bookingId, technicianId) => dispatch(assignTechnician({ bookingId, technicianId })),
    [dispatch]
  );

  const cancel = useCallback(
    (bookingId) => dispatch(cancelBooking(bookingId)),
    [dispatch]
  );

  return { active, past, loading, error, loadActive, loadPast, confirm, assign, cancel };
}
