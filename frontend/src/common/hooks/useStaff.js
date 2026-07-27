// src/common/hooks/useStaff.js
//
// Hook Tier for the Staff feature.
// StaffListPage and StaffFormPage call only this hook — never the
// store directly.
//
// Depends on staffSlice exporting the thunks below (Milestone 2 — the
// slice file currently exists but is empty).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchStaffList,
  searchStaff,
  createStaff,
  updateStaff,
} from '../store/staffSlice';

export function useStaff() {
  const dispatch = useDispatch();
  const { list, selected, loading, error } = useSelector((state) => state.staff);

  const loadAll = useCallback(() => dispatch(fetchStaffList()), [dispatch]);

  const search = useCallback(
    (query) => dispatch(searchStaff(query)),
    [dispatch]
  );

  const create = useCallback(
    (staffData) => dispatch(createStaff(staffData)),
    [dispatch]
  );

  const update = useCallback(
    (staffId, staffData) => dispatch(updateStaff({ staffId, staffData })),
    [dispatch]
  );

  return { list, selected, loading, error, loadAll, search, create, update };
}
