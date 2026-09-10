// Hook Tier for the Technician "My Bookings" feature.
// TechnicianBookingPage calls only this hook — never the store or technicianApi directly.

import { useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import * as technicianApi from "../../features/technician/services/technicianApi";
import {
  fetchBookingsStart,
  fetchBookingsSuccess,
  fetchBookingsFailure,
  bookingActionStart,
  bookingActionSuccess,
  bookingActionFailure,
} from "../../features/technician/technicianSlice";

export function useTechnicianBookings() {
  const dispatch = useDispatch();
  const { bookings, bookingsLoading, bookingsError, bookingActionLoading, bookingActionError } = useSelector(
    (state) => state.technician
  );

  const loadBookings = useCallback(
    async (technicianId) => {
      dispatch(fetchBookingsStart());
      try {
        const response = await technicianApi.fetchMyBookings(technicianId);
        dispatch(fetchBookingsSuccess(response.data));
      } catch (err) {
        dispatch(fetchBookingsFailure(err.message));
      }
    },
    [dispatch]
  );

  const runAction = useCallback(
    async (apiCall, technicianId) => {
      dispatch(bookingActionStart());
      try {
        await apiCall();
        dispatch(bookingActionSuccess());
        await loadBookings(technicianId);
        return true;
      } catch (err) {
        dispatch(bookingActionFailure(err.message));
        return false;
      }
    },
    [dispatch, loadBookings]
  );

  const accept = useCallback(
    (technicianId, bookingId) =>
      runAction(() => technicianApi.acceptAssignment(technicianId, bookingId), technicianId),
    [runAction]
  );

  const reject = useCallback(
    (technicianId, bookingId) =>
      runAction(() => technicianApi.rejectAssignment(technicianId, bookingId), technicianId),
    [runAction]
  );

  const start = useCallback(
    (technicianId, bookingId) =>
      runAction(() => technicianApi.startWork(technicianId, bookingId), technicianId),
    [runAction]
  );

  const complete = useCallback(
    (technicianId, bookingId, payload) =>
      runAction(() => technicianApi.completeBooking(technicianId, bookingId, payload), technicianId),
    [runAction]
  );

  const abort = useCallback(
    (technicianId, bookingId, reason) =>
      runAction(() => technicianApi.abortBooking(technicianId, bookingId, reason), technicianId),
    [runAction]
  );

  return {
    bookings,
    bookingsLoading,
    bookingsError,
    bookingActionLoading,
    bookingActionError,
    loadBookings,
    accept,
    reject,
    start,
    complete,
    abort,
  };
}
