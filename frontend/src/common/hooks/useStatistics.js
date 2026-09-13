// src/common/hooks/useStatistics.js
//
// Hook Tier for the Statistics feature (3.4). Pages must call only this hook —
// never useSelector/useDispatch or statisticsApi directly.

import { useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
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
} from "../../features/statistics/statisticsSlice";
import * as statisticsApi from "../../features/statistics/services/statisticsApi";

export function useStatistics() {
  const dispatch = useDispatch();
  const {
    revenue,
    revenueLoading,
    revenueError,
    serviceTypeRatings,
    serviceTypeRatingsLoading,
    serviceTypeRatingsError,
    technicianRatings,
    technicianRatingsLoading,
    technicianRatingsError,
    bookingReview,
    bookingReviewLoading,
    bookingReviewError,
  } = useSelector((state) => state.statistics);

  const loadRevenue = useCallback(async () => {
    dispatch(fetchRevenueStart());
    try {
      const response = await statisticsApi.fetchRevenueStats();
      dispatch(fetchRevenueSuccess(response.data));
    } catch (err) {
      dispatch(fetchRevenueFailure(err.message));
    }
  }, [dispatch]);

  const loadServiceTypeRatings = useCallback(
    async (period = "monthly") => {
      dispatch(fetchServiceTypeRatingsStart());
      try {
        const response = await statisticsApi.fetchServiceTypeRatings(period);
        dispatch(fetchServiceTypeRatingsSuccess(response.data));
      } catch (err) {
        dispatch(fetchServiceTypeRatingsFailure(err.message));
      }
    },
    [dispatch]
  );

  const loadTechnicianRatings = useCallback(
    async (period = "monthly") => {
      dispatch(fetchTechnicianRatingsStart());
      try {
        const response = await statisticsApi.fetchTechnicianRatings(period);
        dispatch(fetchTechnicianRatingsSuccess(response.data));
      } catch (err) {
        dispatch(fetchTechnicianRatingsFailure(err.message));
      }
    },
    [dispatch]
  );

  const loadBookingReview = useCallback(
    async (bookingId) => {
      dispatch(fetchBookingReviewStart());
      try {
        const response = await statisticsApi.fetchBookingReview(bookingId);
        dispatch(fetchBookingReviewSuccess(response.data));
      } catch (err) {
        dispatch(fetchBookingReviewFailure(err.message));
      }
    },
    [dispatch]
  );

  const clearReview = useCallback(() => dispatch(clearBookingReview()), [dispatch]);

  return {
    revenue,
    revenueLoading,
    revenueError,
    loadRevenue,
    serviceTypeRatings,
    serviceTypeRatingsLoading,
    serviceTypeRatingsError,
    loadServiceTypeRatings,
    technicianRatings,
    technicianRatingsLoading,
    technicianRatingsError,
    loadTechnicianRatings,
    bookingReview,
    bookingReviewLoading,
    bookingReviewError,
    loadBookingReview,
    clearReview,
  };
}