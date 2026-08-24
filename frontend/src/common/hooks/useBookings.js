// src/common/hooks/useBookings.js
//
// Hook Tier for the Booking feature.
// Page components (BookingListPage, BookingCreatePage) must not call
// useSelector/useDispatch directly — they only call this hook.
//
// create()/loadActive()/loadPast() are wired up (3.3.S1-S5, M2) — confirm/assign/cancel
// are still Milestone 2 stubs on the backend (BookingServiceImpl).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  createBookingStart,
  createBookingSuccess,
  createBookingFailure,
  fetchBookingsStart,
  fetchActiveBookingsSuccess,
  fetchPastBookingsSuccess,
  fetchBookingsFailure,
} from '../../features/booking/bookingSlice';
import * as bookingApi from '../../features/booking/services/bookingApi';

export function useBookings() {
  const dispatch = useDispatch();
  const { creating, createError, active, past, listLoading, listError } = useSelector((state) => state.booking);

  const create = useCallback(
    async (bookingData) => {
      dispatch(createBookingStart());
      try {
        const response = await bookingApi.createBooking(bookingData);
        dispatch(createBookingSuccess(response.data));
        return response.data;
      } catch (err) {
        dispatch(createBookingFailure(err.message));
        return null;
      }
    },
    [dispatch]
  );

  const loadActive = useCallback(async () => {
    dispatch(fetchBookingsStart());
    try {
      const response = await bookingApi.fetchActiveBookings();
      dispatch(fetchActiveBookingsSuccess(response.data));
    } catch (err) {
      dispatch(fetchBookingsFailure(err.message));
    }
  }, [dispatch]);

  const loadPast = useCallback(async () => {
    dispatch(fetchBookingsStart());
    try {
      const response = await bookingApi.fetchPastBookings();
      dispatch(fetchPastBookingsSuccess(response.data));
    } catch (err) {
      dispatch(fetchBookingsFailure(err.message));
    }
  }, [dispatch]);

  return { creating, createError, create, active, past, listLoading, listError, loadActive, loadPast };
}
