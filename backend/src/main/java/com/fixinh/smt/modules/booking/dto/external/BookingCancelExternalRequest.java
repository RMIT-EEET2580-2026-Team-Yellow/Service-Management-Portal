package com.fixinh.smt.modules.booking.dto.external;

import java.util.List;

/** External request DTO (A.3.2) — body SBP sends to PATCH /api/v1/integration/bookings/{id}/cancel
 *  (4.M2), mirrors the internal CancelBookingRequest shape. */
public record BookingCancelExternalRequest(String reason, List<String> evidenceImageUrls) {
}
