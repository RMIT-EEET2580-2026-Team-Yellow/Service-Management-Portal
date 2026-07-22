package com.fixinh.smt.modules.technician.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianResponse;
import com.fixinh.smt.modules.technician.dto.internal.TechnicianScheduleRequest;
import com.fixinh.smt.modules.technician.service.internal.TechnicianInternalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/technicians")
public class TechnicianController {

    private final TechnicianInternalService technicianInternalService;

    public TechnicianController(TechnicianInternalService technicianInternalService) {
        this.technicianInternalService = technicianInternalService;
    }

    @PostMapping("/schedule")
    public ApiResponse<TechnicianResponse> setSchedule(@Valid @RequestBody TechnicianScheduleRequest request) {
        return ApiResponse.ok(technicianInternalService.setSchedule(request));
    }

    @GetMapping("/{technicianId}/bookings")
    public ApiResponse<List<TechnicianResponse>> myBookings(@PathVariable String technicianId) {
        return ApiResponse.ok(technicianInternalService.myBookings(technicianId));
    }

    @PatchMapping("/{technicianId}/bookings/{bookingId}/start")
    public ApiResponse<TechnicianResponse> startWork(@PathVariable String technicianId, @PathVariable String bookingId) {
        return ApiResponse.ok(technicianInternalService.startWork(technicianId, bookingId));
    }

    @PatchMapping("/{technicianId}/bookings/{bookingId}/complete")
    public ApiResponse<TechnicianResponse> complete(@PathVariable String technicianId, @PathVariable String bookingId,
                                                      @RequestParam double serviceFee, @RequestParam double materialCost,
                                                      @RequestParam String signedBillImageUrl) {
        return ApiResponse.ok(technicianInternalService.complete(technicianId, bookingId, serviceFee, materialCost, signedBillImageUrl));
    }

    @PatchMapping("/{technicianId}/bookings/{bookingId}/abort")
    public ApiResponse<TechnicianResponse> abort(@PathVariable String technicianId, @PathVariable String bookingId,
                                                   @RequestParam String reason) {
        return ApiResponse.ok(technicianInternalService.abort(technicianId, bookingId, reason));
    }
}
