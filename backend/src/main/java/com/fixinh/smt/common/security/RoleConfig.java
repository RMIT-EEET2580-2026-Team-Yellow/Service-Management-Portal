package com.fixinh.smt.common.security;

public enum RoleConfig {
    ADMIN("ADMIN"),
    RECEPTIONIST("RECEPTIONIST"),
    TECHNICIAN("TECHNICIAN"),
    // Not a human Staff role — issued only via POST /api/v1/auth/service-token (shared-secret)
    SERVICE("SERVICE");

    private final String roleName;

    RoleConfig(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
