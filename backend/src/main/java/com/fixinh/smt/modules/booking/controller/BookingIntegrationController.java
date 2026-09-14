// Path in repo: backend/src/main/java/com/fixinh/smt/modules/booking/controller/BookingIntegrationController.java
// CHANGE: SBP's RealSmpClient.findBookingsByCustomer expects GET {smp.base-url}/bookings?customerId=
// (see their EPIC3.4 TODO) but no controller exposed BookingExternalService over HTTP yet - only
// BookingController existed, which is staff-facing (ADMIN/RECEPTIONIST) and lives at a different
// path. This adds the SERVICE-role-only counterpart for that one read (4.S1).
// UPDATE (2026-09-14, per SBT feedback): added findById (fetch-one, needed for their "one rating
// per completed booking" rule), plus create/cancel now that SBT's contract is ready — their
// service token could previously only read bookings, not write.
package com.fixinh.smt.modules.booking.controller;

import com.fixinh.smt.common.exception.ResourceNotFoundException;
import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.booking.dto.external.BookingCancelExternalRequest;
import com.fixinh.smt.modules.booking.dto.external.BookingCreateExternalRequest;
import com.fixinh.smt.modules.booking.dto.external.BookingExternalDto;
import com.fixinh.smt.modules.booking.service.external.BookingExternalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /** Fetch-one, per SBT's "one rating per completed booking" rule (needs to check a single
     *  booking's status before allowing a rating). */
    @GetMapping("/{bookingId}")
    public ApiResponse<BookingExternalDto> findById(@PathVariable String bookingId) {
        return bookingExternalService.findById(bookingId)
                .map(ApiResponse::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }

    @PostMapping
    public ApiResponse<BookingExternalDto> create(@RequestBody BookingCreateExternalRequest request) {
        BookingExternalDto created = bookingExternalService.createFromCustomer(
                request.customerId(), request.serviceType(), request.issueDescription(),
                request.serviceAddress(), request.preferredDate(), request.availableStart(), request.availableEnd());
        return ApiResponse.ok(created);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ApiResponse<BookingExternalDto> cancel(@PathVariable String bookingId,
                                                    @RequestBody BookingCancelExternalRequest request) {
        BookingExternalDto cancelled = bookingExternalService.cancelFromCustomer(
                bookingId, request.reason(), request.evidenceImageUrls());
        return ApiResponse.ok(cancelled);
    }
}
