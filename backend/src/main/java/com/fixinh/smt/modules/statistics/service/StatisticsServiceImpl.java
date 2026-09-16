package com.fixinh.smt.modules.statistics.service;

import com.fixinh.smt.common.exception.ApiException;
import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.integration.sbp.SbpIntegrationPort;
import com.fixinh.smt.integration.sbp.dto.SbpRatingDto;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import com.fixinh.smt.modules.statistics.dto.internal.RatingStatsResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RevenueStatsResponse;
import com.fixinh.smt.modules.statistics.service.internal.StatisticsInternalService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Depends only on {@link BookingExternalService} (Booking module) and {@link SbpIntegrationPort}
 * (SBP) — never touches another module's repository directly (A.2.4).
 */
@Service
public class StatisticsServiceImpl implements StatisticsInternalService {

    private final BookingExternalService bookingExternalService;
    private final SbpIntegrationPort sbpIntegrationPort;

    public StatisticsServiceImpl(BookingExternalService bookingExternalService, SbpIntegrationPort sbpIntegrationPort) {
        this.bookingExternalService = bookingExternalService;
        this.sbpIntegrationPort = sbpIntegrationPort;
    }

    @Override
    public RevenueStatsResponse getRevenue() {
        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate yearStart = today.withDayOfYear(1);

        double currentMonthRevenue = sumServiceFee(bookingExternalService.findCompletedBetween(monthStart, today));

        List<BookingExternalDto> completedThisYear = bookingExternalService.findCompletedBetween(yearStart, today);
        double currentYearRevenue = sumServiceFee(completedThisYear);

        Map<String, Double> revenueByServiceType = new HashMap<>();
        for (BookingExternalDto booking : completedThisYear) {
            if (booking.serviceFee() == null) {
                continue;
            }
            String type = booking.serviceType() != null ? booking.serviceType() : "Unknown";
            revenueByServiceType.merge(type, booking.serviceFee(), Double::sum);
        }

        Map<String, Double> revenueShareByServiceType = new HashMap<>();
        for (Map.Entry<String, Double> entry : revenueByServiceType.entrySet()) {
            double share = currentYearRevenue > 0 ? (entry.getValue() / currentYearRevenue) * 100 : 0;
            revenueShareByServiceType.put(entry.getKey(), share);
        }

        return new RevenueStatsResponse(currentMonthRevenue, currentYearRevenue, revenueByServiceType, revenueShareByServiceType);
    }

    private double sumServiceFee(List<BookingExternalDto> bookings) {
        return bookings.stream()
                .map(BookingExternalDto::serviceFee)
                .filter(fee -> fee != null)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    @Override
    public List<RatingStatsResponse> getServiceTypeRatings(String period) {
        return sbpIntegrationPort.getServiceTypeRatingStats(period).stream()
                .map(dto -> new RatingStatsResponse(dto.subjectId(), dto.subjectType(), period,
                        dto.averageOverall(), dto.averageIntegrity(), dto.averageQuality(), dto.averageFriendliness()))
                .toList();
    }

    @Override
    public List<RatingStatsResponse> getTechnicianRatings(String period) {
        return sbpIntegrationPort.getTechnicianRatingStats(period).stream()
                .map(dto -> new RatingStatsResponse(dto.subjectId(), dto.subjectType(), period,
                        dto.averageOverall(), dto.averageIntegrity(), dto.averageQuality(), dto.averageFriendliness()))
                .toList();
    }

    @Override
    public Object getBookingReview(String bookingId, String requesterRole) {
        // 3.4.S4/M4 — Technicians must never see ratings/reviews/evidence, even via direct API call.
        if ("TECHNICIAN".equalsIgnoreCase(requesterRole)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Technicians are not allowed to view booking ratings or reviews.");
        }
        SbpRatingDto rating = sbpIntegrationPort.getRatingForBooking(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No rating found for booking: " + bookingId));
        return rating;
    }
}
