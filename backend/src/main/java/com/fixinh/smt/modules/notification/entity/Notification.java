// Path in repo: backend/src/main/java/com/fixinh/smt/modules/notification/entity/Notification.java
// CHANGE: id now explicitly generated as a UUID string.
package com.fixinh.smt.modules.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification implements Persistable<String> {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String recipientStaffId;
    private String bookingId;
    private String message;
    private boolean read;
    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    /** Same reasoning as {@code Booking.isNew()}: id is pre-populated with a UUID instead of left
     *  null for Mongo to assign, so Spring Data's default is-new check (id == null) would treat
     *  every insert as an update and skip {@code @CreatedDate} population — which would break
     *  findByRecipientStaffIdOrderByCreatedAtDesc (every notification would sort as "null"). */
    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
