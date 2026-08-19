package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * DashboardPage — Page Object cho trang /app/orders (dashboard sau login)
 *
 * Locators đã verify từ DOM thực tế (Selenium MCP inspection):
 *   - Sidebar Orders link: a[href*='/app/orders']
 *   - User dropdown:       button[aria-haspopup='menu'] contains admin email
 *   - Logout menuitem:     div[role='menuitem'] text="Log out"
 */
public class DashboardPage extends BasePage {

    private static final Logger log = LogManager.getLogger(DashboardPage.class);

    // ── Locators (verified from DOM) ───────────────────────────────────────────
    private static final By SIDEBAR_ORDERS   = By.cssSelector("a[href*='/app/orders']");
    private static final By SIDEBAR_PRODUCTS = By.cssSelector("a[href*='/app/products']");
    private static final By SIDEBAR_INVENTORY = By.cssSelector("a[href*='/app/inventory']");
    private static final By USER_DROPDOWN_BTN = By.xpath("//button[@aria-haspopup='menu' and contains(normalize-space(.), '@')]");
    private static final By LOGOUT_MENUITEM  = By.xpath("//div[@role='menuitem' and contains(normalize-space(.), 'Log out')]");
    private static final By SEARCH_BUTTON    = By.xpath("//button[contains(., 'Search')]");

    private final String ordersUrl;
    private final String productsUrl;

    public DashboardPage(WebDriver driver) {
        super(driver);
        String baseUrl = ConfigReader.getInstance().getBaseUrl();
        this.ordersUrl = baseUrl + "/app/orders";
        this.productsUrl = baseUrl + "/app/products";
    }

    // ── Verifications ──────────────────────────────────────────────────────────

    public boolean isDisplayed() {
        try {
            wait.until(ExpectedConditions.urlContains("/app/"));
            return isElementPresent(SIDEBAR_ORDERS);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isOnOrdersPage() {
        return getCurrentUrl().contains("/app/orders");
    }

    /**
     * Chờ redirect đến /app/orders hoàn tất (dùng sau login)
     */
    public boolean waitForOrdersPage() {
        try {
            return wait.until(ExpectedConditions.urlContains("/app/orders"));
        } catch (TimeoutException e) {
            log.warn("Timeout chờ redirect đến /app/orders. URL hiện tại: {}", getCurrentUrl());
            return false;
        }
    }

    public boolean isOnProductsPage() {
        return getCurrentUrl().contains("/app/products");
    }

    public boolean isSidebarDisplayed() {
        try {
            // Chờ sidebar Orders link xuất hiện trước (anchor element để verify DOM render xong)
            waitForVisible(SIDEBAR_ORDERS);
            return isDisplayed(SIDEBAR_ORDERS)
                    && isDisplayed(SIDEBAR_PRODUCTS)
                    && isDisplayed(SIDEBAR_INVENTORY);
        } catch (TimeoutException e) {
            log.warn("Timeout chờ sidebar hiển thị. URL hiện tại: {}", getCurrentUrl());
            return false;
        }
    }

    public boolean isUserDropdownVisible() {
        return isDisplayed(USER_DROPDOWN_BTN);
    }

    public String getUserEmailFromDropdown() {
        return getText(USER_DROPDOWN_BTN);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    public DashboardPage navigateToOrders() {
        driver.get(ordersUrl);
        waitForVisible(SIDEBAR_ORDERS);
        return this;
    }

    public DashboardPage navigateToProducts() {
        driver.get(productsUrl);
        return this;
    }

    // ── Actions ────────────────────────────────────────────────────────────────

    public DashboardPage openUserDropdown() {
        click(USER_DROPDOWN_BTN);
        log.info("Opened user dropdown menu");
        return this;
    }

    public LoginPage clickLogout() {
        openUserDropdown();
        click(LOGOUT_MENUITEM);
        log.info("Clicked 'Log out'");
        return new LoginPage(driver);
    }

    // ── Session helpers ────────────────────────────────────────────────────────

    public void deleteAllCookiesAndRefresh() {
        deleteAllCookies();
        driver.navigate().refresh();
        log.info("Cookies deleted and page refreshed");
    }

    public boolean waitForRedirectToLogin() {
        try {
            return wait.until(ExpectedConditions.urlContains("/app/login"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean waitForDashboardLoaded() {
        try {
            return wait.until(ExpectedConditions.urlContains("/app/"));
        } catch (TimeoutException e) {
            return false;
        }
    }
}
