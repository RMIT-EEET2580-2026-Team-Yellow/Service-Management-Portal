package com.fixinh.smt.common.web;

/**
 * Uniform envelope for every controller response across all modules, so the frontend Service
 * Tier (A.1.3) can rely on one response shape regardless of which module handled the request.
 */
public record ApiResponse<T>(boolean success, T data, String message) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
