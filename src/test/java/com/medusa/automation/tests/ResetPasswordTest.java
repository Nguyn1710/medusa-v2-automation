package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.LoginPage;
import com.medusa.automation.pages.ResetPasswordPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ResetPasswordTest — Automation test class cho Auth Module: Reset Password Page
 *
 * TC coverage:
 *   TC_022, TC_023(knownBug), TC_024, TC_027
 */
@Epic("Medusa Admin Authentication")
@Feature("Reset Password Page")
public class ResetPasswordTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // TC_022 — Truy cập trang Reset Password từ link "Reset" trên Login page
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-07 — Reset Password Navigation")
    @Description("MED_AUTH_TC_022: Click link 'Reset' trên Login page → chuyển đến /app/reset-password với đủ thành phần")
    @Severity(SeverityLevel.NORMAL)
    public void tc022_navigateToResetPasswordPage() {
        LoginPage loginPage = new LoginPage(driver).navigateTo();
        ResetPasswordPage resetPage = loginPage.clickResetLink();

        Assert.assertTrue(resetPage.isDisplayed(),
                "TC_022: Trang Reset Password phải hiển thị sau khi click Reset link");
        Assert.assertTrue(driver.getCurrentUrl().contains("/app/reset-password"),
                "TC_022: URL phải chuyển đến /app/reset-password");
        Assert.assertTrue(resetPage.isEmailInputDisplayed(),
                "TC_022: Email input phải hiển thị trên Reset page");
        Assert.assertTrue(resetPage.isSubmitButtonDisplayed(),
                "TC_022: Nút 'Send reset instructions' phải hiển thị");
        Assert.assertTrue(resetPage.isBackToLoginLinkDisplayed(),
                "TC_022: Link 'Back to login' phải hiển thị");
        Assert.assertEquals(resetPage.getPageHeading(), "Reset password",
                "TC_022: Page heading phải là 'Reset password'");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_023 — Happy path: Gửi reset password với email tồn tại (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-07 — Reset Password Submit")
    @Description("MED_AUTH_TC_023 [KNOWN BUG]: Gửi reset với admin email → Expected: thông báo 'Successfully sent you an email'. App Bug: tính năng chưa có, không gửi email thực tế")
    @Severity(SeverityLevel.NORMAL)
    public void tc023_sendResetWithExistingEmail_knownBug() {
        ResetPasswordPage resetPage = new ResetPasswordPage(driver).navigateTo();
        resetPage.fillEmail(config.getAdminEmail());
        resetPage.clickSendResetInstructions();

        // Expected theo spec: hiển thị success message
        // App Bug: tính năng chưa implement, thực tế không gửi email được
        Assert.assertTrue(resetPage.isSuccessMessageDisplayed(),
                "TC_023 [KNOWN BUG]: Phải hiển thị 'Successfully sent you an email' (App Bug: tính năng chưa hoàn thiện)");
        Assert.assertTrue(resetPage.getSuccessMessageText().contains("Successfully sent"),
                "TC_023 [KNOWN BUG]: Success message phải chứa 'Successfully sent'");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_024 — Gửi reset với email KHÔNG tồn tại — vẫn báo thành công (chống enumeration)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-07 — Reset Password Security")
    @Description("MED_AUTH_TC_024: Gửi reset với email không tồn tại → VẪN hiển thị success (chống email enumeration)")
    @Severity(SeverityLevel.CRITICAL)
    public void tc024_sendResetWithNonExistentEmail() {
        ResetPasswordPage resetPage = new ResetPasswordPage(driver).navigateTo();
        resetPage.fillEmail("notexist_admin_9999@fakeemail.org");
        resetPage.clickSendResetInstructions();

        Assert.assertTrue(resetPage.isSuccessMessageDisplayed(),
                "TC_024: Phải hiển thị success message ngay cả khi email không tồn tại (chống email enumeration)");
        Assert.assertTrue(resetPage.getSuccessMessageText().contains("Successfully sent"),
                "TC_024: Success message phải giống hệt email tồn tại — không tiết lộ email có trong hệ thống không");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_027 — Quay lại trang Login từ link "Back to login"
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-07 — Reset Password Navigation")
    @Description("MED_AUTH_TC_027: Click 'Back to login' từ Reset page → chuyển về /app/login")
    @Severity(SeverityLevel.NORMAL)
    public void tc027_backToLoginFromResetPage() {
        ResetPasswordPage resetPage = new ResetPasswordPage(driver).navigateTo();
        LoginPage loginPage = resetPage.clickBackToLogin();

        Assert.assertTrue(loginPage.isCurrentUrlLoginPage(),
                "TC_027: URL phải chuyển về /app/login sau khi click 'Back to login'");
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "TC_027: Login page phải hiển thị đầy đủ sau khi quay lại");
    }
}
