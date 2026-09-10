import { createSlice } from "@reduxjs/toolkit";
import { getUser, saveUser, removeUser } from "../../common/utils/storage";

const authSlice = createSlice({
  name: "auth",
  initialState: {
    user: getUser(),
    token: localStorage.getItem("token"),
    isAuthenticated: !!localStorage.getItem("token"),
    loading: false,
    error: null,
  },
  reducers: {
    loginStart(state) {
      state.loading = true;
      state.error = null;
    },
    loginSuccess(state, action) {
      state.loading = false;
      state.isAuthenticated = true;
      state.token = action.payload.token;
      state.user = { staffId: action.payload.staffId, role: action.payload.role };
      localStorage.setItem("token", action.payload.token);
      saveUser(state.user);
    },
    loginFailure(state, action) {
      state.loading = false;
      state.error = action.payload;
    },
    logout(state) {
      localStorage.removeItem("token");
      removeUser();
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
    },
  },
});

export const { loginStart, loginSuccess, loginFailure, logout } = authSlice.actions;
export default authSlice.reducer;
