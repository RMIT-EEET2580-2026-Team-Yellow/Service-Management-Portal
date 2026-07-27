// src/common/hooks/useStatistics.js
//
// Hook Tier for the Statistics feature.
// StatisticsPage (not yet routed) is intended to call only this hook —
// never the store directly.
//
// Depends on statisticsSlice exporting the thunks below (Milestone 2 —
// the slice file currently exists but is empty, and there is no route
// or page wired up for Statistics yet).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchRevenueStats, fetchRatingStats } from '../store/statisticsSlice';

export function useStatistics() {
  const dispatch = useDispatch();
  const { revenue, ratings, loading, error } = useSelector((state) => state.statistics);

  const loadRevenue = useCallback(
    (periodType, periodValue) => dispatch(fetchRevenueStats({ periodType, periodValue })),
    [dispatch]
  );

  const loadRatings = useCallback(
    (periodType, periodValue) => dispatch(fetchRatingStats({ periodType, periodValue })),
    [dispatch]
  );

  return { revenue, ratings, loading, error, loadRevenue, loadRatings };
}
