import { configureStore } from '@reduxjs/toolkit'
import authReducer from '../../features/auth/authSlice'
import bookingReducer from '../../features/booking/bookingSlice'
import staffReducer from '../../features/staff/staffSlice'
import technicianReducer from '../../features/technician/technicianSlice'
import notificationReducer from '../../features/notification/notificationSlice'

export default configureStore({
  reducer: {
    auth: authReducer,
    booking: bookingReducer,
    staff: staffReducer,
    technician: technicianReducer,
    notification: notificationReducer,
  },
})