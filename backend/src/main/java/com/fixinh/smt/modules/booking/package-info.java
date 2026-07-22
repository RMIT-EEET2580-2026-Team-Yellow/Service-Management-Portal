/**
 * Booking bounded context: Receptionist confirms/assigns bookings created by SBP, tracks the
 * Created -> Confirmed -> Scheduled -> Working -> Completed lifecycle, search/filter/sort
 * (SRS 3.3.S1-S8, 3.3.M1-M10, 3.3.U1-U2). Depends only on
 * {@code TechnicianExternalService} and {@code NotificationExternalService} of other modules
 * (A.2.4).
 */
package com.fixinh.smt.modules.booking;
