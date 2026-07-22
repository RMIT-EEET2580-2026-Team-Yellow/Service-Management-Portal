package com.fixinh.smt.common.util;

/**
 * Shared backend validators mirrored on the frontend per A.1.3/3.1.M4: password strength, email
 * syntax, phone number format. Each validator returns a human-readable reason on failure.
 */
public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isValidPassword(String password) {
        // TODO Milestone 2: >=8 chars, 1 digit, 1 special char, 1 uppercase (3.1.M1).
        throw new UnsupportedOperationException("Milestone 2");
    }

    public static boolean isValidEmail(String email) {
        // TODO Milestone 2: single '@', '.' after '@', <255 chars, no forbidden chars (3.1.M2).
        throw new UnsupportedOperationException("Milestone 2");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // TODO Milestone 2: 7-11 digits, starts with 0 (3.1.M3).
        throw new UnsupportedOperationException("Milestone 2");
    }
}
