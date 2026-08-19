package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * LoginPage — Page Object cho trang /app/login
 *
 * Locators đã verify từ DOM thực tế (Selenium MCP inspection):
 *   - Email input:   name="email"     (id dynamic: :r0:-form-item)
 *   - Password input: name="password" (id dynamic: :r1:-form-item)
 *   - Eye toggle:    button[type=button] với sr-only text "Show password"
 *   - Submit:        button[type=submit]
 *   - Error message: span.txt-small.text-ui-fg-error
 *   - Reset link:    a[href*='reset-password']
 */
public class LoginPage extends BasePage {

    private static final Logger log = LogManager.getLogger(LoginPage.class);

    // ── Locators (verified from DOM) ─────────────────────────────────────────
    private static final By EMAIL_INPUT      = By.name("email");
    private static final By PASSWORD_INPUT   = By.name("password");
    private static final By EYE_TOGGLE_BTN  = By.xpath("//button[@type='button' and .//span[@class='sr-only']]");
    private static final By SUBMIT_BTN      = By.cssSelector("button[type='submit']");
    private static final By ERROR_MESSAGE   = By.cssSelector("span.txt-small.text-ui-fg-error");
    private static final By RESET_LINK      = By.cssSelector("a[href*='reset-password']");
    private static final By PAGE_HEADING    = By.tagName("h1");

    private final String loginUrl;

    public LoginPage(WebDriver driver) {
        super(driver);
        this.loginUrl = ConfigReader.getInstance().getBaseUrl() + "/app/login";
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    public LoginPage navigateTo() {
        driver.get(loginUrl);
        waitForVisible(EMAIL_INPUT);
        log.info("Navigated to Login page: {}", loginUrl);
        return this;
    }

    // ── Verifications ────────────────────────────────────────────────────────

    public boolean isLoginPageDisplayed() {
        return isDisplayed(EMAIL_INPUT)
                && isDisplayed(PASSWORD_INPUT)
                && isDisplayed(SUBMIT_BTN);
    }

    public boolean isHeadingDisplayed() {
        return isDisplayed(PAGE_HEADING);
    }

    public String getPageHeading() {
        return getText(PAGE_HEADING);
    }

    public boolean isEmailInputDisplayed() {
        return isDisplayed(EMAIL_INPUT);
    }

    public boolean isPasswordInputDisplayed() {
        return isDisplayed(PASSWORD_INPUT);
    }

    public boolean isEyeToggleDisplayed() {
        return isDisplayed(EYE_TOGGLE_BTN);
    }

    public boolean isSubmitButtonDisplayed() {
        return isDisplayed(SUBMIT_BTN);
    }

    public boolean isResetLinkDisplayed() {
        return isDisplayed(RESET_LINK);
    }

    public String getSubmitButtonText() {
        return getText(SUBMIT_BTN);
    }

    public String getEmailPlaceholder() {
        return getAttribute(EMAIL_INPUT, "placeholder");
    }

    public String getPasswordPlaceholder() {
        return getAttribute(PASSWORD_INPUT, "placeholder");
    }

    public String getPasswordInputType() {
        return getAttribute(PASSWORD_INPUT, "type");
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    public LoginPage fillEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public LoginPage fillPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    public LoginPage clickSubmit() {
        click(SUBMIT_BTN);
        return this;
    }

    public LoginPage pressEnterOnPassword() {
        pressEnter(PASSWORD_INPUT);
        return this;
    }

    public LoginPage tabFromEmail() {
        pressTab(EMAIL_INPUT);
        return this;
    }

    public LoginPage tabFromPassword() {
        pressTab(PASSWORD_INPUT);
        return this;
    }

    public LoginPage clickEyeToggle() {
        click(EYE_TOGGLE_BTN);
        return this;
    }

    public ResetPasswordPage clickResetLink() {
        click(RESET_LINK);
        log.info("Clicked Reset link → navigating to reset-password page");
        return new ResetPasswordPage(driver);
    }

    /**
     * Convenience method: điền email + password + click submit
     */
    public void login(String email, String password) {
        fillEmail(email);
        fillPassword(password);
        clickSubmit();
        log.info("Login attempt with email: {}", email);
    }

    /**
     * Đăng nhập bằng phím Enter thay vì click button
     */
    public void loginWithEnterKey(String email, String password) {
        fillEmail(email);
        fillPassword(password);
        pressEnterOnPassword();
        log.info("Login via Enter key with email: {}", email);
    }

    // ── Error handling ───────────────────────────────────────────────────────

    public boolean isErrorMessageDisplayed() {
        return isElementPresent(ERROR_MESSAGE) && isDisplayed(ERROR_MESSAGE);
    }

    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean waitForErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ── URL helpers ──────────────────────────────────────────────────────────

    public boolean waitForRedirectAwayFromLogin() {
        try {
            return wait.until(driver -> !driver.getCurrentUrl().contains("/app/login"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isCurrentUrlLoginPage() {
        try {
            return wait.until(ExpectedConditions.urlContains("/app/login"));
        } catch (TimeoutException e) {
            log.warn("Timeout chờ URL login page. URL hiện tại: {}", getCurrentUrl());
            return false;
        }
    }
}
