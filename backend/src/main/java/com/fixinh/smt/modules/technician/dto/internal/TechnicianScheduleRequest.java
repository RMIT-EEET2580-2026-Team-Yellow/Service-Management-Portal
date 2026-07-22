package com.fixinh.smt.modules.technician.dto.internal;

import java.time.LocalTime;
import java.util.List;

/** 3.1.M5/3.2.M5 — Admin defines a Technician's 5-day working schedule (08:00-20:00 window). */
public record TechnicianScheduleRequest(String staffId, List<String> workingDays,
                                         LocalTime dailyStart, LocalTime dailyEnd) {
}
