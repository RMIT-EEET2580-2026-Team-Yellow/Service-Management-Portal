// src/common/hooks/useAuth.js
//
// Hook Tier for the Auth feature.
// LoginPage (and NavBar, once role-based menus are built) call only
// this hook — never the store directly.
//
// Depends on authSlice exporting the thunks/state below (Milestone 2 —
// the slice file currently exists but is empty).

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { login, logout } from '../store/authSlice';

export function useAuth() {
  const dispatch = useDispatch();
  const { user, token, isAuthenticated, loading, error } = useSelector((state) => state.auth);

  const signIn = useCallback(
    (email, password) => dispatch(login({ email, password })),
    [dispatch]
  );

  const signOut = useCallback(() => dispatch(logout()), [dispatch]);

  return { user, token, isAuthenticated, loading, error, signIn, signOut };
}
