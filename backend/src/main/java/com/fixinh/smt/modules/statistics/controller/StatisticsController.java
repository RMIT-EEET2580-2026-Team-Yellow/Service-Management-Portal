package com.fixinh.smt.modules.statistics.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RatingStatsResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RevenueStatsResponse;
import com.fixinh.smt.modules.statistics.service.internal.StatisticsInternalService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Revenue and rating statistics are ADMIN-only reporting (A.2.a). */
@RestController
@RequestMapping("/api/v1/statistics")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
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
    public ApiResponse<Object> bookingReview(@PathVariable String bookingId) {
        return ApiResponse.ok(statisticsInternalService.getBookingReview(bookingId, currentRole()));
    }

    /**
     * Role comes from the authenticated JWT (set by JwtFilter as a "ROLE_&lt;role&gt;" authority),
     * never from a client-supplied header — a header is trivially spoofable and would defeat the
     * whole point of blocking Technicians from ratings (3.4.S4/M4).
     */
    private String currentRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .orElse(null);
    }
}
