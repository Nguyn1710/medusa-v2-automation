package com.medusa.automation.api.utils;

import java.time.Instant;

/**
 * ApiTestDataGenerator — sinh test data unique, traceable cho API tests.
 *
 * Mỗi giá trị được gắn timestamp (epoch seconds) để:
 * - Tránh conflict data giữa các lần chạy test
 * - Truy ngược về thời điểm chạy test khi debug
 *
 * Format chuẩn: auto_api_{prefix}_{timestamp}@test.com
 */
public final class ApiTestDataGenerator {

    private ApiTestDataGenerator() {}

    // ====================================================
    // Email generators
    // ====================================================

    /**
     * Sinh customer email unique cho Storefront API tests.
     * Format: auto_api_{prefix}_{ts}@test.com
     * Ví dụ: auto_api_register_1786779891@test.com
     */
    public static String generateCustomerEmail(String prefix) {
        long ts = Instant.now().getEpochSecond();
        return "auto_api_" + prefix.toLowerCase().replace(" ", "_") + "_" + ts + "@test.com";
    }

    /**
     * Sinh admin email unique cho Admin API tests.
     */
    public static String generateAdminEmail(String prefix) {
        long ts = Instant.now().getEpochSecond();
        return "auto_admin_" + prefix.toLowerCase().replace(" ", "_") + "_" + ts + "@test.com";
    }

    // ====================================================
    // Password generators
    // ====================================================

    /**
     * Password hợp lệ — đủ mạnh cho Medusa registration.
     * Medusa yêu cầu: min 8 chars, có uppercase, số, ký tự đặc biệt.
     */
    public static String validPassword() {
        return "AutoTest@123!";
    }

    /**
     * Password quá ngắn — dùng cho negative test cases.
     */
    public static String shortPassword() {
        return "abc";
    }

    /**
     * Password chỉ lowercase — không đủ phức tạp.
     */
    public static String weakPassword() {
        return "password123";
    }

    // ====================================================
    // Name generators
    // ====================================================

    /**
     * Sinh first name unique.
     */
    public static String generateFirstName(String prefix) {
        long ts = Instant.now().getEpochSecond() % 100000;
        return "AutoFirst" + prefix + ts;
    }

    /**
     * Sinh last name unique.
     */
    public static String generateLastName(String prefix) {
        long ts = Instant.now().getEpochSecond() % 100000;
        return "AutoLast" + prefix + ts;
    }

    // ====================================================
    // Misc
    // ====================================================

    /**
     * Epoch seconds — dùng để trace log khi debug.
     */
    public static long currentTimestamp() {
        return Instant.now().getEpochSecond();
    }

    /**
     * Tạo identifier để tag test run.
     * Ví dụ: "TC_LOGIN_1786779891"
     */
    public static String traceId(String testName) {
        return testName.toUpperCase().replace(" ", "_") + "_" + currentTimestamp();
    }
}
