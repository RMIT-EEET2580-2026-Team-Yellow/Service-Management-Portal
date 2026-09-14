package com.fixinh.smt.common.config;

import com.fixinh.smt.modules.booking.entity.Booking;
import com.fixinh.smt.modules.booking.repository.BookingRepository;
import com.fixinh.smt.modules.staff.entity.Staff;
import com.fixinh.smt.modules.staff.repository.StaffShardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Sample Bookings covering a few different lifecycle statuses (3.3), so Active/Past Bookings
 * list, filters, and Statistics revenue have something to show on a freshly-seeded local Mongo
 * without needing to walk the full create -> confirm -> schedule -> ... flow by hand first.
 * Runs after {@link StaffSeeder} so it can attribute bookings to a real Receptionist staffId.
 */
@Component
@Order(4)
public class BookingSeeder implements CommandLineRunner {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private StaffShardRepository staffShardRepository;

    @Override
    public void run(String... args) throws Exception {
        if (bookingRepository.count() > 0) {
            return;
        }

        String receptionistId = staffShardRepository.findAllAcrossShards().stream()
                .filter(staff -> "receptionist.hn@fixinh.vn".equals(staff.getEmail()))
                .map(Staff::getId)
                .findFirst()
                .orElse(null);

        bookingRepository.saveAll(List.of(
                booking("CREATED", "Plumbing", "Vòi nước bị rò rỉ", receptionistId, null, null),
                booking("CONFIRMED", "Electrical", "Ổ cắm điện bị chập", receptionistId, null, null),
                booking("COMPLETED", "Electronics", "Sửa tivi không lên hình", receptionistId, 250000.0, 80000.0)
        ));

        System.out.println("Seeded 3 sample bookings.");
    }

    private Booking booking(String status, String serviceType, String issueDescription, String createdByStaffId,
                             Double serviceFee, Double materialCost) {
        return Booking.builder()
                .customerId(UUID.randomUUID().toString())
                .createdByStaffId(createdByStaffId)
                .serviceType(serviceType)
                .issueDescription(issueDescription)
                .serviceAddress("123 Sample Street, Ward 1")
                .preferredDate(LocalDate.now().plusDays(3))
                .availableStart(LocalTime.of(9, 0))
                .availableEnd(LocalTime.of(11, 0))
                .requiredTechnicianCount(1)
                .status(status)
                .serviceFee(serviceFee)
                .materialCost(materialCost)
                .build();
    }
}
