package com.fixinh.smt.modules.statistics.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RatingStatsResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RevenueStatsResponse;
import com.fixinh.smt.modules.statistics.service.internal.StatisticsInternalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Revenue and rating statistics are ADMIN-only reporting (A.2.a). */
@RestController
@RequestMapping("/api/v1/statistics")
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsInternalService statisticsInternalService;

    public StatisticsController(StatisticsInternalService statisticsInternalService) {
        this.statisticsInternalService = statisticsInternalService;
    }

    @GetMapping("/revenue")
    public ApiResponse<RevenueStatsResponse> revenue() {
        return ApiResponse.ok(statisticsInternalService.getRevenue());
    }

    @GetMapping("/ratings/service-types")
    public ApiResponse<List<RatingStatsResponse>> serviceTypeRatings(@RequestParam(defaultValue = "monthly") String period) {
        return ApiResponse.ok(statisticsInternalService.getServiceTypeRatings(period));
    }

    @GetMapping("/ratings/technicians")
    public ApiResponse<List<RatingStatsResponse>> technicianRatings(@RequestParam(defaultValue = "monthly") String period) {
        return ApiResponse.ok(statisticsInternalService.getTechnicianRatings(period));
    }

    @GetMapping("/bookings/{bookingId}/review")
    public ApiResponse<Object> bookingReview(@PathVariable String bookingId, @RequestHeader("X-Role") String role) {
        return ApiResponse.ok(statisticsInternalService.getBookingReview(bookingId, role));
    }
}
