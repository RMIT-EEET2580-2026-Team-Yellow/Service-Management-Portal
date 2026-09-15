package com.fixinh.smt.modules.notification.dto.external;

import java.time.LocalDateTime;

public record NotificationExternalDto(String id, String bookingId, String message, boolean read,
                                       LocalDateTime createdAt, String imageUrl) {
}
