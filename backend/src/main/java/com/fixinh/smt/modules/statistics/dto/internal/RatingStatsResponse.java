package com.fixinh.smt.modules.statistics.dto.internal;

/** 3.4.M1/U1/U2 — overall + sub-category ratings, sourced from SBP via SbpIntegrationPort. */
public record RatingStatsResponse(String subjectId, String subjectType, String period,
                                   double averageOverall, double averageIntegrity,
                                   double averageQuality, double averageFriendliness) {
}
