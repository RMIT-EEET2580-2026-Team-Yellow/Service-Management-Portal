// Path in repo: backend/src/main/java/com/fixinh/smt/modules/booking/controller/BookingIntegrationController.java
// CHANGE: SBP's RealSmpClient.findBookingsByCustomer expects GET {smp.base-url}/bookings?customerId=
// (see their EPIC3.4 TODO) but no controller exposed BookingExternalService over HTTP yet - only
// BookingController existed, which is staff-facing (ADMIN/RECEPTIONIST) and lives at a different
// path. This adds the SERVICE-role-only counterpart for that one read (4.S1); createBooking/
// findBooking/cancelBooking on SBP's side are still unimplemented pending contract C1-C7, so
// their controllers aren't added here yet either - build them once SBP is ready to call them.
package com.fixinh.smt.modules.booking.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** SBP-facing Booking data API (4.S1) - SERVICE-role only, per the client-credentials exchange
 *  in AuthServiceImpl.issueServiceToken. SBP must reach Booking data only through here, never by
 *  querying SMT's shard directly (4.U1). */
@RestController
@RequestMapping("/api/v1/integration/bookings")
@PreAuthorize("hasRole('SERVICE')")
public class BookingIntegrationController {

    private final BookingExternalService bookingExternalService;

    public BookingIntegrationController(BookingExternalService bookingExternalService) {
        this.bookingExternalService = bookingExternalService;
    }

    @GetMapping
    public ApiResponse<List<BookingExternalDto>> findByCustomerId(@RequestParam String customerId) {
        return ApiResponse.ok(bookingExternalService.findByCustomerId(customerId));
    }
}
