import { createSlice } from "@reduxjs/toolkit";

// The Active tab renders `filtered` (or `searchResults` while searching) — not `active` — so a
// confirm/assign/cancel success has to patch every list the booking could currently be shown in,
// not just `active`, or the change stays invisible until the next full refetch.
function patchBookingInList(list, updated) {
  const index = list.findIndex((b) => b.id === updated.id);
  if (index !== -1) {
    list[index] = updated;
  }
}

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

    searchResults: [],
    searching: false,
    searchError: null,

    confirming: false,
    confirmError: null,

    cancelling: false,
    cancelError: null,

    assigning: false,
    assignError: null,

    customerLookup: null,
    lookingUpCustomer: false,
    customerLookupError: null
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
      patchBookingInList(state.active, updated);
      patchBookingInList(state.filtered, updated);
      patchBookingInList(state.searchResults, updated);
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
      patchBookingInList(state.active, updated);
      patchBookingInList(state.filtered, updated);
      patchBookingInList(state.searchResults, updated);
    },
    assignBookingFailure(state, active) {
      state.assigning = false;
      state.assignError = active.payload;
    },

    cancelBookingStart(state) {
      state.cancelling = true;
      state.cancelError = null;
    },
    cancelBookingSuccess(state, action) {
      state.cancelling = false;
      const updated = action.payload;
      patchBookingInList(state.active, updated);
      patchBookingInList(state.filtered, updated);
      patchBookingInList(state.searchResults, updated);
    },
    cancelBookingFailure(state, action) {
      state.cancelling = false;
      state.cancelError = action.payload;
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

    searchBookingsStart(state) {
      state.searching = true;
      state.searchError = null;
    },
    searchBookingsSuccess(state, action) {
      state.searching = false;
      state.searchResults = action.payload;
    },
    searchBookingsFailure(state, action) {
      state.searching = false;
      state.searchError = action.payload;
    },

    lookupCustomerStart(state) {
      state.lookingUpCustomer = true;
      state.customerLookupError = null;
      state.customerLookup = null;
    },
    lookupCustomerSuccess(state, action) {
      state.lookingUpCustomer = false;
      state.customerLookup = action.payload;
    },
    lookupCustomerFailure(state, action) {
      state.lookingUpCustomer = false;
      state.customerLookupError = action.payload;
    },
    // Invalidate a previous confirmation the moment the Customer ID field changes again — a
    // stale "confirmed" name must never stay on screen for a different id (3.3.S1/S2).
    clearCustomerLookup(state) {
      state.customerLookup = null;
      state.lookingUpCustomer = false;
      state.customerLookupError = null;
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
  fetchFilteredStart,
  fetchFilteredSuccess,
  fetchFilteredFailure,
  assignBookingStart,
  assignBookingSuccess,
  assignBookingFailure,
  searchBookingsStart,
  searchBookingsSuccess,
  searchBookingsFailure,
  lookupCustomerStart,
  lookupCustomerSuccess,
  lookupCustomerFailure,
  clearCustomerLookup
} = bookingSlice.actions;
export default bookingSlice.reducer;
