package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * StoreFrontAccountPage — Page Object cho trang Account /gb/account.
 *
 * Locators verified từ DOM thực tế (MCP browser inspection).
 * TC Covered:
 *   - MED_SF_TC_034: Cấu trúc trang Đăng nhập /gb/account
 *   - MED_SF_TC_035: Toggle ẩn/hiện mật khẩu
 *   - MED_SF_TC_036: Validation HTML5 form Đăng nhập rỗng
 *   - MED_SF_TC_037: Chuyển sang form Đăng ký (Join us)
 *   - MED_SF_TC_038: Validation HTML5 form Đăng ký rỗng
 *   - MED_SF_TC_049: BUG-SF-01 — React Error #31 sau Register
 */
public class StoreFrontAccountPage extends BasePage {

    private static final Logger log = LogManager.getLogger(StoreFrontAccountPage.class);

    // ── Login form locators ───────────────────────────────────────────────────

    private static final By LOGIN_HEADING          = By.xpath("//h1 | //h2");
    private static final By EMAIL_INPUT            = By.cssSelector("input[name='email'], input[type='email']");
    private static final By PASSWORD_INPUT         = By.cssSelector("input[name='password'], input[type='password']");
    private static final By EYE_TOGGLE_BTN         = By.xpath("//button[@type='button' and (.//svg or @aria-label='Show password' or @aria-label='Hide password' or contains(@class,'eye'))]");
    private static final By SIGN_IN_BTN            = By.cssSelector("button[type='submit'], button[data-testid='sign-in-button']");
    private static final By JOIN_US_LINK           = By.xpath("//a[contains(text(),'Join us')] | //button[contains(text(),'Join us')] | //span[contains(text(),'Join us')]/..");
    private static final By LOGIN_ERROR_MSG        = By.cssSelector("[data-testid='login-error'], [class*='error'], .text-rose");

    // ── Register form locators ────────────────────────────────────────────────

    private static final By REGISTER_HEADING       = By.xpath("//h1[contains(text(),'MEMBER') or contains(text(),'Register') or contains(text(),'Join')] | //h2[contains(text(),'MEMBER')]");
    private static final By REG_FIRST_NAME         = By.cssSelector("input[name='first_name'], input[data-testid='first-name-input']");
    private static final By REG_LAST_NAME          = By.cssSelector("input[name='last_name'], input[data-testid='last-name-input']");
    private static final By REG_EMAIL              = By.cssSelector("input[name='email'], input[type='email']");
    private static final By REG_PHONE              = By.cssSelector("input[name='phone'], input[type='tel']");
    private static final By REG_PASSWORD           = By.cssSelector("input[name='password'], input[type='password']");
    private static final By JOIN_BTN               = By.cssSelector("button[data-testid='register-button'], button[type='submit']");
    private static final By SIGN_IN_LINK           = By.xpath("//a[contains(text(),'Sign in')] | //button[contains(text(),'Sign in')]");
    private static final By REG_ERROR_MSG          = By.cssSelector("[data-testid='register-error'], [class*='error'], .text-rose");

    // ── Account dashboard locators (after login) ──────────────────────────────

    private static final By DASHBOARD_HEADING      = By.xpath("//h1[contains(text(),'Hello') or contains(text(),'Account') or contains(text(),'Profile')]");
    private static final By LOGOUT_BTN             = By.xpath("//button[contains(text(),'Log out') or contains(text(),'Logout') or contains(text(),'Sign out')]");
    private static final By CUSTOMER_NAME          = By.cssSelector("[data-testid='customer-name'], .customer-name");

    private final String accountUrl;

