package com.fixinh.smt.modules.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fixinh.smt.modules.booking.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByStatusIn(List<String> statuses);

    /** Used by BookingExternalService.findByCustomerId(...) - SBP-facing "my bookings" list (4.S1). */
    List<Booking> findByCustomerId(String customerId);

    /** Used by Statistics (3.4.S3/M3) via BookingExternalService.findCompletedBetween(...). */
    List<Booking> findByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
}
