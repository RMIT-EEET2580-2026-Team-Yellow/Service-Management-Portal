// Path in repo: backend/src/main/java/com/fixinh/smt/integration/sbp/dto/SbpRatingDto.java
// CHANGE: bookingId Long -> String. This was already a latent bug (Booking.id has always been
// String), not just a result of the UUID switch — fixed while touching this file.
package com.fixinh.smt.integration.sbp.dto;

/** A single completed-booking rating, consumed for Receptionist review screens (3.4.S2). */
public record SbpRatingDto(String bookingId, int overallStars, int integrity, int quality,
                            int friendliness, String reviewText, java.util.List<String> evidenceImageUrls) {
}