    public StoreFrontAccountPage(WebDriver driver) {
        super(driver);
        ConfigReader config = ConfigReader.getInstance();
        this.accountUrl = config.getStoreFrontUrl() + config.getStoreFrontBasePath() + "/account";
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public StoreFrontAccountPage navigateTo() {
        driver.get(accountUrl);
        waitForVisible(EMAIL_INPUT);
        log.info("Navigated to Account page: {}", accountUrl);
        return this;
    }

    // ── Login page verifications ──────────────────────────────────────────────

    public boolean isLoginFormDisplayed() {
        return isDisplayed(EMAIL_INPUT)
                && isDisplayed(PASSWORD_INPUT)
                && isDisplayed(SIGN_IN_BTN);
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

    public boolean isSignInButtonDisplayed() {
        return isDisplayed(SIGN_IN_BTN);
    }

    public boolean isJoinUsLinkDisplayed() {
        return isDisplayed(JOIN_US_LINK);
    }

    public String getLoginHeading() {
        return isDisplayed(LOGIN_HEADING) ? getText(LOGIN_HEADING) : "";
    }

    public String getPasswordInputType() {
        return getAttribute(PASSWORD_INPUT, "type");
    }

    // ── Login actions ─────────────────────────────────────────────────────────

    public StoreFrontAccountPage fillEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public StoreFrontAccountPage fillPassword(String password) {
        type(PASSWORD_INPUT, password);
        return this;
    }

    public StoreFrontAccountPage clickSignIn() {
        click(SIGN_IN_BTN);
        return this;
    }

    public StoreFrontAccountPage login(String email, String password) {
        fillEmail(email);
        fillPassword(password);
        clickSignIn();
        log.info("Login attempt with email: {}", email);
        return this;
    }

    public StoreFrontAccountPage clickEyeToggle() {
        click(EYE_TOGGLE_BTN);
        log.info("Toggled password visibility");
        return this;
    }

    public StoreFrontAccountPage clickJoinUs() {
        click(JOIN_US_LINK);
        log.info("Clicked Join us link — switching to Register form");
        return this;
    }

    // ── Login errors ──────────────────────────────────────────────────────────

    public boolean isLoginErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_ERROR_MSG)) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getLoginErrorText() {
        return isDisplayed(LOGIN_ERROR_MSG) ? getText(LOGIN_ERROR_MSG) : "";
    }

    // ── Register form verifications ───────────────────────────────────────────

    public boolean isRegisterFormDisplayed() {
        return isDisplayed(REG_FIRST_NAME)
                && isDisplayed(REG_LAST_NAME)
                && isDisplayed(JOIN_BTN);
    }

    public boolean isRegisterHeadingDisplayed() {
        return isDisplayed(REGISTER_HEADING);
    }

    public String getRegisterHeading() {
        return isDisplayed(REGISTER_HEADING) ? getText(REGISTER_HEADING) : "";
    }

    public boolean isFirstNameInputDisplayed() {
        return isDisplayed(REG_FIRST_NAME);
    }

    public boolean isLastNameInputDisplayed() {
        return isDisplayed(REG_LAST_NAME);
    }

    public boolean isJoinButtonDisplayed() {
        return isDisplayed(JOIN_BTN);
    }

    public boolean isSignInLinkDisplayed() {
        return isDisplayed(SIGN_IN_LINK);
    }

    // ── Register actions ──────────────────────────────────────────────────────

    public StoreFrontAccountPage fillRegisterFirstName(String firstName) {
        type(REG_FIRST_NAME, firstName);
        return this;
    }

    public StoreFrontAccountPage fillRegisterLastName(String lastName) {
        type(REG_LAST_NAME, lastName);
        return this;
    }

    public StoreFrontAccountPage fillRegisterEmail(String email) {
        type(REG_EMAIL, email);
        return this;
    }

    public StoreFrontAccountPage fillRegisterPassword(String password) {
        type(REG_PASSWORD, password);
        return this;
    }

    public StoreFrontAccountPage clickJoinButton() {
        click(JOIN_BTN);
        log.info("Clicked Join button to submit registration");
        return this;
    }

    public StoreFrontAccountPage registerNewAccount(String firstName, String lastName,
                                                     String email, String password) {
        clickJoinUs();
        waitForVisible(REG_FIRST_NAME);
        fillRegisterFirstName(firstName);
        fillRegisterLastName(lastName);
        fillRegisterEmail(email);
        fillRegisterPassword(password);
        clickJoinButton();
        log.info("Submitted registration for email: {}", email);
        return this;
    }

    // ── Dashboard (post-login) ────────────────────────────────────────────────

    public boolean isDashboardDisplayed() {
        return isDisplayed(DASHBOARD_HEADING) || isDisplayed(LOGOUT_BTN);
    }

    public boolean isLogoutButtonDisplayed() {
        return isDisplayed(LOGOUT_BTN);
    }

    public boolean waitForLoginRedirect() {
        try {
            return wait.until(driver -> !driver.getCurrentUrl().contains("/account/login")
                    && (isDisplayed(DASHBOARD_HEADING) || isDisplayed(LOGOUT_BTN)));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ── URL helpers ───────────────────────────────────────────────────────────

    public boolean isOnAccountPage() {
        return driver.getCurrentUrl().contains("/account");
    }
}
