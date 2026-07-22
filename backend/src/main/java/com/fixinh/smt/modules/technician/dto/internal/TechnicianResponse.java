package com.fixinh.smt.modules.technician.dto.internal;

import java.time.LocalTime;
import java.util.List;

public record TechnicianResponse(String id, String staffId, String fullName,
                                  List<String> workingDays, LocalTime dailyStart, LocalTime dailyEnd,
                                  boolean currentlyAvailable) {
}
