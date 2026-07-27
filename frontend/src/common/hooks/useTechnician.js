// src/common/hooks/useTechnician.js
//
// Hook Tier for the Technician feature.
// TechnicianPage calls only this hook — never the store directly.
//
// Depends on technicianSlice exporting the thunks below (Milestone 2 —
// the slice file currently exists but is empty).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchAssignments,
  fetchSchedule,
  acceptAssignment,
  rejectAssignment,
} from '../store/technicianSlice';

export function useTechnician() {
  const dispatch = useDispatch();
  const { assignments, schedule, loading, error } = useSelector((state) => state.technician);

  const loadAssignments = useCallback(() => dispatch(fetchAssignments()), [dispatch]);
  const loadSchedule = useCallback(() => dispatch(fetchSchedule()), [dispatch]);

  const accept = useCallback(
    (assignmentId) => dispatch(acceptAssignment(assignmentId)),
    [dispatch]
  );

  const reject = useCallback(
    (assignmentId) => dispatch(rejectAssignment(assignmentId)),
    [dispatch]
  );

  return { assignments, schedule, loading, error, loadAssignments, loadSchedule, accept, reject };
}
