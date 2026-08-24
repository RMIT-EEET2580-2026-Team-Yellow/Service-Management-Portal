import { createSlice } from "@reduxjs/toolkit";

const bookingSlice = createSlice({
  name: "booking",
  initialState: {
    creating: false,
    createError: null,
    lastCreated: null,
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
  },
});

export const { createBookingStart, createBookingSuccess, createBookingFailure } = bookingSlice.actions;
export default bookingSlice.reducer;
