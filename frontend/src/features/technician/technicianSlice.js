import { createSlice } from "@reduxjs/toolkit";

const technicianSlice = createSlice({
  name: "technician",
  initialState: {
    profile: null,
    profileLoading: false,
    profileError: null,

    myRequests: [],
    myRequestsLoading: false,
    myRequestsError: null,

    pendingRequests: [],
    pendingLoading: false,
    pendingError: null,

    submitLoading: false,
    submitError: null,

    actionLoading: false,
    actionError: null,

    bookings: [],
    bookingsLoading: false,
    bookingsError: null,

    bookingActionLoading: false,
    bookingActionError: null,
  },
  reducers: {
    fetchProfileStart(state) {
      state.profileLoading = true;
      state.profileError = null;
    },
    fetchProfileSuccess(state, action) {
      state.profileLoading = false;
      state.profile = action.payload;
    },
    fetchProfileFailure(state, action) {
      state.profileLoading = false;
      state.profileError = action.payload;
    },

    fetchMyRequestsStart(state) {
      state.myRequestsLoading = true;
      state.myRequestsError = null;
    },
    fetchMyRequestsSuccess(state, action) {
      state.myRequestsLoading = false;
      state.myRequests = action.payload;
    },
    fetchMyRequestsFailure(state, action) {
      state.myRequestsLoading = false;
      state.myRequestsError = action.payload;
    },

    fetchPendingRequestsStart(state) {
      state.pendingLoading = true;
      state.pendingError = null;
    },
    fetchPendingRequestsSuccess(state, action) {
      state.pendingLoading = false;
      state.pendingRequests = action.payload;
    },
    fetchPendingRequestsFailure(state, action) {
      state.pendingLoading = false;
      state.pendingError = action.payload;
    },

    submitRequestStart(state) {
      state.submitLoading = true;
      state.submitError = null;
    },
    submitRequestSuccess(state, action) {
      state.submitLoading = false;
      state.myRequests = [action.payload, ...state.myRequests];
    },
    submitRequestFailure(state, action) {
      state.submitLoading = false;
      state.submitError = action.payload;
    },

    respondRequestStart(state) {
      state.actionLoading = true;
      state.actionError = null;
    },
    respondRequestSuccess(state, action) {
      state.actionLoading = false;
      state.pendingRequests = state.pendingRequests.filter((r) => r.id !== action.payload.id);
    },
    respondRequestFailure(state, action) {
      state.actionLoading = false;
      state.actionError = action.payload;
    },

    fetchBookingsStart(state) {
      state.bookingsLoading = true;
      state.bookingsError = null;
    },
    fetchBookingsSuccess(state, action) {
      state.bookingsLoading = false;
      state.bookings = action.payload;
    },
    fetchBookingsFailure(state, action) {
      state.bookingsLoading = false;
      state.bookingsError = action.payload;
    },

    bookingActionStart(state) {
      state.bookingActionLoading = true;
      state.bookingActionError = null;
    },
    bookingActionSuccess(state) {
      state.bookingActionLoading = false;
    },
    bookingActionFailure(state, action) {
      state.bookingActionLoading = false;
      state.bookingActionError = action.payload;
    },
  },
});

export const {
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
  fetchBookingsStart,
  fetchBookingsSuccess,
  fetchBookingsFailure,
  bookingActionStart,
  bookingActionSuccess,
  bookingActionFailure,
} = technicianSlice.actions;

export default technicianSlice.reducer;
