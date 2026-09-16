package com.fixinh.smt.modules.booking.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.booking.dto.internal.*;
import com.fixinh.smt.modules.booking.service.internal.BookingInternalService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** Booking intake/management is a front-desk capability: RECEPTIONIST and ADMIN only (A.2.a). */
@RestController
@RequestMapping("/api/v1/bookings")
@PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
public class BookingController {

    private final BookingInternalService bookingInternalService;

    public BookingController(BookingInternalService bookingInternalService) {
        this.bookingInternalService = bookingInternalService;
    }

    @PostMapping
    public ApiResponse<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ApiResponse.ok(bookingInternalService.create(request));
    }

    @GetMapping("/active")
    public ApiResponse<List<BookingResponse>> listActive() {
        return ApiResponse.ok(bookingInternalService.listActive());
    }

    @GetMapping("/past")
    public ApiResponse<List<BookingResponse>> listPast() {
        return ApiResponse.ok(bookingInternalService.listPast());
    }

    @GetMapping("/search")
    public ApiResponse<List<BookingResponse>> search(@RequestParam String query) {
        return ApiResponse.ok(bookingInternalService.search(query));
    }

    @GetMapping("/filter")
    public ApiResponse<List<BookingResponse>> filter(@RequestParam(required = false) String serviceType,
                                                       @RequestParam(required = false) LocalDate from,
                                                       @RequestParam(required = false) LocalDate to,
                                                       @RequestParam(defaultValue = "desc") String sort) {
        return ApiResponse.ok(bookingInternalService.filter(serviceType, from, to, sort));
    }

    @PatchMapping("/{bookingId}/confirm")
    public ApiResponse<BookingResponse> confirm(@PathVariable String bookingId, @Valid @RequestBody ConfirmBookingRequest request) {
        return ApiResponse.ok(bookingInternalService.confirm(bookingId, request));
    }

    @PatchMapping("/{bookingId}/assign")
    public ApiResponse<BookingResponse> assign(@PathVariable String bookingId, @RequestBody List<String> technicianIds) {
        return ApiResponse.ok(bookingInternalService.assignTechnicians(bookingId, technicianIds));
    }

    @PatchMapping("/{bookingId}/cancel")
    public ApiResponse<BookingResponse> cancel(@PathVariable String bookingId, @Valid @RequestBody CancelBookingRequest request) {
        return ApiResponse.ok(bookingInternalService.cancel(bookingId, request));
    }

    /** Look up a customer by id against SBP before creating a booking on their behalf — lets the
     *  Receptionist confirm they have the right person, not just a well-formed id (4.M1). */
    @GetMapping("/customers/{customerId}")
    public ApiResponse<CustomerLookupResponse> lookupCustomer(@PathVariable String customerId) {
        return ApiResponse.ok(bookingInternalService.lookupCustomer(customerId));
    }
}
