// Path in repo: backend/src/main/java/com/fixinh/smt/modules/auth/entity/User.java
// CHANGES: staffId Long -> String (UUID, matches Staff.id after the PK switch), id now
// explicitly generated as a UUID string.
package com.fixinh.smt.modules.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Login credential record, keyed to a {@code Staff} document by {@code staffId}. Kept separate
 * from Staff so password/Google-OAuth concerns stay inside the Auth module (A.2.1).
 */
@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String staffId; 

    private String email;

    private String passwordHash;

    private String googleId;

    private String role; // ADMIN | RECEPTIONIST | TECHNICIAN
}
