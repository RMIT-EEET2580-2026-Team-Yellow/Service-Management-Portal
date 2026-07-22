package com.fixinh.smt.modules.notification.service.external;

/**
 * External Interface (A.3.1/A.3.2) — the ONLY way the Booking module triggers a real-time
 * in-app notification (3.3.U2) when a status changes to Completed, Cancelled by Customer,
 * Terminated by Customer, or Aborted by Technician.
 */
public interface NotificationExternalService {

    void notifyStatusChange(String recipientStaffId, String bookingId, String newStatus);
}
