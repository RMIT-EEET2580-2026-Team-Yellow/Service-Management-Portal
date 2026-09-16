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

        // Real customerIds from SBT's fixed seed data not random. BookingSeeder
        bookingRepository.saveAll(List.of(
                booking("CREATED", "Plumbing", "Vòi nước bị rò rỉ", receptionistId, null, null,
                        "c0000000-0000-0000-0000-000000000001"),  // alice.johnson@gmail.com — NORTH
                booking("CONFIRMED", "Electrical", "Ổ cắm điện bị chập", receptionistId, null, null,
                        "c0000000-0000-0000-0000-000000000002"),  // bob.smith@gmail.com — SOUTH
                booking("COMPLETED", "Electronics", "Sửa tivi không lên hình", receptionistId, 250000.0, 80000.0,
                        "c0000000-0000-0000-0000-000000000005")   // emma.davis@gmail.com — NORTH
        ));

        System.out.println("Seeded 3 sample bookings.");
    }

    private Booking booking(String status, String serviceType, String issueDescription, String createdByStaffId,
                             Double serviceFee, Double materialCost, String customerId) {
        return Booking.builder()
                .customerId(customerId)
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
