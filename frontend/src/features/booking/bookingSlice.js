import { createSlice } from "@reduxjs/toolkit";

const bookingSlice = createSlice({
  name: "booking",
  initialState: {
    creating: false,
    createError: null,
    lastCreated: null,

    active: [],
    past: [],
    listLoading: false,
    listError: null,
  },
  reducers: {
    createBookingStart(state) {
      state.creating = true;
      state.createError = null;
    },
    createBookingSuccess(state, action) {
      state.creating = false;
      state.lastCreated = action.payload;
    },
    createBookingFailure(state, action) {
      state.creating = false;
      state.createError = action.payload;
    },

    fetchBookingsStart(state) {
      state.listLoading = true;
      state.listError = null;
    },
    fetchActiveBookingsSuccess(state, action) {
      state.listLoading = false;
      state.active = action.payload;
    },
    fetchPastBookingsSuccess(state, action) {
      state.listLoading = false;
      state.past = action.payload;
    },
    fetchBookingsFailure(state, action) {
      state.listLoading = false;
      state.listError = action.payload;
    },
  },
});

export const {
  createBookingStart,
  createBookingSuccess,
  createBookingFailure,
  fetchBookingsStart,
  fetchActiveBookingsSuccess,
  fetchPastBookingsSuccess,
  fetchBookingsFailure,
} = bookingSlice.actions;
export default bookingSlice.reducer;
