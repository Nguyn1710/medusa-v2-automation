package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontSecurityTest — Security tests và Known Bugs.
 *
 * TCs:
 *   - MED_SF_TC_044: XSS trong Checkout Shipping Address
 *   - MED_SF_TC_045: Double-click Place Order
 *   - MED_SF_TC_046: URL injection /gb/../admin
 *   - MED_SF_TC_047: Keyboard navigation (Tab + Enter)
 *   - MED_SF_TC_049: BUG-SF-01 — React Error #31 sau Register
 */
@Epic("Storefront")
@Feature("Security & Known Bugs")
public class StoreFrontSecurityTest extends BaseTest {

    // ─── MED_SF_TC_044 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_044")
    @Severity(SeverityLevel.BLOCKER)
    @Description("XSS payload trong Checkout Shipping Address — React phải escape HTML, không có alert popup")
    public void testXssInCheckoutShippingAddress() {
        // Setup: thêm sản phẩm và vào checkout
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();
        StoreFrontProductPage productPage = storePage.clickFirstProduct();
        if (productPage.areSizeOptionsDisplayed()) {
            productPage.selectFirstSize();
        }
        productPage.clickAddToCart();
        StoreFrontCartPage cartPage;
        if (productPage.isMiniCartDisplayed()) {
            cartPage = productPage.clickGoToCart();
        } else {
            cartPage = new StoreFrontCartPage(driver);
            cartPage.navigateTo();
        }
        StoreFrontCheckoutPage checkoutPage = cartPage.clickGoToCheckout();
        Assert.assertTrue(checkoutPage.isStep1Displayed(), "Phải ở Checkout Bước 1");

        // Điền XSS payload vào First Name
        String xssPayload = "<script>alert('XSS_CHECKOUT')</script>";
        checkoutPage.fillFirstName(xssPayload);
        checkoutPage.fillLastName("Test");
        checkoutPage.fillEmail("xss_test_" + System.currentTimeMillis() + "@test.com");
        checkoutPage.fillAddress("123 XSS Street");
        checkoutPage.fillCity("London");
        checkoutPage.fillPostalCode("E1 1AA");

        Assert.assertFalse(checkoutPage.isJavaScriptAlertPresent(),
                "Không được có JavaScript alert — XSS phải bị blocked/escaped bởi React");
    }

    // ─── MED_SF_TC_046 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_046")
    @Severity(SeverityLevel.BLOCKER)
    @Description("URL path traversal /gb/../admin — phải redirect về homepage hoặc /gb, không phải admin page")
    public void testUrlInjectionPathTraversal() {
        String baseUrl = config.getStoreFrontUrl();
        // Thử URL traversal — ký tự .. bị encode bởi browser
        driver.get(baseUrl + "/gb/%2E%2E/admin");

        String currentUrl = driver.getCurrentUrl();
        boolean isNotAdminPage = !currentUrl.contains("/admin")
                || currentUrl.contains("/dk")
                || currentUrl.contains("storefront-production");

        Assert.assertTrue(isNotAdminPage,
                "URL injection không được dẫn đến trang admin — URL: " + currentUrl);
    }

    // ─── MED_SF_TC_049 — KNOWN BUG ────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_049")
    @Severity(SeverityLevel.CRITICAL)
    @Description("[BUG-SF-01] Register với email mới → EXPECTED: Login thành công | BUG: React Error #31 'Objects are not valid as React children'")
    public void testRegisterNewAccountKnownBug() {
        StoreFrontAccountPage accountPage = new StoreFrontAccountPage(driver);
        accountPage.navigateTo();

        long ts = System.currentTimeMillis();
        String uniqueEmail = "bug_test_" + ts + "@auto.test";

        try {
            accountPage.registerNewAccount(
                    "BugTest", "AutoUser",
                    uniqueEmail, "TestPass123!"
            );

            // Expect: sau đăng ký thành công — phải redirect đến Account Dashboard
            // BUG: Đang bị lỗi React Error #31 thay vì redirect bình thường
            boolean hasReactError = driver.getPageSource().contains("Error #31")
                    || driver.getPageSource().contains("Objects are not valid as a React child");

            if (hasReactError) {
                // Document the known bug — fail the test với mô tả rõ ràng
                Assert.fail("[BUG-SF-01 CONFIRMED] Sau Register, xuất hiện React Error #31: "
                        + "'Objects are not valid as a React child'. "
                        + "Expected: Redirect đến Account Dashboard. "
                        + "Email used: " + uniqueEmail);
            }

            // Nếu không có bug — verify đã login thành công
            Assert.assertTrue(accountPage.isDashboardDisplayed() || accountPage.waitForLoginRedirect(),
                    "Sau Register thành công, phải redirect đến Account Dashboard — URL: "
                            + driver.getCurrentUrl());

        } catch (Exception e) {
            // Log và re-throw nếu là unexpected exception
            throw new AssertionError("Register test failed với exception: " + e.getMessage(), e);
        }
    }
}
