// src/common/hooks/useBookings.js
//
// Hook Tier for the Booking feature.
// Page components (BookingListPage, BookingCreatePage) must not call
// useSelector/useDispatch directly — they only call this hook.
//
// create()/loadActive()/loadPast()/confirm()/cancel() are wired up (3.3.S1-S6, M1-M2) —
// assign is still a Milestone 2 stub on the backend (BookingServiceImpl).

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
  confirmBookingStart,
  confirmBookingSuccess,
  confirmBookingFailure,
  cancelBookingStart,
  cancelBookingSuccess,
  cancelBookingFailure,
} from '../../features/booking/bookingSlice';
import * as bookingApi from '../../features/booking/services/bookingApi';

export function useBookings() {
  const dispatch = useDispatch();
  const {
    creating,
    createError,
    active,
    past,
    listLoading,
    listError,
    confirming,
    confirmError,
    cancelling,
    cancelError,
  } = useSelector((state) => state.booking);

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

  const confirm = useCallback(
    async (bookingId, payload) => {
      dispatch(confirmBookingStart());
      try {
        const response = await bookingApi.confirmBooking(bookingId, payload);
        dispatch(confirmBookingSuccess(response.data));
        return response.data;
      } catch (err) {
        dispatch(confirmBookingFailure(err.message));
        return null;
      }
    },
    [dispatch]
  );

  const cancel = useCallback(
    async (bookingId, payload) => {
      dispatch(cancelBookingStart());
      try {
        const response = await bookingApi.cancelBooking(bookingId, payload);
        dispatch(cancelBookingSuccess(response.data));
        return response.data;
      } catch (err) {
        dispatch(cancelBookingFailure(err.message));
        return null;
      }
    },
    [dispatch]
  );

  return {
    creating,
    createError,
    create,
    active,
    past,
    listLoading,
    listError,
    loadActive,
    loadPast,
    confirming,
    confirmError,
    confirm,
    cancelling,
    cancelError,
    cancel,
  };
}
