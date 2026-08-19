package com.medusa.automation.utils;

import java.time.Instant;

/**
 * TestDataGenerator — sinh test data unique và traceable
 *
 * Format: auto_{prefix}_{timestamp}@test.com
 * Traceable: timestamp có thể map ngược về thời điểm chạy test
 */
public class TestDataGenerator {

    private TestDataGenerator() {}

    /**
     * Sinh email unique theo format: auto_{prefix}_{epochSeconds}@test.com
     * Ví dụ: auto_login_1786779891@test.com
     */
    public static String generateEmail(String prefix) {
        long ts = Instant.now().getEpochSecond();
        return "auto_" + prefix.toLowerCase().replace(" ", "_") + "_" + ts + "@test.com";
    }

    /**
     * Sinh username unique
     */
    public static String generateUsername(String prefix) {
        long ts = Instant.now().getEpochSecond();
        return "auto_" + prefix.toLowerCase().replace(" ", "_") + "_" + ts;
    }

    /**
     * Lấy timestamp hiện tại dạng epoch seconds (dùng để trace log)
     */
    public static long currentTimestamp() {
        return Instant.now().getEpochSecond();
    }
}
