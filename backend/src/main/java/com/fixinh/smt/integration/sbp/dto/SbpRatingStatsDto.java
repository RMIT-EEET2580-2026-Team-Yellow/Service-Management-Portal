package com.fixinh.smt.integration.sbp.dto;

/** Aggregated rating statistics per Service Type or Technician (3.4.U1/U2). */
public record SbpRatingStatsDto(String subjectId, String subjectType, double averageOverall,
                                 double averageIntegrity, double averageQuality, double averageFriendliness) {
}
