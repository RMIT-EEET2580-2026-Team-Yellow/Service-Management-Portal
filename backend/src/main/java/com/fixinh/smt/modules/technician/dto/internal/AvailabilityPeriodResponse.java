package com.fixinh.smt.modules.technician.dto.internal;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailabilityPeriodResponse(
        String id,
        String technicianId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        boolean active
) {
}
