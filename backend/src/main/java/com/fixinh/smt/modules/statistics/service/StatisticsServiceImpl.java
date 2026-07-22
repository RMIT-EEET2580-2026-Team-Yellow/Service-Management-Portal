package com.fixinh.smt.modules.statistics.service;

import com.fixinh.smt.integration.sbp.SbpIntegrationPort;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import com.fixinh.smt.modules.statistics.dto.internal.RatingStatsResponse;
import com.fixinh.smt.modules.statistics.dto.internal.RevenueStatsResponse;
import com.fixinh.smt.modules.statistics.service.internal.StatisticsInternalService;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // TODO Milestone 2: aggregate Completed bookings' serviceFee (material cost excluded) via
        // bookingExternalService.findCompletedBetween(...) (3.4.S3/M3).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<RatingStatsResponse> getServiceTypeRatings(String period) {
        // TODO Milestone 2: map sbpIntegrationPort.getServiceTypeRatingStats(period) (3.4.U1).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public List<RatingStatsResponse> getTechnicianRatings(String period) {
        // TODO Milestone 2: map sbpIntegrationPort.getTechnicianRatingStats(period) (3.4.U2).
        throw new UnsupportedOperationException("Milestone 2");
    }

    @Override
    public Object getBookingReview(String bookingId, String requesterRole) {
        // TODO Milestone 2: block TECHNICIAN role (3.4.S4/M4), else sbpIntegrationPort.getRatingForBooking(...).
        throw new UnsupportedOperationException("Milestone 2");
    }
}
