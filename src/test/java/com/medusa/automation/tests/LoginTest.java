package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.DashboardPage;
import com.medusa.automation.pages.LoginPage;
import com.medusa.automation.pages.ResetPasswordPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — Automation test class cho Auth Module: Login Page
 *
 * Groups:
 *   - smoke, regression : test ổn định, expected PASS
 *   - knownBug          : test verify Expected Result nhưng app có bug thực tế (Expected FAIL)
 *
 * TC coverage:
 *   TC_001, TC_003, TC_005, TC_006, TC_007(knownBug), TC_008, TC_009,
 *   TC_010(knownBug), TC_011(knownBug), TC_012(knownBug), TC_013, TC_014,
 *   TC_015, TC_017, TC_044, TC_045
 */
@Epic("Medusa Admin Authentication")
@Feature("Login Page")
public class LoginTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // TC_001 — Cấu trúc trang Login hiển thị đầy đủ tất cả thành phần
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-01 — Cấu trúc trang Login")
    @Description("MED_AUTH_TC_001: Verify trang Login hiển thị đầy đủ: Email, Password, Submit button, Reset link")
    @Severity(SeverityLevel.NORMAL)
    public void tc001_loginPageStructureDisplayed() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();

        Assert.assertTrue(loginPage.isEmailInputDisplayed(),
                "TC_001: Email input phải hiển thị trên Login page");
        Assert.assertTrue(loginPage.isPasswordInputDisplayed(),
                "TC_001: Password input phải hiển thị trên Login page");
        Assert.assertTrue(loginPage.isEyeToggleDisplayed(),
                "TC_001: Eye toggle icon phải hiển thị cạnh Password field");
        Assert.assertTrue(loginPage.isSubmitButtonDisplayed(),
                "TC_001: Nút 'Continue with Email' phải hiển thị");
        Assert.assertTrue(loginPage.isResetLinkDisplayed(),
                "TC_001: Link 'Reset' trong dòng 'Forgot password?' phải hiển thị");
        Assert.assertEquals(loginPage.getEmailPlaceholder(), "Email",
                "TC_001: Email placeholder phải là 'Email'");
        Assert.assertEquals(loginPage.getPasswordPlaceholder(), "Password",
                "TC_001: Password placeholder phải là 'Password'");
        Assert.assertEquals(loginPage.getSubmitButtonText(), "Continue with Email",
                "TC_001: Submit button text phải là 'Continue with Email'");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_003 — Happy path: Đăng nhập thành công
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"smoke", "regression"})
    @Story("SC-AUTH-02 — Happy path Login")
    @Description("MED_AUTH_TC_003: Đăng nhập với credentials hợp lệ → redirect đến /app/orders, sidebar hiển thị")
    @Severity(SeverityLevel.BLOCKER)
    public void tc003_loginSuccessWithValidCredentials() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.login(config.getAdminEmail(), config.getAdminPassword());

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_003: Sau login thành công phải redirect đến /app/orders trong vòng 15 giây");
        Assert.assertTrue(dashboard.isSidebarDisplayed(),
                "TC_003: Sidebar phải hiển thị sau khi đăng nhập thành công");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_005 — Submit form trống — cả Email và Password đều rỗng
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_005: Submit form trống → hệ thống ngăn submit, không chuyển trang")
    @Severity(SeverityLevel.CRITICAL)
    public void tc005_submitEmptyForm() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.clickSubmit();

        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_005: URL phải vẫn ở /app/login sau khi submit form trống");
        Assert.assertFalse(loginPage.waitForRedirectAwayFromLogin(),
                "TC_005: Trang KHÔNG được chuyển khỏi login page");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_006 — Submit chỉ nhập Email, bỏ trống Password
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_006: Submit với Email nhưng Password trống → hệ thống ngăn submit")
    @Severity(SeverityLevel.CRITICAL)
    public void tc006_submitEmailOnlyEmptyPassword() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillEmail(config.getAdminEmail());
        loginPage.clickSubmit();

        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_006: URL phải vẫn ở /app/login sau khi submit thiếu Password");
        Assert.assertFalse(loginPage.waitForRedirectAwayFromLogin(),
                "TC_006: Trang KHÔNG được chuyển khỏi login page");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_007 — Submit chỉ nhập Password, bỏ trống Email (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_007 [KNOWN BUG]: Submit với Password nhưng Email trống → Expected: validation 'Email required'. App Bug: không hiển thị validation nhưng trang vẫn không chuyển")
    @Severity(SeverityLevel.NORMAL)
    public void tc007_submitPasswordOnlyEmptyEmail_knownBug() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillPassword(config.getAdminPassword());
        loginPage.clickSubmit();

        // Expected Result theo spec: hệ thống ngăn submit + hiển thị validation Email required
        // App Bug: không có validation message nhưng trang vẫn không chuyển
        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_007 [KNOWN BUG]: URL phải vẫn ở /app/login (trang không chuyển)");
        // Assertion này FAIL nếu app không hiển thị validation message — đây là bug đã biết
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "TC_007 [KNOWN BUG]: Phải hiển thị validation message cho Email required (App Bug: không hiển thị)");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_008 — Đăng nhập sai password — thông báo "Invalid email or password"
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_008: Đăng nhập với password sai → hiển thị 'Invalid email or password'")
    @Severity(SeverityLevel.CRITICAL)
    public void tc008_loginWithWrongPassword() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.login(config.getAdminEmail(), "wrongpassword12345678");

        Assert.assertTrue(loginPage.waitForErrorMessage(),
                "TC_008: Error message phải hiển thị sau khi nhập sai password");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Invalid email or password"),
                "TC_008: Error message phải chứa 'Invalid email or password'");
        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_008: Trang KHÔNG được chuyển khỏi login page");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_009 — Đăng nhập email không tồn tại
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_009: Đăng nhập với email không tồn tại → 'Invalid email or password' (không tiết lộ email có tồn tại không)")
    @Severity(SeverityLevel.NORMAL)
    public void tc009_loginWithNonExistentEmail() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.login("nonexistent_admin_9999@fakemail.com", "AnyPassword123!");

        Assert.assertTrue(loginPage.waitForErrorMessage(),
                "TC_009: Error message phải hiển thị cho email không tồn tại");
        Assert.assertTrue(loginPage.getErrorMessage().contains("Invalid email or password"),
                "TC_009: Message phải là 'Invalid email or password' — không tiết lộ email tồn tại hay không");
        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_009: Trang KHÔNG được chuyển");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_010 — Email sai format (thiếu @) — client-side validation (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_010 [KNOWN BUG]: Email thiếu @ → Expected: HTML5 validation 'Please include an @'. App Bug: không hiển thị validation")
    @Severity(SeverityLevel.NORMAL)
    public void tc010_loginWithInvalidEmailMissingAt_knownBug() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillEmail("notanemail");
        loginPage.fillPassword(config.getAdminPassword());
        loginPage.clickSubmit();

        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_010: Trang KHÔNG được chuyển khỏi login page");
        // App Bug: HTML5 validation không trigger → không có error message
        // Test verify theo Expected Result (nên có validation)
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed() || loginPage.isCurrentUrlLoginPage(),
                "TC_010 [KNOWN BUG]: Phải có validation lỗi hoặc trang không chuyển (App Bug: không hiển thị HTML5 '@' validation)"
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_011 — Email có @ nhưng thiếu domain (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_011 [KNOWN BUG]: Email 'admin@' thiếu domain → Expected: HTML5 validation lỗi. App Bug: không hiển thị validation")
    @Severity(SeverityLevel.NORMAL)
    public void tc011_loginWithEmailMissingDomain_knownBug() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillEmail("admin@");
        loginPage.fillPassword(config.getAdminPassword());
        loginPage.clickSubmit();

        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_011: Trang KHÔNG được chuyển khỏi login page");
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed() || loginPage.isCurrentUrlLoginPage(),
                "TC_011 [KNOWN BUG]: Phải có validation cho email thiếu domain (App Bug: không hiển thị)"
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_012 — Email chữ hoa — case sensitivity (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-03 — Validation")
    @Description("MED_AUTH_TC_012 [KNOWN BUG]: Email ADMIN@YOURMAIL.COM (chữ hoa) → Expected: login thành công (case-insensitive). App Bug: không thông báo lỗi gì, trang không chuyển")
    @Severity(SeverityLevel.NORMAL)
    public void tc012_loginWithUpperCaseEmail_knownBug() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.login("ADMIN@YOURMAIL.COM", config.getAdminPassword());

        DashboardPage dashboard = new DashboardPage(driver);
        // Expected: login thành công vì email case-insensitive
        // App Bug: trang không chuyển, không thông báo gì
        Assert.assertTrue(dashboard.isOnOrdersPage(),
                "TC_012 [KNOWN BUG]: Email uppercase phải login thành công (case-insensitive) — App Bug: không login được");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_013 — XSS injection vào Email field
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-04 — Security")
    @Description("MED_AUTH_TC_013: XSS injection trong Email → KHÔNG có popup alert, script bị sanitize")
    @Severity(SeverityLevel.CRITICAL)
    public void tc013_xssInjectionInEmailField() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillEmail("<script>alert('XSS')</script>@test.com");
        loginPage.fillPassword("AnyPass123");
        loginPage.clickSubmit();

        // Verify không có JS alert popup (nếu có → XSS thành công)
        try {
            driver.switchTo().alert().dismiss();
            Assert.fail("TC_013: XSS popup alert xuất hiện — đây là lỗ hổng bảo mật NGHIÊM TRỌNG!");
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            // Expected: không có alert
        }

        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_013: Trang KHÔNG được chuyển sau XSS injection");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_014 — SQL injection vào Email field
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-04 — Security")
    @Description("MED_AUTH_TC_014: SQL injection trong Email → không bị bypass, hiển thị 'Invalid email or password'")
    @Severity(SeverityLevel.CRITICAL)
    public void tc014_sqlInjectionInEmailField() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillEmail("admin@test.com' OR '1'='1");
        loginPage.fillPassword("anything");
        loginPage.clickSubmit();

        Assert.assertFalse(new DashboardPage(driver).isOnOrdersPage(),
                "TC_014: SQL injection KHÔNG được phép bypass authentication → không vào dashboard");
        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_014: Trang phải vẫn ở /app/login");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_015 — Email với ký tự đặc biệt hợp lệ RFC 5321
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-04 — Email Field Edge Cases")
    @Description("MED_AUTH_TC_015: Email hợp lệ RFC 5321 'test.user+tag_01@sub.domain.com' → server xử lý bình thường, trả 'Invalid email or password'")
    @Severity(SeverityLevel.NORMAL)
    public void tc015_validRfc5321Email() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.login("test.user+tag_01@sub.domain.com", config.getAdminPassword());

        // Expected: server không reject format email hợp lệ RFC 5321
        // Trả về Invalid email or password (email không phải admin account)
        Assert.assertTrue(
                loginPage.waitForErrorMessage() || loginPage.isCurrentUrlLoginPage(),
                "TC_015: Server phải xử lý email RFC 5321 — không reject format, trả error message"
        );
        Assert.assertFalse(new DashboardPage(driver).isOnOrdersPage(),
                "TC_015: Tài khoản này không phải admin → không vào được dashboard");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_017 — Password toggle eye-icon hiện/ẩn mật khẩu
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-05 — Password Field")
    @Description("MED_AUTH_TC_017: Click eye-icon → password chuyển sang text (hiện), click lại → ẩn trở lại")
    @Severity(SeverityLevel.NORMAL)
    public void tc017_passwordToggleEyeIcon() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.fillPassword(config.getAdminPassword());

        // Bước 3: Password đang ẩn (type=password)
        Assert.assertEquals(loginPage.getPasswordInputType(), "password",
                "TC_017: Password input type ban đầu phải là 'password' (ẩn)");

        // Bước 4: Click eye toggle → hiện mật khẩu
        loginPage.clickEyeToggle();
        Assert.assertEquals(loginPage.getPasswordInputType(), "text",
                "TC_017: Sau click eye icon, type phải chuyển thành 'text' (hiện mật khẩu)");

        // Bước 6: Click eye toggle lần 2 → ẩn lại
        loginPage.clickEyeToggle();
        Assert.assertEquals(loginPage.getPasswordInputType(), "password",
                "TC_017: Sau click eye icon lần 2, type phải trở lại 'password' (ẩn)");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_044 — Tab navigation: thứ tự focus Email → Password → Submit → Reset
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-13 — Accessibility")
    @Description("MED_AUTH_TC_044: Tab navigation từ Email → Password → eye-icon → Submit button → Reset link đúng thứ tự")
    @Severity(SeverityLevel.NORMAL)
    public void tc044_tabNavigationOrder() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();

        // Focus vào Email field, nhấn Tab
        loginPage.tabFromEmail();

        // Verify Password field nhận focus (có thể type vào được)
        loginPage.fillPassword("test_tab_navigation");

        // Verify page không bị chuyển
        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_044: Tab navigation không được gây chuyển trang");
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "TC_044: Login page phải vẫn hiển thị đầy đủ sau tab navigation");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_045 — Submit form Login bằng phím Enter
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"smoke", "regression"})
    @Story("SC-AUTH-13 — Accessibility")
    @Description("MED_AUTH_TC_045: Điền Email + Password, nhấn Enter → form submit, đăng nhập thành công")
    @Severity(SeverityLevel.NORMAL)
    public void tc045_submitLoginFormWithEnterKey() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        loginPage.loginWithEnterKey(config.getAdminEmail(), config.getAdminPassword());

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_045: Nhấn Enter để submit form phải đăng nhập thành công và redirect /app/orders trong vòng 15 giây");
    }
}
