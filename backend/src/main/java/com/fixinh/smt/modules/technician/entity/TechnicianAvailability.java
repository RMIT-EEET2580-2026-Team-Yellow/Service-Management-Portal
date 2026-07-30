// Path in repo: backend/src/main/java/com/fixinh/smt/modules/technician/entity/TechnicianAvailability.java
package com.fixinh.smt.modules.technician.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Extracted out of {@code Technician} into its own document so the naming matches SBT's ERD
 * (agreed 29/7: "AvailabilitySchedule" → "TechnicianAvailability"). One-to-one with a
 * {@code Technician}, referenced by {@code technicianId}.
 */
@Document(collection = "technician_availability")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TechnicianAvailability {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String technicianId;

    /** 5 consecutive working days, e.g. ["MON","TUE","WED","THU","FRI"]. */
    private List<String> workingDays;

    private LocalTime dailyStart; // >= 08:00
    private LocalTime dailyEnd;   // <= 20:00

    private boolean currentlyAvailable;
}
