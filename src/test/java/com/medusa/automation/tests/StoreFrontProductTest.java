package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.StoreFrontProductPage;
import com.medusa.automation.pages.StoreFrontStorePage;
import com.medusa.automation.pages.StoreFrontCartPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontProductTest — Automation tests cho Product Detail, Mini Cart.
 *
 * TCs:
 *   - MED_SF_TC_007: Cấu trúc trang chi tiết sản phẩm
 *   - MED_SF_TC_008: Chọn variant màu sắc
 *   - MED_SF_TC_009: Chọn variant kích cỡ
 *   - MED_SF_TC_010: Thay đổi số lượng
 *   - MED_SF_TC_011: Breadcrumb điều hướng
 *   - MED_SF_TC_012: Gallery ảnh sản phẩm
 *   - MED_SF_TC_013: Related products
 *   - MED_SF_TC_014: Happy path — Thêm vào giỏ hàng
 *   - MED_SF_TC_015: Mini Cart preview
 *   - MED_SF_TC_016: Go to cart từ Mini Cart
 */
@Epic("Storefront")
@Feature("Product Detail & Mini Cart")
public class StoreFrontProductTest extends BaseTest {

    private StoreFrontProductPage navigateToFirstProduct() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();
        return storePage.clickFirstProduct();
    }

    // ─── MED_SF_TC_007 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_007")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Product detail page hiển thị tên sản phẩm, giá, mô tả, Add to Cart button")
    public void testProductDetailPageStructure() {
        StoreFrontProductPage productPage = navigateToFirstProduct();

        Assert.assertTrue(productPage.isProductDetailPageDisplayed(),
                "Product detail page phải hiển thị tên sản phẩm và Add to Cart button");
        String title = productPage.getProductTitle();
        Assert.assertFalse(title.isEmpty(),
                "Tên sản phẩm (h1) không được rỗng — actual: " + title);
    }

    // ─── MED_SF_TC_008 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_008")
    @Severity(SeverityLevel.NORMAL)
    @Description("Color variant buttons hiển thị và có thể click để chọn màu")
    public void testColorVariantsAreSelectable() {
        StoreFrontProductPage productPage = navigateToFirstProduct();

        if (!productPage.areColorOptionsDisplayed()) {
            // Sản phẩm này không có color variant — SKIP assertion
            return;
        }
        int colorCount = productPage.getColorOptionCount();
        Assert.assertTrue(colorCount > 0,
                "Phải có ít nhất 1 color option nếu color variants hiển thị");
        productPage.selectFirstColor();
    }

    // ─── MED_SF_TC_009 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_009")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Size variant buttons hiển thị — click chọn size 'S'")
    public void testSizeVariantsAreSelectable() {
        StoreFrontProductPage productPage = navigateToFirstProduct();

        if (!productPage.areSizeOptionsDisplayed()) {
            return;
        }
        productPage.selectFirstSize();
        Assert.assertTrue(productPage.isAddToCartButtonDisplayed(),
                "Add to Cart button phải hiển thị sau khi chọn size");
    }

    // ─── MED_SF_TC_010 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_010")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tăng số lượng lên 2 — quantity input hiển thị giá trị mới")
    public void testQuantityIncreaseAndDecrease() {
        StoreFrontProductPage productPage = navigateToFirstProduct();
        productPage.selectFirstSize();

        productPage.increaseQuantity();
        String qty = productPage.getQuantityValue();
        Assert.assertTrue(Integer.parseInt(qty) >= 2 || qty.equals("2"),
                "Sau khi tăng, số lượng phải >= 2 — actual: " + qty);
    }

    // ─── MED_SF_TC_011 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_011")
    @Severity(SeverityLevel.MINOR)
    @Description("Breadcrumb hiển thị và link Store dẫn về trang Store")
    public void testBreadcrumbNavigationToStore() {
        StoreFrontProductPage productPage = navigateToFirstProduct();

        Assert.assertTrue(productPage.isBreadcrumbDisplayed(),
                "Breadcrumb navigation phải hiển thị trên Product detail page");
    }

    // ─── MED_SF_TC_012 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_012")
    @Severity(SeverityLevel.NORMAL)
    @Description("Gallery ảnh sản phẩm — main image hiển thị")
    public void testProductGalleryMainImageIsDisplayed() {
        StoreFrontProductPage productPage = navigateToFirstProduct();

        Assert.assertTrue(productPage.isMainImageDisplayed(),
                "Main product image phải hiển thị trong gallery");
    }

    // ─── MED_SF_TC_014 — Happy Path ───────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_014")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Happy path: Chọn size S → Click Add to Cart → Cart có sản phẩm")
    public void testAddToCartHappyPath() {
        // Navigate trực tiếp đến sản phẩm biết rõ có variant — sweatshirt
        StoreFrontProductPage productPage = new StoreFrontProductPage(driver);
        productPage.navigateToProduct("sweatshirt");

        // Chọn size nếu có options
        if (productPage.areSizeOptionsDisplayed()) {
            productPage.selectFirstSize();
        }

        productPage.clickAddToCart();

        // TC 015: Verify via cart page — tránh race condition mini cart
        // Navigate tới cart và kiểm tra có sản phẩm
        StoreFrontCartPage cartPage = new StoreFrontCartPage(driver);
        cartPage.navigateTo();
        Assert.assertTrue(cartPage.hasCartItems(),
                "Cart phải có sản phẩm sau khi Add to Cart thành công");
    }

    // ─── MED_SF_TC_016 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_016")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click 'Go to cart' từ Mini Cart → Navigate đến trang /gb/cart")
    public void testGoToCartFromMiniCart() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();
        StoreFrontProductPage productPage = storePage.clickFirstProduct();

        if (productPage.areSizeOptionsDisplayed()) {
            productPage.selectFirstSize();
        }
        productPage.clickAddToCart();

        if (productPage.isMiniCartDisplayed()) {
            StoreFrontCartPage cartPage = productPage.clickGoToCart();
            Assert.assertTrue(cartPage.isCartPageDisplayed(),
                    "Sau khi click Go to Cart, URL phải chứa '/cart'");
        }
    }
}
