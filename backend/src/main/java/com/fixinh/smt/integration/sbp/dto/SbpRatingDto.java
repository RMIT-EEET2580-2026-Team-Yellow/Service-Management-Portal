package com.fixinh.smt.integration.sbp.dto;

/** A single completed-booking rating, consumed for Receptionist review screens (3.4.S2). */
public record SbpRatingDto(Long bookingId, int overallStars, int integrity, int quality,
                            int friendliness, String reviewText, java.util.List<String> evidenceImageUrls) {
}
