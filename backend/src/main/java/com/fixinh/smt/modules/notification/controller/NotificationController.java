package com.fixinh.smt.modules.notification.controller;

import com.fixinh.smt.common.web.ApiResponse;
import com.fixinh.smt.modules.notification.dto.external.NotificationExternalDto;
import com.fixinh.smt.modules.notification.service.internal.NotificationInternalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationInternalService notificationInternalService;

    public NotificationController(NotificationInternalService notificationInternalService) {
        this.notificationInternalService = notificationInternalService;
    }

    /** A staff member may only read their own notifications; ADMIN may read anyone's (A.2.a). */
    @GetMapping("/{staffId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #staffId")
    public ApiResponse<List<NotificationExternalDto>> list(@PathVariable String staffId) {
        return ApiResponse.ok(notificationInternalService.listFor(staffId));
    }

    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markRead(@PathVariable String notificationId) {
        notificationInternalService.markRead(notificationId);
        return ApiResponse.ok(null);
    }

    /** "Clear" button — a staff member may only clear their own notifications; ADMIN may clear
     *  anyone's (A.2.a), same rule as reading the list. */
    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #staffId")
    public ApiResponse<Void> clearAll(@PathVariable String staffId) {
        notificationInternalService.clearAll(staffId);
        return ApiResponse.ok(null);
    }
}
