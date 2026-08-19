package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.*;
import com.medusa.automation.utils.TestDataGenerator;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontCheckoutTest — Automation tests cho Checkout flow.
 *
 * TCs:
 *   - MED_SF_TC_022: Cart → Checkout Bước 1 (Shipping Address)
 *   - MED_SF_TC_023: Validation HTML5 Bước 1
 *   - MED_SF_TC_024: Validation Email không hợp lệ Bước 1
 *   - MED_SF_TC_025: Điền Bước 1 → chuyển Bước 2
 *   - MED_SF_TC_026: Danh sách shipping methods Bước 2
 *   - MED_SF_TC_027: Express Shipping — Order Summary cập nhật
 *   - MED_SF_TC_028: Bước 2 → Bước 3 (Payment)
 *   - MED_SF_TC_031: Review screen Bước 4
 *   - MED_SF_TC_032: Happy path — Đặt hàng thành công
 */
@Epic("Storefront")
@Feature("Checkout Flow")
public class StoreFrontCheckoutTest extends BaseTest {

    /**
     * Helper: Thêm sản phẩm và vào Checkout page.
     */
    private StoreFrontCheckoutPage addProductAndGoToCheckout() {
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
        return cartPage.clickGoToCheckout();
    }

    // ─── MED_SF_TC_022 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_022")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Click 'Go to Checkout' từ Cart → Checkout page hiển thị Bước 1 (Shipping Address)")
    public void testCartToCheckoutStep1Navigation() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();

        Assert.assertTrue(checkoutPage.isCheckoutPageDisplayed(),
                "URL phải chứa '/checkout' sau khi click Go to Checkout");
        Assert.assertTrue(checkoutPage.isStep1Displayed(),
                "Checkout Bước 1 (Shipping Address) phải hiển thị với Email và First name inputs");
    }

    // ─── MED_SF_TC_023 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_023")
    @Severity(SeverityLevel.NORMAL)
    @Description("Submit Bước 1 với fields rỗng → HTML5 validation ngăn chặn submit")
    public void testCheckoutStep1EmptyFieldsValidation() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();
        Assert.assertTrue(checkoutPage.isStep1Displayed(), "Phải ở Bước 1 trước khi test");

        String urlBefore = driver.getCurrentUrl();
        checkoutPage.clickContinueToDelivery();

        Assert.assertTrue(checkoutPage.isStep1Displayed(),
                "Sau validation form rỗng, vẫn phải ở Bước 1 (HTML5 validation chặn submit)");
    }

    // ─── MED_SF_TC_024 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_024")
    @Severity(SeverityLevel.NORMAL)
    @Description("Nhập Email không hợp lệ ở Bước 1 → HTML5 email validation báo lỗi")
    public void testCheckoutStep1InvalidEmailValidation() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();
        Assert.assertTrue(checkoutPage.isStep1Displayed(), "Phải ở Bước 1 trước khi test");

        checkoutPage.fillEmail("invalid-email-format");
        checkoutPage.fillFirstName("Test");
        checkoutPage.fillLastName("User");
        checkoutPage.clickContinueToDelivery();

        Assert.assertTrue(checkoutPage.isStep1Displayed(),
                "Email không hợp lệ phải bị chặn — vẫn phải ở Bước 1");
    }

    // ─── MED_SF_TC_025 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_025")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Điền đầy đủ thông tin hợp lệ Bước 1 → Click Continue → chuyển sang Bước 2 (Delivery)")
    public void testCheckoutStep1ToStep2Transition() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();

        long ts = System.currentTimeMillis();
        checkoutPage.fillShippingAddress(
                "auto_checkout_" + ts + "@test.com",
                "Auto", "Test",
                "123 Test Street",
                "London",
                "E1 1AA",
                "gb"
        );
        checkoutPage.clickContinueToDelivery();

        Assert.assertTrue(checkoutPage.isStep2Displayed(),
                "Sau khi điền Bước 1 hợp lệ và click Continue, phải chuyển sang Bước 2 (Delivery)");
    }

    // ─── MED_SF_TC_026 & 027 & 028 ────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_026")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Bước 2 hiển thị danh sách shipping methods — Standard và Express Shipping")
    public void testCheckoutStep2DeliveryMethodsDisplayed() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();

        long ts = System.currentTimeMillis();
        checkoutPage.fillShippingAddress(
                "auto_checkout_" + ts + "@test.com",
                "Auto", "Test", "123 Test St", "London", "E1 1AA", "gb"
        );
        checkoutPage.clickContinueToDelivery();
        Assert.assertTrue(checkoutPage.isStep2Displayed(), "Phải ở Bước 2 trước khi test");

        Assert.assertTrue(checkoutPage.areShippingOptionsDisplayed(),
                "Bước 2 phải hiển thị ít nhất 1 phương thức giao hàng");
    }

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_028")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Chọn Standard Shipping → Click Continue → chuyển sang Bước 3 (Payment)")
    public void testCheckoutStep2ToStep3Transition() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();

        long ts = System.currentTimeMillis();
        checkoutPage.fillShippingAddress(
                "auto_checkout_" + ts + "@test.com",
                "Auto", "Test", "123 Test St", "London", "E1 1AA", "gb"
        );
        checkoutPage.clickContinueToDelivery();

        if (checkoutPage.areShippingOptionsDisplayed()) {
            checkoutPage.selectStandardShipping();
        }
        checkoutPage.clickContinueToPayment();

        Assert.assertTrue(checkoutPage.isStep3Displayed(),
                "Sau khi chọn shipping và click Continue, phải chuyển sang Bước 3 (Payment)");
    }

    // ─── MED_SF_TC_031 & 032 — Full Checkout Happy Path ──────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_032")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Happy path: Điền đầy đủ Checkout 4 bước → Đặt hàng thành công")
    public void testCompleteCheckoutHappyPath() {
        StoreFrontCheckoutPage checkoutPage = addProductAndGoToCheckout();

        long ts = System.currentTimeMillis();
        // Bước 1: Shipping Address
        checkoutPage.fillShippingAddress(
                "auto_order_" + ts + "@test.com",
                "Auto", "Order", "456 Order Street", "London", "E1 2BB", "gb"
        );
        checkoutPage.clickContinueToDelivery();
        Assert.assertTrue(checkoutPage.isStep2Displayed(), "Phải ở Bước 2 sau Step 1");

        // Bước 2: Delivery
        if (checkoutPage.areShippingOptionsDisplayed()) {
            checkoutPage.selectStandardShipping();
        }
        checkoutPage.clickContinueToPayment();
        Assert.assertTrue(checkoutPage.isStep3Displayed(), "Phải ở Bước 3 sau Step 2");

        // Bước 3: Payment
        if (checkoutPage.arePaymentOptionsDisplayed()) {
            checkoutPage.selectManualPayment();
        }
        checkoutPage.clickContinueToReview();
        Assert.assertTrue(checkoutPage.isStep4Displayed(), "Phải ở Bước 4 Review sau Step 3");

        // Bước 4: Place Order
        checkoutPage.clickPlaceOrder();

        Assert.assertTrue(checkoutPage.isOrderConfirmationDisplayed(),
                "Sau Place Order, phải hiển thị trang Xác nhận đơn hàng (Order Confirmation)");
    }
}
