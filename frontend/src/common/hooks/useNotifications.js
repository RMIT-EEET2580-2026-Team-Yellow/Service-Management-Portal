// src/common/hooks/useNotifications.js
//
// Hook Tier for the Notification feature.
// NotificationPanel calls only this hook — never the store directly.
//
// Depends on notificationSlice exporting the thunks below (Milestone 2 —
// the slice file currently exists but is empty).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchNotifications, markAsRead } from '../store/notificationSlice';

export function useNotifications() {
  const dispatch = useDispatch();
  const { items, unreadCount, loading, error } = useSelector((state) => state.notification);

  const load = useCallback(() => dispatch(fetchNotifications()), [dispatch]);

  const read = useCallback(
    (notificationId) => dispatch(markAsRead(notificationId)),
    [dispatch]
  );

  return { items, unreadCount, loading, error, load, read };
}
