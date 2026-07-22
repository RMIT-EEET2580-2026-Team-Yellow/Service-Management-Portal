package com.fixinh.smt.modules.statistics.service.internal;

import com.fixinh.smt.modules.statistics.dto.internal.RatingStatsResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RevenueStatsResponse;

import java.util.List;

/** Internal Interface (A.3.1/A.3.2) — consumed only by {@code StatisticsController}. */
public interface StatisticsInternalService {

    RevenueStatsResponse getRevenue();

    List<RatingStatsResponse> getServiceTypeRatings(String period);

    List<RatingStatsResponse> getTechnicianRatings(String period);

    /** 3.4.S2 — a single Completed booking's review, for Receptionists (Technicians excluded, 3.4.S4/M4). */
    Object getBookingReview(String bookingId, String requesterRole);
}
