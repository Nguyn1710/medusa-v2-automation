package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.StoreFrontAccountPage;
import com.medusa.automation.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontAccountTest — Automation tests cho Account Login & Register.
 *
 * TCs:
 *   - MED_SF_TC_034: Cấu trúc trang Đăng nhập /gb/account
 *   - MED_SF_TC_035: Toggle ẩn/hiện mật khẩu (icon mắt)
 *   - MED_SF_TC_036: Validation HTML5 — submit form rỗng
 *   - MED_SF_TC_037: Chuyển sang form Đăng ký (Join us)
 *   - MED_SF_TC_038: Validation HTML5 — form Đăng ký rỗng
 */
@Epic("Storefront")
@Feature("Account — Login & Register")
public class StoreFrontAccountTest extends BaseTest {

    // ─── MED_SF_TC_034 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_034")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Cấu trúc trang /gb/account — hiển thị đủ Email, Password, Sign in button, Join us link")
    public void testLoginPageStructureIsCorrect() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        Assert.assertTrue(accountPage.isEmailInputDisplayed(),
                "Email input phải hiển thị trong form đăng nhập");
        Assert.assertTrue(accountPage.isPasswordInputDisplayed(),
                "Password input phải hiển thị trong form đăng nhập");
        Assert.assertTrue(accountPage.isSignInButtonDisplayed(),
                "Sign in button phải hiển thị trong form đăng nhập");
        Assert.assertTrue(accountPage.isJoinUsLinkDisplayed(),
                "Link 'Join us' phải hiển thị để chuyển sang Register form");
    }

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_034")
    @Severity(SeverityLevel.NORMAL)
    @Description("Login thành công với credentials hợp lệ — chuyển đến Account Dashboard")
    public void testLoginWithValidCredentials() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        accountPage.login(
                config.getCustomerEmail(),
                config.getCustomerPassword()
        );

        boolean isRedirected = accountPage.waitForLoginRedirect();
        Assert.assertTrue(isRedirected || accountPage.isDashboardDisplayed(),
                "Sau khi login thành công, phải redirect đến Account Dashboard — URL: "
                        + driver.getCurrentUrl());
    }

    // ─── MED_SF_TC_035 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_035")
    @Severity(SeverityLevel.NORMAL)
    @Description("Click icon mắt → password chuyển từ *** sang text; click lần 2 → ẩn lại")
    public void testPasswordVisibilityToggle() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        // Nhập password trước
        accountPage.fillPassword("SecretPass123!");

        // Ban đầu phải là type=password (ẩn)
        String initialType = accountPage.getPasswordInputType();
        Assert.assertEquals(initialType, "password",
                "Password input ban đầu phải có type='password' (ẩn)");

        // Click eye toggle — show password
        Assert.assertTrue(accountPage.isEyeToggleDisplayed(),
                "Eye toggle button phải hiển thị");
        accountPage.clickEyeToggle();
        String visibleType = accountPage.getPasswordInputType();
        Assert.assertEquals(visibleType, "text",
                "Sau lần click đầu, password phải chuyển sang type='text' (hiển thị)");

        // Click eye toggle lần 2 — hide password
        accountPage.clickEyeToggle();
        String hiddenType = accountPage.getPasswordInputType();
        Assert.assertEquals(hiddenType, "password",
                "Sau lần click thứ 2, password phải quay lại type='password' (ẩn)");
    }

    // ─── MED_SF_TC_036 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_036")
    @Severity(SeverityLevel.NORMAL)
    @Description("Submit form Đăng nhập rỗng — HTML5 browser validation ngăn chặn submit")
    public void testLoginFormEmptySubmitShowsHtml5Validation() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        String urlBefore = driver.getCurrentUrl();

        // Click submit không điền dữ liệu — không dùng login() để tránh fill data
        accountPage.clickSignIn();

        String urlAfter = driver.getCurrentUrl();
        Assert.assertEquals(urlAfter, urlBefore,
                "Form rỗng phải bị chặn bởi HTML5 validation — URL không được thay đổi");
        Assert.assertTrue(accountPage.isEmailInputDisplayed(),
                "Email input vẫn phải hiển thị sau validation — form chưa được submit");
    }

    // ─── MED_SF_TC_037 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_037")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click 'Join us' → SPA transition sang Register form (BECOME A MEDUSA STORE MEMBER)")
    public void testJoinUsLinkSwitchesToRegisterForm() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        accountPage.clickJoinUs();

        Assert.assertTrue(accountPage.isFirstNameInputDisplayed(),
                "First name input phải hiển thị sau khi chuyển sang Register form");
        Assert.assertTrue(accountPage.isLastNameInputDisplayed(),
                "Last name input phải hiển thị sau khi chuyển sang Register form");
        Assert.assertTrue(accountPage.isJoinButtonDisplayed(),
                "Join button phải hiển thị trong Register form");
    }

    // ─── MED_SF_TC_038 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_038")
    @Severity(SeverityLevel.NORMAL)
    @Description("Submit form Đăng ký rỗng — HTML5 browser validation chặn tại trường First name")
    public void testRegisterFormEmptySubmitShowsHtml5Validation() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        // Chuyển sang Register form
        accountPage.clickJoinUs();
        Assert.assertTrue(accountPage.isFirstNameInputDisplayed(),
                "Phải ở Register form trước khi test");

        String urlBefore = driver.getCurrentUrl();

        // Click Join không điền data
        accountPage.clickJoinButton();

        String urlAfter = driver.getCurrentUrl();
        Assert.assertEquals(urlAfter, urlBefore,
                "Form rỗng phải bị chặn — URL không đổi (HTML5 native validation)");
        Assert.assertTrue(accountPage.isFirstNameInputDisplayed(),
                "First name input vẫn phải hiển thị — form chưa submit");
    }
}
