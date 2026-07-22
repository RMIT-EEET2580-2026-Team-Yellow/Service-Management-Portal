package com.fixinh.smt.modules.notification.service.internal;

import com.fixinh.smt.modules.notification.dto.external.NotificationExternalDto;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code NotificationController}. */
public interface NotificationInternalService {

    List<NotificationExternalDto> listFor(String staffId);

    void markRead(String notificationId);
}
