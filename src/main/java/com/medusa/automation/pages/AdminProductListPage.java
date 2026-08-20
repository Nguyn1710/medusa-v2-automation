package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * AdminProductListPage — Page Object cho trang danh sách Products.
 * URL: /app/products
 * Inspect DOM: Medusa Admin v2 — React SPA
 */
public class AdminProductListPage extends BasePage {

    private static final Logger log = LogManager.getLogger(AdminProductListPage.class);

    // ── Locators (verified từ DOM Recon) ──────────────────────────────────────
    private final By pageHeading         = By.xpath("//h1[contains(text(),'Products')]");
    private final By createButton        = By.xpath("//button[.//span[text()='Create']]");
    private final By productTable        = By.xpath("//table | //div[@role='grid']");
    private final By productRows         = By.xpath("//tbody/tr | //div[@role='row'][not(@data-header)]");
    private final By searchInput         = By.xpath("//input[@placeholder='Search' or @placeholder='Search products']");

    public AdminProductListPage(WebDriver driver) {
        super(driver);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    public AdminProductListPage navigateTo() {
        String url = ConfigReader.getInstance().getBaseUrl() + "/app/products";
        driver.get(url);
        waitForPageLoad();
        log.info("Navigated to Admin Products: {}", url);
        return this;
    }

    // ── Verification methods ───────────────────────────────────────────────────

    public boolean isProductsPageDisplayed() {
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(pageHeading),
                ExpectedConditions.urlContains("/app/products")
            ));
            return driver.getCurrentUrl().contains("/app/products");
        } catch (Exception e) {
            log.warn("Products page không hiển thị: {}", e.getMessage());
            return false;
        }
    }

    public boolean isProductTableDisplayed() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productTable));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCreateButtonDisplayed() {
        try {
            WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(createButton));
            return btn.isDisplayed() && btn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public int getProductCount() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(productRows));
            return driver.findElements(productRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    // ── Action methods ──────────────────────────────────────────────────────────

    /**
     * Click nút Create để mở drawer tạo sản phẩm.
     */
    public AdminProductCreateDrawer clickCreate() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createButton));
        btn.click();
        log.info("Clicked Create button — opening Create Product drawer");
        return new AdminProductCreateDrawer(driver);
    }

    /**
     * Tìm kiếm sản phẩm theo tên.
     */
    public AdminProductListPage searchProduct(String keyword) {
        try {
            WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
            input.clear();
            input.sendKeys(keyword);
            log.info("Searched for product: {}", keyword);
        } catch (Exception e) {
            log.warn("Search input không tìm thấy: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Kiểm tra sản phẩm có xuất hiện trong danh sách theo title.
     */
    public boolean isProductVisible(String productTitle) {
        try {
            By productLocator = By.xpath(
                "//table//td[contains(normalize-space(text()),'" + productTitle + "')] | " +
                "//div[@role='row']//*[contains(normalize-space(text()),'" + productTitle + "')]"
            );
            wait.until(ExpectedConditions.visibilityOfElementLocated(productLocator));
            return true;
        } catch (Exception e) {
            log.warn("Product '{}' không tìm thấy trong danh sách", productTitle);
            return false;
        }
    }

    private void waitForPageLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
            .until(ExpectedConditions.urlContains("/app/products"));
    }
}
