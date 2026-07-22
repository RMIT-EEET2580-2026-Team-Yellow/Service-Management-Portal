package com.fixinh.smt.common.exception;

import java.time.LocalDateTime;

/**
 * Backend validation-error shape required by SRS 3.1.M4 / 3.5: every error describes the
 * cause and, where relevant, an example of a valid input.
 */
public record ErrorResponse(String error, String cause, String example, LocalDateTime timestamp) {
}
