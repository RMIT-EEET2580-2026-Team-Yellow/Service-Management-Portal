/**
 * Statistics bounded context: monthly/yearly revenue and rating dashboards for Receptionists/
 * Admin (SRS 3.4). Computes revenue from {@code BookingExternalService} (Booking module) and
 * MUST source all rating data through {@code SbpIntegrationPort} — this module never queries
 * rating data itself (3.4.S1/S5). No other module depends on Statistics, so it exposes only an
 * Internal Interface (A.2.4).
 */
package com.fixinh.smt.modules.statistics;
