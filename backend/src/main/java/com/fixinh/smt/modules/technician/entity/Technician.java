// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/entity/Technician.java
// CHANGE: schedule fields (workingDays/dailyStart/dailyEnd/currentlyAvailable) moved out to the
// new TechnicianAvailability entity (see TechnicianAvailability.java) — this class now only
// holds the link to the Staff account. UUID default added for the id.
package com.fixinh.smt.modules.technician.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Extends a {@code Staff} account (by {@code staffId}) so it can be auto-assigned work
 * (3.1.M5, 3.2.M5, 3.3.M3). Scheduling data now lives in {@link TechnicianAvailability}
 * (technicianId -> availability), not embedded here.
 */
@Document(collection = "technicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Technician {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String staffId;
}
