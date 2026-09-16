import { createSlice } from "@reduxjs/toolkit";

const statisticsSlice = createSlice({
  name: "statistics",
  initialState: {
    revenue: null,
    revenueLoading: false,
    revenueError: null,

    serviceTypeRatings: [],
    serviceTypeRatingsLoading: false,
    serviceTypeRatingsError: null,

    technicianRatings: [],
    technicianRatingsLoading: false,
    technicianRatingsError: null,

    bookingReview: null,
    bookingReviewLoading: false,
    bookingReviewError: null,
  },
  reducers: {
    fetchRevenueStart(state) {
      state.revenueLoading = true;
      state.revenueError = null;
    },
    fetchRevenueSuccess(state, action) {
      state.revenueLoading = false;
      state.revenue = action.payload;
    },
    fetchRevenueFailure(state, action) {
      state.revenueLoading = false;
      state.revenueError = action.payload;
    },

    fetchServiceTypeRatingsStart(state) {
      state.serviceTypeRatingsLoading = true;
      state.serviceTypeRatingsError = null;
    },
    fetchServiceTypeRatingsSuccess(state, action) {
      state.serviceTypeRatingsLoading = false;
      state.serviceTypeRatings = action.payload;
    },
    fetchServiceTypeRatingsFailure(state, action) {
      state.serviceTypeRatingsLoading = false;
      state.serviceTypeRatingsError = action.payload;
    },

    fetchTechnicianRatingsStart(state) {
      state.technicianRatingsLoading = true;
      state.technicianRatingsError = null;
    },
    fetchTechnicianRatingsSuccess(state, action) {
      state.technicianRatingsLoading = false;
      state.technicianRatings = action.payload;
    },
    fetchTechnicianRatingsFailure(state, action) {
      state.technicianRatingsLoading = false;
      state.technicianRatingsError = action.payload;
    },

    fetchBookingReviewStart(state) {
      state.bookingReviewLoading = true;
      state.bookingReviewError = null;
    },
    fetchBookingReviewSuccess(state, action) {
      state.bookingReviewLoading = false;
      state.bookingReview = action.payload;
    },
    fetchBookingReviewFailure(state, action) {
      state.bookingReviewLoading = false;
      state.bookingReviewError = action.payload;
    },
    // Called on RatingDetailPage unmount / bookingId change so a stale review
    // from the previous booking never flashes before the new fetch resolves.
    clearBookingReview(state) {
      state.bookingReview = null;
      state.bookingReviewError = null;
    },
  },
});

export const {
  fetchRevenueStart,
  fetchRevenueSuccess,
  fetchRevenueFailure,
  fetchServiceTypeRatingsStart,
  fetchServiceTypeRatingsSuccess,
  fetchServiceTypeRatingsFailure,
  fetchTechnicianRatingsStart,
  fetchTechnicianRatingsSuccess,
  fetchTechnicianRatingsFailure,
  fetchBookingReviewStart,
  fetchBookingReviewSuccess,
  fetchBookingReviewFailure,
  clearBookingReview,
} = statisticsSlice.actions;

export default statisticsSlice.reducer;