// src/common/hooks/useAuth.js
//
// Hook Tier for the Auth feature.
// LoginPage and Sidebar call only this hook — never the store or authApi directly.

import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { loginStart, loginSuccess, loginFailure, logout } from '../../features/auth/authSlice';
import * as authApi from '../../features/auth/services/authApi';

export function useAuth() {
  const dispatch = useDispatch();
  const { user, token, isAuthenticated, loading, error } = useSelector((state) => state.auth);

  const signIn = useCallback(
    async (email, password) => {
      dispatch(loginStart());
      try {
        const response = await authApi.login(email, password);
        dispatch(loginSuccess(response.data)); // { token, staffId, role }
        return response.data;
      } catch (err) {
        dispatch(loginFailure(err.message));
        return null;
      }
    },
    [dispatch]
  );

  const signOut = useCallback(async () => {
    try {
      await authApi.logout();
    } catch {
      // Best-effort: still clear local session even if the server call fails
      // (e.g. token already expired) — logout must never strand the user.
    } finally {
      dispatch(logout());
    }
  }, [dispatch]);

  return { user, token, isAuthenticated, loading, error, signIn, signOut };
}
