package com.fixinh.smt.modules.notification.service;

import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.modules.notification.dto.external.NotificationExternalDto;
import com.fixinh.smt.modules.notification.entity.Notification;
import com.fixinh.smt.modules.notification.repository.NotificationRepository;
import com.fixinh.smt.modules.notification.service.external.NotificationExternalService;
import com.fixinh.smt.modules.notification.service.internal.NotificationInternalService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationExternalService, NotificationInternalService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Override
    public void notifyStatusChange(String recipientStaffId, String bookingId, String newStatus, String detail, String imageUrl) {
        String message = "Booking #" + bookingId + " is now " + newStatus + ".";
        if (StringUtils.hasText(detail)) {
            message += " " + detail;
        }

        Notification notification = Notification.builder()
                .recipientStaffId(recipientStaffId)
                .bookingId(bookingId)
                .message(message)
                .read(false)
                .imageUrl(imageUrl)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationExternalDto> listFor(String staffId) {
        return notificationRepository.findByRecipientStaffIdOrderByCreatedAtDesc(staffId).stream()
                .map(NotificationServiceImpl::toDto)
                .toList();
    }

    @Override
    public void markRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void clearAll(String staffId) {
        notificationRepository.deleteByRecipientStaffId(staffId);
    }

    private static NotificationExternalDto toDto(Notification notification) {
        return new NotificationExternalDto(
                notification.getId(),
                notification.getBookingId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getImageUrl()
        );
    }
}
