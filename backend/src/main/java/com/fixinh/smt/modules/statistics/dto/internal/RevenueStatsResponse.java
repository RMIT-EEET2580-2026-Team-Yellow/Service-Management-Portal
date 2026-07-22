package com.fixinh.smt.modules.statistics.dto.internal;

import java.util.Map;

/** 3.4.S3/M3 — service revenue (fees only, material cost excluded) split by Service Type. */
public record RevenueStatsResponse(double currentMonthRevenue, double currentYearRevenue,
                                    Map<String, Double> revenueByServiceType,
                                    Map<String, Double> revenueShareByServiceType) {
}
