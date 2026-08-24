// src/common/hooks/useBookings.js
//
// Hook Tier for the Booking feature.
// Page components (BookingListPage, BookingCreatePage) must not call
// useSelector/useDispatch directly — they only call this hook.
//
// Only create() is wired up (3.3.S1/S2) — listActive/listPast/confirm/assign/cancel
// are still Milestone 2 stubs on the backend (BookingServiceImpl).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { createBookingStart, createBookingSuccess, createBookingFailure } from '../../features/booking/bookingSlice';
import * as bookingApi from '../../features/booking/services/bookingApi';

export function useBookings() {
  const dispatch = useDispatch();
  const { creating, createError } = useSelector((state) => state.booking);

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

  return { creating, createError, create };
}
