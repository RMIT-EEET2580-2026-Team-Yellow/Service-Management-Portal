// src/common/hooks/useNotifications.js
//
// Hook Tier for the Notification feature.
// NotificationPage/Sidebar call only this hook — never the store or notificationApi directly.

import { useCallback, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchNotificationsStart,
  fetchNotificationsSuccess,
  fetchNotificationsFailure,
  markAsReadStart,
  markAsReadSuccess,
  markAsReadFailure,
  clearAllStart,
  clearAllSuccess,
  clearAllFailure,
} from "../../features/notification/notificationSlice";
import * as notificationApi from "../../features/notification/services/notificationApi";
import { useAuth } from "./useAuth";

// 3.3.U2 "real-time" is achieved by polling rather than a push transport (WebSocket/SSE) —
// simpler to set up/demo for this project's scope, at the cost of up to this many seconds of
// lag before a status change shows up.
const POLL_INTERVAL_MS = 8000;

export function useNotifications() {
  const dispatch = useDispatch();
  const { user } = useAuth();
  const { items, unreadCount, loading, error } = useSelector((state) => state.notification);

  const load = useCallback(async () => {
    if (!user?.staffId) return;
    dispatch(fetchNotificationsStart());
    try {
      const response = await notificationApi.fetchNotifications(user.staffId);
      dispatch(fetchNotificationsSuccess(response.data));
    } catch (err) {
      dispatch(fetchNotificationsFailure(err.message));
    }
  }, [dispatch, user]);

  const read = useCallback(
    async (notificationId) => {
      dispatch(markAsReadStart());
      try {
        await notificationApi.markNotificationRead(notificationId);
        dispatch(markAsReadSuccess(notificationId));
      } catch (err) {
        dispatch(markAsReadFailure(err.message));
      }
    },
    [dispatch]
  );

  const clearAll = useCallback(async () => {
    if (!user?.staffId) return;
    dispatch(clearAllStart());
    try {
      await notificationApi.clearNotifications(user.staffId);
      dispatch(clearAllSuccess());
    } catch (err) {
      dispatch(clearAllFailure(err.message));
    }
  }, [dispatch, user]);

  // Poll for new notifications every POLL_INTERVAL_MS while a Staff member is logged in — this
  // is what makes Sidebar's unread badge and NotificationPage's list update "live" (3.3.U2)
  useEffect(() => {
    if (!user?.staffId) return undefined;

    const intervalId = setInterval(load, POLL_INTERVAL_MS);
    return () => clearInterval(intervalId);
  }, [load, user]);

  return { items, unreadCount, loading, error, load, read, clearAll };
}
