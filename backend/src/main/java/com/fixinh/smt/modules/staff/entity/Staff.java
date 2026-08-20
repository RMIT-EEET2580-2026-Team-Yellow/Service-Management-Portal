// Path in repo: backend/src/main/java/com/fixinh/smt/modules/staff/entity/Staff.java
// CHANGE: id now explicitly generated as a UUID string (was left to Mongo's default ObjectId).
// email/active fields unchanged — already correct, confirmed 30/7.
package com.fixinh.smt.modules.staff.entity;

import com.fixinh.smt.common.security.RoleConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Maps to a Receptionist/Technician/Admin account. Horizontally sharded by branch province
 * (3.1.U1) — physically stored in either the Northern or Southern Mongo database, selected by
 * {@link com.fixinh.smt.common.sharding.ShardKeyResolver}; this document carries no shard flag.
 */
@Document(collection = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String fullName;

    private String email;

    private String phoneNumber;

    private RoleConfig role;

    private String streetNumber;
    private String streetName;
    private String ward;
    private String province;

    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;
}
