package com.fixinh.smt.common.util;

/**
 * Shared backend validators mirrored on the frontend per A.1.3/3.1.M4: password strength, email
 * syntax, phone number format. Each validator returns a human-readable reason on failure.
 */
public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isValidPassword(String password) {
        return password != null
            && password.length() >= 8
            && password.matches(".*[0-9].*")
            && password.matches(".*[A-Z].*")
            && password.matches(".*[^A-Za-z0-9].*");
    }

    public static boolean isValidEmail(String email) {
        return email != null
            && email.length() < 255
            && email.matches("[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+" + "@" + "[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null
            && phoneNumber.matches("0[0-9]{6,10}");
    }
}
