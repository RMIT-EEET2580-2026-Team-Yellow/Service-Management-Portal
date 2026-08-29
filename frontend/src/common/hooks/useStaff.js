// SMT only reads Staff data here (SBT owns the create/edit UI) 
// GET /api/v1/staff (3.1.S5-S8).

import { useCallback } from "react";
import { useDispatch, useSelector } from "react-redux";
import { fetchStaffStart, fetchStaffSuccess, fetchStaffFailure } from "../../features/staff/staffSlice";
import fetchStaffList from "../../features/staff/services/staffApi";

export function useStaff() {
  const dispatch = useDispatch();
  const { list, listLoading, listError } = useSelector((state) => state.staff);

  const loadStaff = useCallback(async () => {
    dispatch(fetchStaffStart());
    try {
      const list = await fetchStaffList();
      dispatch(fetchStaffSuccess(list));
    } catch (err) {
      dispatch(fetchStaffFailure(err.message));
    }
  }, [dispatch]);

  return { list, listLoading, listError, loadStaff };
}
