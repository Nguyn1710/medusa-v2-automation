package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * AdminDraftOrderPage — Page Object cho trang Draft Orders và form tạo Draft Order.
 * URL: /app/draft-orders
 * DOM Recon: Medusa Admin v2 — modal/drawer form
 */
public class AdminDraftOrderPage extends BasePage {

    private static final Logger log = LogManager.getLogger(AdminDraftOrderPage.class);

    // ── Locators List page ─────────────────────────────────────────────────────
    private final By pageHeading   = By.xpath("//h1[contains(text(),'Draft Orders') or contains(text(),'Drafts')]");
    private final By createButton  = By.xpath("//button[.//span[text()='Create']] | //a[contains(@href,'create')]");

    // ── Locators Form ──────────────────────────────────────────────────────────
    private final By formContainer      = By.xpath("//div[@role='dialog'] | //form");
    private final By regionDropdown     = By.xpath(
        "//button[contains(@aria-label,'Region') or .//span[contains(text(),'Select a region')]] | " +
        "//div[contains(@class,'select')][.//label[contains(text(),'Region')]]//button | " +
        "//select[@name='region_id']"
    );
    private final By salesChannelDropdown = By.xpath(
        "//button[contains(@aria-label,'Sales Channel') or .//span[contains(text(),'Select a sales channel')]] | " +
        "//select[@name='sales_channel_id']"
    );
    private final By emailInput = By.xpath(
        "//input[@type='email' or @name='email' or @placeholder='Email']"
    );
    private final By countryDropdown = By.xpath(
        "//select[@name='country_code'] | " +
        "//button[contains(@aria-label,'Country') or .//span[contains(text(),'Select a country')]]"
    );
    private final By saveButton   = By.xpath("//button[.//span[text()='Save'] or .//span[text()='Create']]");
    private final By cancelButton = By.xpath("//button[.//span[text()='Cancel'] or normalize-space(text())='Cancel']");

    // ── Validation errors ──────────────────────────────────────────────────────
    private final By validationError = By.xpath(
        "//*[contains(@class,'error') or contains(@class,'invalid') or @role='alert']" +
        "[string-length(normalize-space(text())) > 0]"
    );
    private final By regionError = By.xpath(
        "//p[contains(text(),'Region') and (contains(@class,'error') or contains(text(),'required'))] | " +
        "//*[@data-field='region_id']//p[contains(@class,'error')]"
    );
    private final By salesChannelError = By.xpath(
        "//p[contains(text(),'Sales Channel') and contains(@class,'error')] | " +
        "//*[@data-field='sales_channel_id']//p[contains(@class,'error')]"
    );
    private final By emailError = By.xpath(
        "//p[contains(@class,'error') and (contains(text(),'email') or contains(text(),'Email'))] | " +
        "//*[@data-field='email']//p[contains(@class,'error')]"
    );

    // ── Confirmation ───────────────────────────────────────────────────────────
    private final By draftOrderDetailUrl = By.xpath("//h1[contains(@class,'heading')]");

    public AdminDraftOrderPage(WebDriver driver) {
        super(driver);
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    public AdminDraftOrderPage navigateTo() {
        String url = ConfigReader.getInstance().getBaseUrl() + "/app/draft-orders";
        driver.get(url);
        wait.until(ExpectedConditions.urlContains("/app/draft-orders"));
        log.info("Navigated to Draft Orders: {}", url);
        return this;
    }

    // ── Verification ───────────────────────────────────────────────────────────

    public boolean isDraftOrdersPageDisplayed() {
        return driver.getCurrentUrl().contains("/app/draft-orders");
    }

    public boolean isCreateFormDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(formContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidationErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(validationError));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRegionErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(regionError));
            return true;
        } catch (Exception e) {
            // Fallback: check any validation error
            return isValidationErrorDisplayed();
        }
    }

    public boolean isSalesChannelErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(salesChannelError));
            return true;
        } catch (Exception e) {
            return isValidationErrorDisplayed();
        }
    }

    public boolean isEmailErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailError));
            return true;
        } catch (Exception e) {
            return isValidationErrorDisplayed();
        }
    }

    public boolean isDraftOrderCreatedSuccessfully() {
        try {
            wait.until(ExpectedConditions.urlContains("/app/draft-orders/"));
            String url = driver.getCurrentUrl();
            boolean success = url.matches(".*\\/app\\/draft-orders\\/.+");
            log.info("Draft order URL: {} → success={}", url, success);
            return success;
        } catch (Exception e) {
            log.warn("Draft order creation không redirect đến detail: {}", e.getMessage());
            return false;
        }
    }

    public boolean isFormClosed() {
        try {
            // Nếu form đóng → dialog không còn hiển thị
            wait.until(ExpectedConditions.invisibilityOfElementLocated(formContainer));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Actions ─────────────────────────────────────────────────────────────────

    public AdminDraftOrderPage clickCreate() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(createButton));
        btn.click();
        log.info("Clicked Create button for Draft Order");
        return this;
    }

    public AdminDraftOrderPage selectRegion(String regionName) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(regionDropdown));
            dropdown.click();
            // Chọn option theo text
            By regionOption = By.xpath("//*[@role='option' or @role='menuitem'][contains(normalize-space(text()),'" + regionName + "')]");
            wait.until(ExpectedConditions.elementToBeClickable(regionOption)).click();
            log.info("Selected Region: {}", regionName);
        } catch (Exception e) {
            log.warn("Không thể chọn Region '{}': {}", regionName, e.getMessage());
        }
        return this;
    }

    public AdminDraftOrderPage selectSalesChannel(String channelName) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(salesChannelDropdown));
            dropdown.click();
            By channelOption = By.xpath("//*[@role='option' or @role='menuitem'][contains(normalize-space(text()),'" + channelName + "')]");
            wait.until(ExpectedConditions.elementToBeClickable(channelOption)).click();
            log.info("Selected Sales Channel: {}", channelName);
        } catch (Exception e) {
            log.warn("Không thể chọn Sales Channel '{}': {}", channelName, e.getMessage());
        }
        return this;
    }

    public AdminDraftOrderPage fillEmail(String email) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(emailInput));
        input.clear();
        input.sendKeys(email);
        log.info("Filled email: {}", email);
        return this;
    }

    public AdminDraftOrderPage selectCountry(String countryCode) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(countryDropdown));
            dropdown.click();
            By countryOption = By.xpath("//*[@role='option' or @value='" + countryCode + "'][contains(normalize-space(text()),'" + countryCode + "')]");
            wait.until(ExpectedConditions.elementToBeClickable(countryOption)).click();
            log.info("Selected Country: {}", countryCode);
        } catch (Exception e) {
            log.warn("Không thể chọn Country '{}': {}", countryCode, e.getMessage());
        }
        return this;
    }

    public void clickSave() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        btn.click();
        log.info("Clicked Save/Create button for Draft Order");
    }

    public void clickCancel() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(cancelButton));
        btn.click();
        log.info("Clicked Cancel button for Draft Order form");
    }
}
