package com.fixinh.smt.common.util;

import java.time.LocalDate;
import java.time.LocalTime;

/** Shared date/time helpers, e.g. validating the 08:00-20:00 Technician availability window (3.1.M5). */
public final class DateUtil {

    private DateUtil() {
    }

    public static boolean isWithinWorkingHours(LocalTime start, LocalTime end) {
        // TODO Milestone 2
        throw new UnsupportedOperationException("Milestone 2");
    }

    public static boolean isValidDateRange(LocalDate start, LocalDate end) {
        throw new UnsupportedOperationException("Milestone 2");
    }
}
