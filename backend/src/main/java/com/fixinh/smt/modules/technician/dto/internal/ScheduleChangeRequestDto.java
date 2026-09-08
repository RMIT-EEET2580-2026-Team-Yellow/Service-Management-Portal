package com.fixinh.smt.modules.technician.dto.internal;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Technician-submitted request body to replace their current working schedule — exactly 5 of
 *  the 7 days, any combination. Hours are fixed at 08:00-20:00, not chosen by the Technician. */
public record ScheduleChangeRequestDto(
        @NotNull List<String> workingDays
) {
}
