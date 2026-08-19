package com.medusa.automation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton ConfigReader — đọc test.properties từ config/
 * Không hardcode bất kỳ credentials hay URL nào trong code
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_PATH = "config/test.properties";
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
            log.info("Config loaded from: {}", CONFIG_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc config file: " + CONFIG_PATH, e);
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
            throw new RuntimeException("Property bắt buộc không tìm thấy trong config: " + key);
        }
        return value.trim();
    }

    private String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }
}
