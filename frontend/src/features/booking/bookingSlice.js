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

    filtered: [],
    filterLoading: false,
    filterError: null,

    confirming: false,
    confirmError: null,

    assigning: false,
    assignError: null
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
    assignBookingStart(state, action) {
      state.assigning = true;
      state.assignError = null;
    },
    assignBookingSuccess(state, action) {
      state.assigning = false;
      const updated = action.payload;
      const index = state.active.findIndex((booking) => booking.id === updated.id);
      if (index !== -1) {
        state.active[index] = updated;
      }
    },
    assignBookingFailure(state, active) {
      state.assigning = false;
      state.assignError = active.payload;
    },

    fetchFilteredStart(state) {
      state.filterLoading = true;
      state.filterError = null;
    },
    fetchFilteredSuccess(state, action) {
      state.filterLoading = false;
      state.filtered = action.payload;
    },
    fetchFilteredFailure(state, action) {
      state.filterLoading = false;
      state.filterError = action.payload;
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
  fetchFilteredStart,
  fetchFilteredSuccess,
  fetchFilteredFailure,
  assignBookingStart,
  assignBookingSuccess,
  assignBookingFailure
} = bookingSlice.actions;
export default bookingSlice.reducer;
