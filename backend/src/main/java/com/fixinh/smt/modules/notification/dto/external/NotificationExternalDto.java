package com.fixinh.smt.modules.notification.dto.external;

import java.time.LocalDateTime;

/** bookingId lets the frontend link the notification straight to the booking detail page. */
public record NotificationExternalDto(String id, String bookingId, String message, boolean read, LocalDateTime createdAt) {
}
