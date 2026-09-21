// src/common/hooks/useBookings.js
//
// Hook Tier for the Booking feature.
// Page components (BookingListPage, BookingCreatePage) must not call
// useSelector/useDispatch directly — they only call this hook.
//
// create()/loadActive()/loadPast()/confirm()/cancel()/loadFiltered()/search() are wired up
// (3.3.S1-S6, M1-M2, M9-M10) — assign is still a Milestone 2 stub on the backend
// (BookingServiceImpl).

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
  fetchFilteredStart,
  fetchFilteredSuccess,
  fetchFilteredFailure,
  searchBookingsStart,
  searchBookingsSuccess,
  searchBookingsFailure,
  assignBookingStart,
  assignBookingSuccess,
  assignBookingFailure,
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
    filtered,
    filterLoading,
    filterError,
    searchResults,
    searching,
    searchError,
    confirming,
    confirmError,
    cancelling,
    cancelError,
    assigning,
    assignError
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

  const loadFiltered = useCallback(
    async (params) => {
      dispatch(fetchFilteredStart());
      try {
        const response = await bookingApi.fetchFilteredBookings(params);
        dispatch(fetchFilteredSuccess(response.data));
      } catch (err) {
        dispatch(fetchFilteredFailure(err.message));
      }
    },
    [dispatch]
  );

  const search = useCallback(
    async (query) => {
      dispatch(searchBookingsStart());
      try {
        const response = await bookingApi.searchBookings(query);
        dispatch(searchBookingsSuccess(response.data));
      } catch (err) {
        dispatch(searchBookingsFailure(err.message));
      }
    },
    [dispatch]
  );

  const assign = useCallback(
    async (bookingId, technicianIds) => {
      dispatch(assignBookingStart());

      try {
        const response = await bookingApi.assignTechnicians(bookingId, technicianIds);
        dispatch(assignBookingSuccess(response.data));
        return response.data;
      } catch (err) {
        dispatch(assignBookingFailure(err.message));
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
    filtered,
    filterLoading,
    filterError,
    loadFiltered,
    searchResults,
    searching,
    searchError,
    search,
    confirming,
    confirmError,
    confirm,
    cancelling,
    cancelError,
    cancel,
    assigning,
    assignError,
    assign
  };
}
