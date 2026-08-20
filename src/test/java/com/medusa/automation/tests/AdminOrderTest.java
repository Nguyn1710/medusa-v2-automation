package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.AdminDraftOrderPage;
import com.medusa.automation.pages.AdminOrderListPage;
import com.medusa.automation.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AdminOrderTest — Automation tests cho Admin Orders module.
 *
 * TCs từ Orders.xlsx (Automation Candidate: Yes, Priority: High):
 *   MED_ORD_TC_001: Trang /app/orders hiển thị danh sách
 *   MED_ORD_TC_044: Happy path tạo Draft Order
 *   MED_ORD_TC_045: Validation thiếu Region
 *   MED_ORD_TC_046: Validation thiếu Sales Channel
 *   MED_ORD_TC_047: Validation email sai format
 *   MED_ORD_TC_054: Unauthenticated redirect → login
 *   MED_ORD_TC_062: Nút Prev disabled khi ở trang 1
 */
@Epic("Medusa Admin")
@Feature("Orders Management")
public class AdminOrderTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_001 — Danh sách Orders hiển thị
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"smoke", "regression"})
    @Story("SC-ORD-01 — Orders List")
    @Description("MED_ORD_TC_001: Admin navigate /app/orders → danh sách đơn hàng hiển thị")
    @Severity(SeverityLevel.CRITICAL)
    public void testOrdersListPageDisplayed() {
        loginAsAdmin();
        AdminOrderListPage ordersPage = new AdminOrderListPage(driver);
        ordersPage.navigateTo();

        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(),
                "MED_ORD_TC_001: URL phải chứa /app/orders");
        Assert.assertTrue(ordersPage.isOrderTableDisplayed(),
                "MED_ORD_TC_001: Bảng danh sách đơn hàng phải hiển thị");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_062 — Nút Prev disabled khi ở trang 1
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-02 — Pagination")
    @Description("MED_ORD_TC_062: Ở trang 1 danh sách Orders → nút Prev phải bị disabled")
    @Severity(SeverityLevel.NORMAL)
    public void testPrevButtonDisabledOnFirstPage() {
        loginAsAdmin();
        AdminOrderListPage ordersPage = new AdminOrderListPage(driver);
        ordersPage.navigateTo();

        Assert.assertTrue(ordersPage.isOrdersPageDisplayed(),
                "Phải ở trang /app/orders trước khi test pagination");

        Assert.assertTrue(ordersPage.isPrevButtonDisabled(),
                "MED_ORD_TC_062: Nút Prev phải bị disabled khi đang ở trang 1");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_054 — Unauthenticated: truy cập /app/orders → redirect login
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-03 — Security")
    @Description("MED_ORD_TC_054: Truy cập /app/orders không có session → redirect về /app/login")
    @Severity(SeverityLevel.CRITICAL)
    public void testUnauthenticatedOrdersRedirectToLogin() {
        // Truy cập trực tiếp KHÔNG qua loginAsAdmin()
        driver.get(config.getBaseUrl() + "/app/orders");

        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/login"));
        } catch (Exception e) {
            // Log URL để debug
        }

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/app/login") || currentUrl.contains("/login"),
            "MED_ORD_TC_054: Truy cập không có session phải redirect về login — URL: " + currentUrl
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_045 — Validation: Submit thiếu Region
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-04 — Draft Order Validation")
    @Description("MED_ORD_TC_045: Tạo Draft Order bỏ qua Region → hệ thống hiển thị validation error")
    @Severity(SeverityLevel.NORMAL)
    public void testDraftOrderValidationMissingRegion() {
        String testEmail = TestDataGenerator.generateEmail("draft_noregion");

        loginAsAdmin();
        AdminDraftOrderPage draftPage = new AdminDraftOrderPage(driver);
        draftPage.navigateTo();

        Assert.assertTrue(draftPage.isDraftOrdersPageDisplayed(),
                "Phải ở trang /app/draft-orders");

        // Mở form tạo Draft Order
        draftPage.clickCreate();
        Assert.assertTrue(draftPage.isCreateFormDisplayed(),
                "Form tạo Draft Order phải mở");

        // BỎ QUA Region — chỉ điền Sales Channel và Email
        draftPage.selectSalesChannel("Default Sales Channel");
        draftPage.fillEmail(testEmail);
        draftPage.clickSave();

        Assert.assertTrue(draftPage.isRegionErrorDisplayed(),
                "MED_ORD_TC_045: Phải hiển thị validation error khi bỏ qua trường Region bắt buộc");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_046 — Validation: Submit thiếu Sales Channel
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-04 — Draft Order Validation")
    @Description("MED_ORD_TC_046: Tạo Draft Order bỏ qua Sales Channel → validation error")
    @Severity(SeverityLevel.NORMAL)
    public void testDraftOrderValidationMissingSalesChannel() {
        String testEmail = TestDataGenerator.generateEmail("draft_nochannel");

        loginAsAdmin();
        AdminDraftOrderPage draftPage = new AdminDraftOrderPage(driver);
        draftPage.navigateTo();

        draftPage.clickCreate();
        Assert.assertTrue(draftPage.isCreateFormDisplayed(),
                "Form tạo Draft Order phải mở");

        // Chọn Region nhưng BỎ QUA Sales Channel
        draftPage.selectRegion("Europe");
        draftPage.fillEmail(testEmail);
        draftPage.clickSave();

        Assert.assertTrue(draftPage.isSalesChannelErrorDisplayed(),
                "MED_ORD_TC_046: Phải hiển thị validation error khi bỏ qua Sales Channel bắt buộc");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_047 — Validation: Email sai format
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-04 — Draft Order Validation")
    @Description("MED_ORD_TC_047: Nhập email sai format (thiếu @) → validation error email")
    @Severity(SeverityLevel.NORMAL)
    public void testDraftOrderValidationInvalidEmail() {
        loginAsAdmin();
        AdminDraftOrderPage draftPage = new AdminDraftOrderPage(driver);
        draftPage.navigateTo();

        draftPage.clickCreate();
        Assert.assertTrue(draftPage.isCreateFormDisplayed(),
                "Form tạo Draft Order phải mở");

        draftPage.selectRegion("Europe");
        draftPage.selectSalesChannel("Default Sales Channel");
        draftPage.fillEmail("invalidemail.nodomain");  // Email sai format — thiếu @
        draftPage.clickSave();

        Assert.assertTrue(draftPage.isEmailErrorDisplayed(),
                "MED_ORD_TC_047: Phải hiển thị validation error khi email sai format (thiếu @)");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_044 — Happy path: Tạo Draft Order thành công
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-05 — Draft Order Happy Path")
    @Description("MED_ORD_TC_044: Tạo Draft Order đầy đủ thông tin → redirect đến trang chi tiết")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateDraftOrderHappyPath() {
        String testEmail = TestDataGenerator.generateEmail("draft_order");

        loginAsAdmin();
        AdminDraftOrderPage draftPage = new AdminDraftOrderPage(driver);
        draftPage.navigateTo();

        draftPage.clickCreate();
        Assert.assertTrue(draftPage.isCreateFormDisplayed(),
                "Form tạo Draft Order phải mở");

        // Điền đầy đủ thông tin hợp lệ
        draftPage.selectRegion("Europe");
        draftPage.selectSalesChannel("Default Sales Channel");
        draftPage.fillEmail(testEmail);
        draftPage.clickSave();

        Assert.assertTrue(draftPage.isDraftOrderCreatedSuccessfully(),
                "MED_ORD_TC_044: Tạo Draft Order thành công phải redirect đến trang chi tiết /app/draft-orders/{id}");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MED_ORD_TC_052 — Hủy tạo Draft Order bằng nút Cancel
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-ORD-06 — Draft Order Cancel")
    @Description("MED_ORD_TC_052: Click Cancel trong form Draft Order → form đóng, không tạo Draft")
    @Severity(SeverityLevel.NORMAL)
    public void testCancelDraftOrderForm() {
        loginAsAdmin();
        AdminDraftOrderPage draftPage = new AdminDraftOrderPage(driver);
        draftPage.navigateTo();

        draftPage.clickCreate();
        Assert.assertTrue(draftPage.isCreateFormDisplayed(),
                "Form phải mở trước khi test Cancel");

        draftPage.fillEmail("cancel_test@testmail.com");
        draftPage.clickCancel();

        // Form phải đóng và không tạo Draft Order mới
        Assert.assertTrue(
            draftPage.isFormClosed() || draftPage.isDraftOrdersPageDisplayed(),
            "MED_ORD_TC_052: Sau khi Cancel, form phải đóng lại"
        );
    }
}
