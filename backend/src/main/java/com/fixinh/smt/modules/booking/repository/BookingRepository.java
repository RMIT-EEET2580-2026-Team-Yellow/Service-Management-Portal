package com.fixinh.smt.modules.booking.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fixinh.smt.modules.booking.entity.Booking;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByStatusIn(List<String> statuses);
}
