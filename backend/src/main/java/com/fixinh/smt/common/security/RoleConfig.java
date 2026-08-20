package com.fixinh.smt.common.security;

public enum RoleConfig {
    ADMIN("ADMIN"),
    RECEPTIONIST("RECEPTIONIST"),
    TECHNICIAN("TECHNICIAN");

    private final String roleName;

    RoleConfig(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
