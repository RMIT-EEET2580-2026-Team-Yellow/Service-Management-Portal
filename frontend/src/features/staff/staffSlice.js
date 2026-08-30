import { createSlice } from "@reduxjs/toolkit";

const staffSlice = createSlice({
  name: "staff",
  initialState: {
    list: [],
    listLoading: false,
    listError: null,
  },
  reducers: {
    fetchStaffStart(state) {
      state.listLoading = true;
      state.listError = null;
    },
    fetchStaffSuccess(state, action) {
      state.listLoading = false;
      state.list = action.payload;
    },
    fetchStaffFailure(state, action) {
      state.listLoading = false;
      state.listError = action.payload;
    },
  },
});

export const { fetchStaffStart, fetchStaffSuccess, fetchStaffFailure } = staffSlice.actions;
export default staffSlice.reducer;
