// Hook Tier for the Technician schedule-change-request feature.
// TechnicianSchedulePage calls only this hook — never the store or technicianApi directly.

import { useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import * as technicianApi from "../../features/technician/services/technicianApi";
import {
  fetchProfileStart,
  fetchProfileSuccess,
  fetchProfileFailure,
  fetchMyRequestsStart,
  fetchMyRequestsSuccess,
  fetchMyRequestsFailure,
  fetchPendingRequestsStart,
  fetchPendingRequestsSuccess,
  fetchPendingRequestsFailure,
  submitRequestStart,
  submitRequestSuccess,
  submitRequestFailure,
  respondRequestStart,
  respondRequestSuccess,
  respondRequestFailure,
} from "../../features/technician/technicianSlice";

export function useTechnicianSchedule() {
  const dispatch = useDispatch();
  const {
    profile,
    profileLoading,
    profileError,
    myRequests,
    myRequestsLoading,
    myRequestsError,
    pendingRequests,
    pendingLoading,
    pendingError,
    submitLoading,
    submitError,
    actionLoading,
    actionError,
  } = useSelector((state) => state.technician);

  const loadMyProfile = useCallback(
    async (staffId) => {
      dispatch(fetchProfileStart());
      try {
        const response = await technicianApi.fetchTechnicianByStaffId(staffId);
        dispatch(fetchProfileSuccess(response.data));
        return response.data;
      } catch (err) {
        dispatch(fetchProfileFailure(err.message));
        return null;
      }
    },
    [dispatch]
  );

  const loadMyRequests = useCallback(
    async (technicianId) => {
      dispatch(fetchMyRequestsStart());
      try {
        const response = await technicianApi.fetchMyScheduleRequests(technicianId);
        dispatch(fetchMyRequestsSuccess(response.data));
      } catch (err) {
        dispatch(fetchMyRequestsFailure(err.message));
      }
    },
    [dispatch]
  );

  const loadPendingRequests = useCallback(async () => {
    dispatch(fetchPendingRequestsStart());
    try {
      const response = await technicianApi.fetchPendingScheduleRequests();
      dispatch(fetchPendingRequestsSuccess(response.data));
    } catch (err) {
      dispatch(fetchPendingRequestsFailure(err.message));
    }
  }, [dispatch]);

  const submitRequest = useCallback(
    async (technicianId, schedule) => {
      dispatch(submitRequestStart());
      try {
        const response = await technicianApi.submitScheduleChangeRequest(technicianId, schedule);
        dispatch(submitRequestSuccess(response.data));
        return true;
      } catch (err) {
        dispatch(submitRequestFailure(err.message));
        return false;
      }
    },
    [dispatch]
  );

  const approveRequest = useCallback(
    async (requestId) => {
      dispatch(respondRequestStart());
      try {
        const response = await technicianApi.approveScheduleRequest(requestId);
        dispatch(respondRequestSuccess(response.data));
        return true;
      } catch (err) {
        dispatch(respondRequestFailure(err.message));
        return false;
      }
    },
    [dispatch]
  );

  const rejectRequest = useCallback(
    async (requestId, reason) => {
      dispatch(respondRequestStart());
      try {
        const response = await technicianApi.rejectScheduleRequest(requestId, reason);
        dispatch(respondRequestSuccess(response.data));
        return true;
      } catch (err) {
        dispatch(respondRequestFailure(err.message));
        return false;
      }
    },
    [dispatch]
  );

  return {
    profile,
    profileLoading,
    profileError,
    myRequests,
    myRequestsLoading,
    myRequestsError,
    pendingRequests,
    pendingLoading,
    pendingError,
    submitLoading,
    submitError,
    actionLoading,
    actionError,
    loadMyProfile,
    loadMyRequests,
    loadPendingRequests,
    submitRequest,
    approveRequest,
    rejectRequest,
  };
}
