package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.DashboardPage;
import com.medusa.automation.pages.LoginPage;
import io.qameta.allure.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * SessionManagementTest — Automation test class cho Auth Module: Session + Logout
 *
 * TC coverage:
 *   TC_004(knownBug), TC_031, TC_032, TC_034, TC_037(knownBug), TC_038, TC_049
 */
@Epic("Medusa Admin Authentication")
@Feature("Session Management & Logout")
public class SessionManagementTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // TC_004 — Redirect khi đã có session hợp lệ truy cập /app/login (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-02 — Session Redirect")
    @Description("MED_AUTH_TC_004 [KNOWN BUG]: Đã login, truy cập /app/login → Expected: auto-redirect về /app/orders. App Bug: vẫn ở trang login, không redirect")
    @Severity(SeverityLevel.NORMAL)
    public void tc004_redirectWhenAlreadyLoggedIn_knownBug() {
        // Pre-condition: Đăng nhập trước
        loginAsAdmin();
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/app/"),
                "TC_004 Pre-condition: Phải đang ở trang dashboard sau login");

        // Nhập thủ công URL login
        driver.get(config.getBaseUrl() + "/app/login");

        // Expected: auto-redirect về /app/orders vì đã có session
        // App Bug: vẫn ở trang login
        Assert.assertFalse(driver.getCurrentUrl().contains("/app/login"),
                "TC_004 [KNOWN BUG]: Khi đã login, truy cập /app/login phải auto-redirect về /app/orders (App Bug: vẫn ở trang login)");
        Assert.assertTrue(driver.getCurrentUrl().contains("/app/orders"),
                "TC_004 [KNOWN BUG]: Phải redirect về /app/orders (App Bug: không redirect)");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_031 — Bảo vệ route: truy cập /app/orders khi chưa đăng nhập
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"smoke", "regression"})
    @Story("SC-AUTH-09 — Route Protection")
    @Description("MED_AUTH_TC_031: Truy cập /app/orders không có session → phải redirect về /app/login")
    @Severity(SeverityLevel.BLOCKER)
    public void tc031_routeGuardRedirectsOrdersToLogin() {
        // Không login — truy cập thẳng /app/orders
        driver.get(config.getBaseUrl() + "/app/orders");

        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
        Assert.assertTrue(
                w.until(ExpectedConditions.urlContains("/app/login")),
                "TC_031: Truy cập /app/orders không có session phải redirect về /app/login"
        );
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "TC_031: Login page phải hiển thị đầy đủ sau redirect");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_032 — Bảo vệ route: truy cập /app/products khi chưa đăng nhập
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-09 — Route Protection")
    @Description("MED_AUTH_TC_032: Truy cập /app/products không có session → phải redirect về /app/login")
    @Severity(SeverityLevel.CRITICAL)
    public void tc032_routeGuardRedirectsProductsToLogin() {
        driver.get(config.getBaseUrl() + "/app/products");

        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
        Assert.assertTrue(
                w.until(ExpectedConditions.urlContains("/app/login")),
                "TC_032: Truy cập /app/products không có session phải redirect về /app/login"
        );
        LoginPage loginPage = new LoginPage(driver);
        Assert.assertTrue(loginPage.isLoginPageDisplayed(),
                "TC_032: Login page phải hiển thị đầy đủ sau redirect");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_034 — Session timeout: xóa cookie thủ công → redirect login
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-09 — Session Management")
    @Description("MED_AUTH_TC_034: Đang login, xóa cookie → refresh → phải redirect về /app/login")
    @Severity(SeverityLevel.CRITICAL)
    public void tc034_sessionTimeoutAfterCookieDeletion() {
        // Pre-condition: Đăng nhập trước
        loginAsAdmin();
        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_034 Pre-condition: Phải đang ở /app/orders sau login");

        // Xóa tất cả cookie → simulate session timeout
        dashboard.deleteAllCookiesAndRefresh();

        // Expected: redirect về login vì session bị hủy
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
        Assert.assertTrue(
                w.until(ExpectedConditions.urlContains("/app/login")),
                "TC_034: Sau khi xóa cookie và refresh, phải redirect về /app/login"
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_037 — Happy path: Đăng xuất thành công (KNOWN BUG)
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"knownBug"})
    @Story("SC-AUTH-10 — Logout")
    @Description("MED_AUTH_TC_037 [KNOWN BUG]: Click 'Log out' → Expected: session hủy + redirect /app/login. App Bug: không redirect ngay, phải F5 mới redirect")
    @Severity(SeverityLevel.CRITICAL)
    public void tc037_logoutSuccessfully_knownBug() {
        // Pre-condition: Đăng nhập trước
        loginAsAdmin();

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_037 Pre-condition: Phải đang ở /app/orders sau login");

        // Click Log out
        dashboard.clickLogout();

        // Expected: redirect ngay về /app/login
        // App Bug: không redirect ngay — phải F5 mới redirect
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
        Assert.assertTrue(
                w.until(ExpectedConditions.urlContains("/app/login")),
                "TC_037 [KNOWN BUG]: Sau click 'Log out' phải redirect ngay về /app/login (App Bug: không redirect ngay)"
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_038 — Sau logout: nhấn Back trình duyệt — không vào được dashboard
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-10 — Logout")
    @Description("MED_AUTH_TC_038: Sau logout, nhấn Back → session đã hủy → redirect về /app/login, không vào dashboard")
    @Severity(SeverityLevel.CRITICAL)
    public void tc038_afterLogoutBrowserBackNotAllowed() {
        // Pre-condition: Đăng nhập, rồi xóa cookie để simulate logout state
        loginAsAdmin();
        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_038 Pre-condition: Phải đang ở /app/orders sau login");

        // Xóa tất cả cookie → session bị hủy hoàn toàn
        driver.manage().deleteAllCookies();

        // Refresh trang protected (/app/orders) → route guard phải redirect về /app/login
        driver.navigate().refresh();

        // Verify: trang không được load /app/orders nữa → redirect về /app/login
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(15));
        Assert.assertTrue(
                w.until(ExpectedConditions.urlContains("/app/login")),
                "TC_038: Sau khi xóa session cookie và refresh trang protected, phải redirect về /app/login"
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC_049 — Refresh trang Dashboard sau đăng nhập — session giữ nguyên
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-AUTH-16 — Edge Cases")
    @Description("MED_AUTH_TC_049: Đang ở /app/orders, F5 refresh → session vẫn còn, không redirect login")
    @Severity(SeverityLevel.NORMAL)
    public void tc049_refreshDashboardKeepsSession() {
        // Pre-condition: Đăng nhập trước
        loginAsAdmin();

        DashboardPage dashboard = new DashboardPage(driver);
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_049 Pre-condition: Phải đang ở /app/orders sau login");

        // Refresh trang
        driver.navigate().refresh();

        // Verify vẫn ở dashboard, không bị redirect login
        Assert.assertTrue(dashboard.waitForOrdersPage(),
                "TC_049: Sau F5 refresh, session phải vẫn còn — không bị redirect về /app/login");
        Assert.assertFalse(driver.getCurrentUrl().contains("/app/login"),
                "TC_049: URL KHÔNG được chứa /app/login sau refresh");
    }
}
