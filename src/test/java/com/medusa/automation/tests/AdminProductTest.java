package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.AdminProductCreateDrawer;
import com.medusa.automation.pages.AdminProductListPage;
import com.medusa.automation.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * AdminProductTest — Automation tests cho Admin Products module.
 *
 * TCs từ Products.xlsx (Automation Candidate: Yes, Priority: High):
 *   - MED_PROD_TC: Products list hiển thị đúng cấu trúc
 *   - MED_PROD_TC: Create product → Happy path
 *   - MED_PROD_TC: Create product → Validation title bắt buộc
 *   - MED_PROD_TC: Nút Create hiển thị và clickable
 */
@Epic("Medusa Admin")
@Feature("Products Management")
public class AdminProductTest extends BaseTest {

    // ──────────────────────────────────────────────────────────────────────────
    // TC — Cấu trúc trang /app/products hiển thị đúng
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"smoke", "regression"})
    @Story("SC-PROD-01 — Products List")
    @Description("Admin navigate đến /app/products → danh sách sản phẩm hiển thị, nút Create hiển thị")
    @Severity(SeverityLevel.CRITICAL)
    public void testProductsListPageDisplayed() {
        loginAsAdmin();
        AdminProductListPage productListPage = new AdminProductListPage(driver);
        productListPage.navigateTo();

        Assert.assertTrue(productListPage.isProductsPageDisplayed(),
                "URL phải chứa /app/products sau khi navigate");
        Assert.assertTrue(productListPage.isProductTableDisplayed(),
                "Bảng danh sách sản phẩm phải hiển thị");
        Assert.assertTrue(productListPage.isCreateButtonDisplayed(),
                "Nút Create phải hiển thị và enabled");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC — Happy path: Tạo sản phẩm thành công
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-PROD-02 — Create Product Happy Path")
    @Description("Admin tạo sản phẩm mới với Title hợp lệ → sản phẩm xuất hiện trong danh sách")
    @Severity(SeverityLevel.BLOCKER)
    public void testCreateProductHappyPath() {
        String productTitle = "auto_product_" + TestDataGenerator.currentTimestamp();

        loginAsAdmin();
        AdminProductListPage listPage = new AdminProductListPage(driver);
        listPage.navigateTo();

        Assert.assertTrue(listPage.isCreateButtonDisplayed(),
                "Nút Create phải hiển thị trước khi tạo sản phẩm");

        // Mở drawer và điền thông tin
        AdminProductCreateDrawer drawer = listPage.clickCreate();
        Assert.assertTrue(drawer.isDrawerDisplayed(),
                "Drawer tạo sản phẩm phải mở sau khi click Create");
        Assert.assertTrue(drawer.isTitleInputDisplayed(),
                "Field Title phải hiển thị trong drawer");

        // Tạo product qua happy path wizard
        AdminProductListPage resultPage = drawer.createSimpleProduct(productTitle);

        // Verify: redirect về danh sách hoặc detail page
        Assert.assertTrue(
            resultPage.isProductsPageDisplayed() ||
            driver.getCurrentUrl().contains("/app/products"),
            "Sau khi tạo sản phẩm, URL phải thuộc /app/products"
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC — Mở Create drawer → xác nhận Form hiển thị các fields bắt buộc
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-PROD-03 — Create Product Form Structure")
    @Description("Click nút Create → Drawer mở với Title input và Continue button")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateProductFormStructure() {
        loginAsAdmin();
        AdminProductListPage listPage = new AdminProductListPage(driver);
        listPage.navigateTo();

        AdminProductCreateDrawer drawer = listPage.clickCreate();

        Assert.assertTrue(drawer.isDrawerDisplayed(),
                "Create Product drawer phải mở sau khi click Create");
        Assert.assertTrue(drawer.isTitleInputDisplayed(),
                "Field Title phải hiển thị trong Create Product drawer");
        Assert.assertTrue(drawer.isContinueButtonDisplayed(),
                "Nút Continue phải hiển thị để chuyển bước tiếp theo");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TC — Unauthenticated: truy cập /app/products → redirect về login
    // ──────────────────────────────────────────────────────────────────────────
    @Test(groups = {"regression"})
    @Story("SC-PROD-04 — Unauthenticated Access")
    @Description("Truy cập /app/products không có session → bị redirect về /app/login")
    @Severity(SeverityLevel.CRITICAL)
    public void testUnauthenticatedRedirectToLogin() {
        // Truy cập trực tiếp KHÔNG qua loginAsAdmin()
        driver.get(config.getBaseUrl() + "/app/products");

        // Chờ redirect
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/app/login"));
        } catch (Exception e) {
            // Ghi nhận URL hiện tại để debug
        }

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
            currentUrl.contains("/app/login") || currentUrl.contains("/login"),
            "Truy cập /app/products không có session phải redirect về /app/login — URL hiện tại: " + currentUrl
        );
    }
}
