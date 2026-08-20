package com.medusa.automation.base;

import com.medusa.automation.api.admin.AdminAuthApi;
import com.medusa.automation.api.storefront.StorefrontAuthApi;
import com.medusa.automation.config.ConfigReader;
import io.restassured.RestAssured;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

/**
 * BaseApiTest — lifecycle base class cho tất cả API Test classes.
 *
 * Không khởi động WebDriver — hoàn toàn tách biệt với UI tests.
 *
 * Cung cấp:
 * - RestAssured global config (baseURI, relaxed HTTPS)
 * - customerToken — JWT token customer đang login
 * - adminToken   — JWT token admin đang login
 *
 * Convention: Test classes kế thừa BaseApiTest KHÔNG extends BaseTest.
 */
public abstract class BaseApiTest {

    protected static final Logger log = LogManager.getLogger(BaseApiTest.class);
    protected static final ConfigReader config = ConfigReader.getInstance();

    /** JWT token của customer đã login — dùng cho Storefront auth-required APIs */
    protected static String customerToken;

    /** JWT token của admin đã login — dùng cho Admin APIs */
    protected static String adminToken;

    // ====================================================
    // Suite-level Setup — chạy 1 lần trước toàn bộ suite
    // ====================================================

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        // Config RestAssured global
        RestAssured.baseURI = config.getApiBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Bỏ qua SSL errors trong môi trường test
        RestAssured.useRelaxedHTTPSValidation();

        log.info("=== BaseApiTest Global Setup ===");
        log.info("API Base URL: {}", config.getApiBaseUrl());
        log.info("Publishable Key present: {}", !config.getPublishableKey().isEmpty());
    }

    // ====================================================
    // Class-level Setup — login lấy tokens trước mỗi test class
    // ====================================================

    @BeforeClass(alwaysRun = true)
    public void setupTokens() {
        log.info("--- Setup tokens for: {} ---", this.getClass().getSimpleName());

        // Lấy Customer JWT token từ config credentials
        try {
            StorefrontAuthApi storefrontAuth = new StorefrontAuthApi();
            customerToken = storefrontAuth.login(
                    config.getCustomerEmail(),
                    config.getCustomerPassword()
            );
            log.info("Customer token obtained: {}",
                    customerToken != null ? "✅ OK" : "❌ FAILED");
        } catch (Exception e) {
            log.warn("Không thể lấy customerToken: {} — Storefront auth tests vẫn chạy", e.getMessage());
        }

        // Lấy Admin JWT token từ config credentials
        try {
            AdminAuthApi adminAuth = new AdminAuthApi();
            adminToken = adminAuth.login(
                    config.getAdminEmail(),
                    config.getAdminPassword()
            );
            log.info("Admin token obtained: {}",
                    adminToken != null ? "✅ OK" : "❌ FAILED");
        } catch (Exception e) {
            log.warn("Không thể lấy adminToken: {} — Admin auth tests vẫn chạy", e.getMessage());
        }
    }
}
