package com.fixinh.smt.modules.technician.dto.internal;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record ScheduleChangeResponse(
        String id,
        String technicianId,
        String technicianFullName,
        List<String> currentWorkingDays,
        LocalTime currentDailyStart,
        LocalTime currentDailyEnd,
        List<String> requestedWorkingDays,
        LocalTime requestedDailyStart,
        LocalTime requestedDailyEnd,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime respondedAt,
        String rejectReason
) {
}
