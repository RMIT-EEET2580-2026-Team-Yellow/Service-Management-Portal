package com.fixinh.smt.modules.notification.service;

import com.fixinh.smt.modules.notification.dto.external.NotificationExternalDto;
import com.fixinh.smt.modules.notification.repository.NotificationRepository;
import com.fixinh.smt.modules.notification.service.external.NotificationExternalService;
import com.fixinh.smt.modules.notification.service.internal.NotificationInternalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationExternalService, NotificationInternalService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void notifyStatusChange(String recipientStaffId, String bookingId, String newStatus) {
        // TODO Milestone 2: persist + push over WebSocket/SSE (3.3.U2), also inform SBP (4.M3).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<NotificationExternalDto> listFor(String staffId) {
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public void markRead(String notificationId) {
        throw new UnsupportedOperationException("Milestone 2");
    }
}
