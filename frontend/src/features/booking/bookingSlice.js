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

    confirming: false,
    confirmError: null,

    cancelling: false,
    cancelError: null,
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

    confirmBookingStart(state) {
      state.confirming = true;
      state.confirmError = null;
    },
    confirmBookingSuccess(state, action) {
      state.confirming = false;
      const updated = action.payload;
      const index = state.active.findIndex((b) => b.id === updated.id);
      if (index !== -1) {
        state.active[index] = updated;
      }
    },
    confirmBookingFailure(state, action) {
      state.confirming = false;
      state.confirmError = action.payload;
    },

    cancelBookingStart(state) {
      state.cancelling = true;
      state.cancelError = null;
    },
    cancelBookingSuccess(state, action) {
      state.cancelling = false;
      const updated = action.payload;
      const index = state.active.findIndex((b) => b.id === updated.id);
      if (index !== -1) {
        state.active[index] = updated;
      }
    },
    cancelBookingFailure(state, action) {
      state.cancelling = false;
      state.cancelError = action.payload;
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
  confirmBookingStart,
  confirmBookingSuccess,
  confirmBookingFailure,
  cancelBookingStart,
  cancelBookingSuccess,
  cancelBookingFailure,
} = bookingSlice.actions;
export default bookingSlice.reducer;
