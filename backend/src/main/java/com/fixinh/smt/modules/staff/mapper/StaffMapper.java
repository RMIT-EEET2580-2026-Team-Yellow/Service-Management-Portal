package com.fixinh.smt.modules.staff.mapper;

import com.fixinh.smt.modules.staff.dto.external.StaffExternalDto;
import com.fixinh.smt.modules.staff.dto.internal.StaffResponse;
import com.fixinh.smt.modules.staff.entity.Staff;

public final class StaffMapper {

    private StaffMapper() {
    }

    public static StaffResponse toResponse(Staff staff) {
        return new StaffResponse(staff.getId(), staff.getFullName(), staff.getEmail(), staff.getPhoneNumber(),
                staff.getRole(), staff.getProvince(), staff.isActive());
    }

    public static StaffExternalDto toExternalDto(Staff staff) {
        return new StaffExternalDto(staff.getId(), staff.getFullName(), staff.getEmail(), staff.getRole(),
                staff.isActive());
    }
}
