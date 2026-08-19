package com.medusa.automation.base;

import com.medusa.automation.config.ConfigReader;
import com.medusa.automation.drivers.DriverFactory;
import com.medusa.automation.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * BaseTest — lifecycle hooks cho tất cả Test classes.
 * Khởi tạo và teardown WebDriver, chụp screenshot khi fail.
 */
public abstract class BaseTest {

    private static final Logger log = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ConfigReader config;

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        config = ConfigReader.getInstance();
        driver = DriverFactory.createDriver();
        log.info("=== TEST START: {} ===", method.getName());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result, Method method) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED: {}", method.getName());
            String screenshotPath = ScreenshotUtil.takeScreenshot(driver, method.getName());
            if (screenshotPath != null) {
                attachScreenshotToAllure(screenshotPath, method.getName());
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("TEST PASSED: {}", method.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("TEST SKIPPED: {}", method.getName());
        }

        DriverFactory.quitDriver();
        driver = null;
        log.info("=== TEST END: {} ===", method.getName());
    }

    private void attachScreenshotToAllure(String screenshotPath, String testName) {
        try {
            Allure.addAttachment(
                    "Screenshot on Failure - " + testName,
                    "image/png",
                    new FileInputStream(screenshotPath),
                    ".png"
            );
        } catch (IOException e) {
            log.warn("Không thể đính kèm screenshot vào Allure report: {}", e.getMessage());
        }
    }

    /**
     * Helper: login admin từ config (dùng chung cho nhiều test cần pre-condition đã login)
     */
    protected void loginAsAdmin() {
        driver.get(config.getBaseUrl() + "/app/login");
        com.medusa.automation.pages.LoginPage loginPage =
                new com.medusa.automation.pages.LoginPage(driver);
        loginPage.login(config.getAdminEmail(), config.getAdminPassword());

        // Chờ redirect đến /app/ bằng smart wait — KHÔNG dùng Thread.sleep
        org.openqa.selenium.support.ui.WebDriverWait loginWait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(20));
        try {
            loginWait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/app/orders"));
        } catch (org.openqa.selenium.TimeoutException e) {
            throw new RuntimeException(
                "Không thể đăng nhập admin — URL hiện tại: " + driver.getCurrentUrl()
                + " — kiểm tra credentials trong config/test.properties", e);
        }
        log.info("Pre-condition: Admin logged in successfully → {}", driver.getCurrentUrl());
    }
}
