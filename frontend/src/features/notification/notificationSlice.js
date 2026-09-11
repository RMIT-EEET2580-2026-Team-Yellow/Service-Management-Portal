// src/features/notification/notificationSlice.js
//
// Plain createSlice reducers (start/success/failure), same pattern as bookingSlice/
// statisticsSlice — the async dispatch + try/catch lives in useNotifications, not here.

import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  items: [],
  unreadCount: 0,
  loading: false,
  error: null,
};

function recomputeUnreadCount(items) {
  return items.filter((n) => !n.read).length;
}

const notificationSlice = createSlice({
  name: "notification",
  initialState,
  reducers: {
    fetchNotificationsStart(state) {
      state.loading = true;
      state.error = null;
    },
    fetchNotificationsSuccess(state, action) {
      state.loading = false;
      state.items = action.payload;
      state.unreadCount = recomputeUnreadCount(action.payload);
    },
    fetchNotificationsFailure(state, action) {
      state.loading = false;
      state.error = action.payload;
    },

    markAsReadStart(state) {
      state.error = null;
    },
    markAsReadSuccess(state, action) {
      const notificationId = action.payload;
      const notification = state.items.find((n) => n.id === notificationId);
      if (notification) {
        notification.read = true;
      }
      state.unreadCount = recomputeUnreadCount(state.items);
    },
    markAsReadFailure(state, action) {
      state.error = action.payload;
    },

    clearAllStart(state) {
      state.error = null;
    },
    clearAllSuccess(state) {
      state.items = [];
      state.unreadCount = 0;
    },
    clearAllFailure(state, action) {
      state.error = action.payload;
    },
  },
});

export const {
  fetchNotificationsStart,
  fetchNotificationsSuccess,
  fetchNotificationsFailure,
  markAsReadStart,
  markAsReadSuccess,
  markAsReadFailure,
  clearAllStart,
  clearAllSuccess,
  clearAllFailure,
} = notificationSlice.actions;

export default notificationSlice.reducer;
