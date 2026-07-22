package com.fixinh.smt.modules.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fixinh.smt.modules.notification.entity.Notification;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByRecipientStaffIdOrderByCreatedAtDesc(String recipientStaffId);
}
