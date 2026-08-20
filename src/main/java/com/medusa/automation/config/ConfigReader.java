package com.medusa.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton ConfigReader — đọc cấu hình theo thứ tự ưu tiên:
 *   1. Environment Variables  (dùng trên CI/CD)
 *   2. config/test.properties (dùng khi chạy local)
 *
 * Mapping Environment Variables → Property Keys:
 *   BASE_URL               → base.url
 *   STOREFRONT_URL         → storefront.url
 *   STOREFRONT_BASE_PATH   → storefront.base.path
 *   ADMIN_EMAIL            → admin.email
 *   ADMIN_PASSWORD         → admin.password
 *   CUSTOMER_EMAIL         → storefront.customer.email
 *   CUSTOMER_PASSWORD      → storefront.customer.password
 *   API_BASE_URL           → api.base.url
 *   API_PUBLISHABLE_KEY    → api.publishable.key
 *   API_TIMEOUT_MS         → api.timeout.ms
 *   BROWSER                → browser
 *   HEADLESS               → headless
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_PATH = "config/test.properties";
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();

        // Bước 1: Thử load từ file (local dev)
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("Config loaded from file: {}", CONFIG_PATH);
        } catch (IOException e) {
            log.warn("config/test.properties không tìm thấy — sẽ dùng Environment Variables (CI mode)");
        }

        // Bước 2: Override/bổ sung từ Environment Variables (CI takes precedence)
        overrideFromEnv("BASE_URL",             "base.url");
        overrideFromEnv("STOREFRONT_URL",        "storefront.url");
        overrideFromEnv("STOREFRONT_BASE_PATH",  "storefront.base.path");
        overrideFromEnv("ADMIN_EMAIL",           "admin.email");
        overrideFromEnv("ADMIN_PASSWORD",        "admin.password");
        overrideFromEnv("CUSTOMER_EMAIL",        "storefront.customer.email");
        overrideFromEnv("CUSTOMER_PASSWORD",     "storefront.customer.password");
        overrideFromEnv("API_BASE_URL",          "api.base.url");
        overrideFromEnv("API_PUBLISHABLE_KEY",   "api.publishable.key");
        overrideFromEnv("API_TIMEOUT_MS",        "api.timeout.ms");
        overrideFromEnv("BROWSER",               "browser");
        overrideFromEnv("HEADLESS",              "headless");
    }

    /**
     * Nếu environment variable tồn tại và không rỗng → ghi đè giá trị trong properties.
     */
    private void overrideFromEnv(String envVarName, String propertyKey) {
        String envValue = System.getenv(envVarName);
        if (envValue != null && !envValue.trim().isEmpty()) {
            properties.setProperty(propertyKey, envValue.trim());
            log.debug("Config override từ env: {} → {}", envVarName, propertyKey);
        }
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    public String getBaseUrl() {
        return getRequired("base.url");
    }

    public String getAdminEmail() {
        return getRequired("admin.email");
    }

    public String getAdminPassword() {
        return getRequired("admin.password");
    }

    public String getStoreFrontUrl() {
        return getRequired("storefront.url");
    }

    public String getStoreFrontBasePath() {
        return get("storefront.base.path", "/gb");
    }

    public String getCustomerEmail() {
        return getRequired("storefront.customer.email");
    }

    public String getCustomerPassword() {
        return getRequired("storefront.customer.password");
    }

    public String getBrowser() {
        return get("browser", "chrome");
    }

    // ====================================================
    // API Testing Configuration (REST Assured)
    // ====================================================

    /**
     * Base URL cho Medusa Backend API (REST Assured baseURI)
     * Đọc từ key: api.base.url hoặc env var: API_BASE_URL
     */
    public String getApiBaseUrl() {
        return getRequired("api.base.url");
    }

    /**
     * Publishable API Key cho Storefront API
     * Đọc từ key: api.publishable.key hoặc env var: API_PUBLISHABLE_KEY
     * Dùng trong header: x-publishable-api-key
     */
    public String getPublishableKey() {
        return get("api.publishable.key", "");
    }

    /**
     * API request timeout tính bằng milliseconds
     * Đọc từ key: api.timeout.ms (default: 10000ms)
     */
    public int getApiTimeoutMs() {
        return Integer.parseInt(get("api.timeout.ms", "10000"));
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(get("explicit.wait", "15"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(get("page.load.timeout", "30"));
    }

    public boolean isScreenshotOnFail() {
        return Boolean.parseBoolean(get("screenshot.on.fail", "true"));
    }

    public String getScreenshotDir() {
        return get("screenshot.dir", "target/screenshots");
    }

    private String getRequired(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(
                "Property bắt buộc không tìm thấy: '" + key + "'. " +
                "Kiểm tra config/test.properties hoặc set Environment Variable tương ứng."
            );
        }
        return value.trim();
    }

    private String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }
}
