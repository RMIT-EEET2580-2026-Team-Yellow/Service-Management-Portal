package com.fixinh.smt.modules.booking.dto.internal;

import java.util.List;

/** 3.3.M1 — cancellation reason + evidence images. */
public record CancelBookingRequest(String reason, List<String> evidenceImageUrls) {
}
