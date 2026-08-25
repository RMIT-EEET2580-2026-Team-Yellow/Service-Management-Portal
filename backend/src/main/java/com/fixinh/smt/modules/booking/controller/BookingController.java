package com.fixinh.smt.modules.booking.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.booking.dto.internal.*;
import com.fixinh.smt.modules.booking.service.internal.BookingInternalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
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
}
