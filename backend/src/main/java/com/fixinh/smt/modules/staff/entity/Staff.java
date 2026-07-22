package com.fixinh.smt.modules.staff.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

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
    private String id;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String role; // ADMIN | RECEPTIONIST | TECHNICIAN

    private String streetNumber;
    private String streetName;
    private String ward;
    private String province;

    private boolean active;

    @CreatedDate
    private LocalDateTime createdAt;
}
