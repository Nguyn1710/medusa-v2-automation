package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * ResetPasswordPage — Page Object cho trang /app/reset-password
 *
 * Locators đã verify từ DOM thực tế (Selenium MCP inspection):
 *   - Email input:   name="email"
 *   - Submit button: button[type='submit'] text="Send reset instructions"
 *   - Success msg:   span.text-ui-fg-base với text "Successfully sent you an email"
 *   - Back to login: a[href*='login'] text="Back to login"
 */
public class ResetPasswordPage extends BasePage {

    private static final Logger log = LogManager.getLogger(ResetPasswordPage.class);

    // ── Locators (verified from DOM) ──────────────────────────────────────────
    private static final By EMAIL_INPUT      = By.name("email");
    private static final By SUBMIT_BTN       = By.cssSelector("button[type='submit']");
    private static final By SUCCESS_MESSAGE  = By.xpath("//span[contains(text(),'Successfully sent you an email')]");
    private static final By BACK_TO_LOGIN    = By.linkText("Back to login");
    private static final By PAGE_HEADING     = By.tagName("h1");
    private static final By CLOSE_SUCCESS_BTN = By.xpath("//button[contains(@class,'text-ui-fg') and not(@type='submit')]");

    private final String resetUrl;

    public ResetPasswordPage(WebDriver driver) {
        super(driver);
        this.resetUrl = ConfigReader.getInstance().getBaseUrl() + "/app/reset-password";
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public ResetPasswordPage navigateTo() {
        driver.get(resetUrl);
        waitForVisible(EMAIL_INPUT);
        log.info("Navigated to Reset Password page: {}", resetUrl);
        return this;
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isDisplayed() {
        try {
            wait.until(ExpectedConditions.urlContains("/app/reset-password"));
            waitForVisible(EMAIL_INPUT);
            return isElementPresent(EMAIL_INPUT) && isElementPresent(SUBMIT_BTN);
        } catch (TimeoutException e) {
            log.warn("Timeout chờ Reset Password page. URL hiện tại: {}", getCurrentUrl());
            return false;
        }
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING);
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getSuccessMessageText() {
        return getText(SUCCESS_MESSAGE);
    }

    public boolean isEmailFieldEmpty() {
        String value = getAttribute(EMAIL_INPUT, "value");
        return value == null || value.isEmpty();
    }

    public boolean isEmailInputDisplayed() {
        return isDisplayed(EMAIL_INPUT);
    }

    public boolean isSubmitButtonDisplayed() {
        return isDisplayed(SUBMIT_BTN);
    }

    public boolean isBackToLoginLinkDisplayed() {
        return isDisplayed(BACK_TO_LOGIN);
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public ResetPasswordPage fillEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public ResetPasswordPage clickSendResetInstructions() {
        click(SUBMIT_BTN);
        log.info("Clicked 'Send reset instructions'");
        return this;
    }

    public LoginPage clickBackToLogin() {
        click(BACK_TO_LOGIN);
        log.info("Clicked 'Back to login' → navigating to login page");
        return new LoginPage(driver);
    }

    public ResetPasswordPage clickCloseSuccessMessage() {
        click(CLOSE_SUCCESS_BTN);
        return this;
    }
}
