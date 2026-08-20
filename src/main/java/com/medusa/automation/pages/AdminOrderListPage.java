package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * AdminOrderListPage — Page Object cho trang danh sách Orders Admin.
 * URL: /app/orders
 * DOM Recon: Medusa Admin v2 React SPA
 */
public class AdminOrderListPage extends BasePage {

    private static final Logger log = LogManager.getLogger(AdminOrderListPage.class);

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading      = By.xpath("//h1[contains(text(),'Orders')]");
    private final By orderTable       = By.xpath("//table | //div[@role='grid']");
    private final By orderRows        = By.xpath("//tbody/tr | //div[@role='row'][not(@data-header)]");
    private final By prevButton       = By.xpath("//button[contains(@aria-label,'Previous') or normalize-space(text())='Prev' or .//span[text()='Prev']]");
    private final By nextButton       = By.xpath("//button[contains(@aria-label,'Next') or normalize-space(text())='Next' or .//span[text()='Next']]");
    private final By searchInput      = By.xpath("//input[@placeholder='Search' or @placeholder='Search orders' or @type='search']");
    private final By createDraftBtn   = By.xpath(
        "//a[contains(@href,'draft-orders')] | " +
        "//button[.//span[text()='Create'] or .//span[contains(text(),'Draft')]]"
    );

    public AdminOrderListPage(WebDriver driver) {
        super(driver);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    public AdminOrderListPage navigateTo() {
        String url = ConfigReader.getInstance().getBaseUrl() + "/app/orders";
        driver.get(url);
        waitForOrdersPage();
        log.info("Navigated to Admin Orders: {}", url);
        return this;
    }

    // ── Verification ───────────────────────────────────────────────────────────

    public boolean isOrdersPageDisplayed() {
        try {
            wait.until(ExpectedConditions.urlContains("/app/orders"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isOrderTableDisplayed() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(orderTable));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getOrderCount() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(orderRows));
            return driver.findElements(orderRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Kiểm tra nút Prev bị disabled khi đang ở trang 1.
     */
    public boolean isPrevButtonDisabled() {
        try {
            WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(prevButton));
            String disabled = btn.getAttribute("disabled");
            String ariaDisabled = btn.getAttribute("aria-disabled");
            boolean isDisabled = disabled != null || "true".equals(ariaDisabled);
            log.info("Prev button disabled={}, aria-disabled={}", disabled, ariaDisabled);
            return isDisabled;
        } catch (Exception e) {
            log.warn("Không tìm thấy Prev button: {}", e.getMessage());
            return false;
        }
    }

    public boolean isNextButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(nextButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Actions ─────────────────────────────────────────────────────────────────

    public AdminOrderListPage searchOrder(String keyword) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
            input.clear();
            input.sendKeys(keyword);
            log.info("Searched order: {}", keyword);
        } catch (Exception e) {
            log.warn("Search input không tìm thấy: {}", e.getMessage());
        }
        return this;
    }

    private void waitForOrdersPage() {
        try {
            wait.until(ExpectedConditions.urlContains("/app/orders"));
        } catch (Exception e) {
            log.warn("Timeout chờ /app/orders: {}", e.getMessage());
        }
    }
}
