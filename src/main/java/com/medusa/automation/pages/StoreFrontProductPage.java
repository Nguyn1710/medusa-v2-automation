package com.medusa.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.Set;

/**
 * StoreFrontProductPage — Page Object cho Product Detail /gb/products/{slug}.
 *
 * Locators verified từ DOM thực tế (MCP browser inspection).
 * TC Covered:
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
public class StoreFrontProductPage extends BasePage {

    private static final Logger log = LogManager.getLogger(StoreFrontProductPage.class);

    // ── Locators (verified from DOM) ──────────────────────────────────────────

    // Product info — h2 với data-testid='product-title' (verified from DOM)
    private static final By PRODUCT_TITLE      = By.cssSelector("[data-testid='product-title']");
    private static final By PRODUCT_PRICE      = By.cssSelector("[data-testid='price'], span[class*='price'], .text-ui-fg-base");
    private static final By PRODUCT_DESCRIPTION = By.cssSelector("[data-testid='product-description'], .product-description, p.text-medium");

    // Gallery
    private static final By MAIN_IMAGE         = By.cssSelector("img[data-testid='product-image'], [data-testid='product-gallery'] img:first-child, .product-gallery img");
    private static final By THUMBNAIL_IMAGES   = By.cssSelector("[data-testid='thumbnail-button'], .thumbnail-button");

    // Breadcrumb
    private static final By BREADCRUMB         = By.cssSelector("nav[aria-label='breadcrumb'], .breadcrumb, [data-testid='breadcrumb']");
    private static final By BREADCRUMB_STORE   = By.cssSelector("nav[aria-label='breadcrumb'] a[href*='/store'], [data-testid='breadcrumb'] a");

    // Variants — Color
    private static final By COLOR_OPTIONS      = By.cssSelector("[data-testid='color-button'], button[title], .option-button");
    private static final By FIRST_COLOR_BTN    = By.cssSelector("[data-testid='color-button']:first-child, .option-button:first-child");

    // Variants — Size — data-testid='option-button' (verified from DOM)
    private static final By SIZE_OPTIONS       = By.cssSelector("[data-testid='option-button']");
    private static final By SIZE_S_BTN         = By.xpath("//button[@data-testid='option-button' and normalize-space(text())='S']");
    private static final By SIZE_M_BTN         = By.xpath("//button[@data-testid='option-button' and normalize-space(text())='M']");
    private static final By SIZE_L_BTN         = By.xpath("//button[@data-testid='option-button' and normalize-space(text())='L']");
    private static final By SIZE_XL_BTN        = By.xpath("//button[@data-testid='option-button' and normalize-space(text())='XL']");

    // Quantity
    private static final By QTY_DECREASE       = By.xpath("//button[@data-testid='decrement-button' or @aria-label='Decrease quantity']");
    private static final By QTY_INCREASE       = By.xpath("//button[@data-testid='increment-button' or @aria-label='Increase quantity']");
    private static final By QTY_INPUT          = By.cssSelector("input[data-testid='quantity-input'], input[type='number']");

    // Add to cart button
    private static final By ADD_TO_CART_BTN    = By.cssSelector("button[data-testid='add-product-button'], button[type='submit'][class*='cart'], button[class*='btn']");

    // Related products
    private static final By RELATED_PRODUCTS   = By.cssSelector("[data-testid='related-products'], .related-products");

    // Mini Cart (appears after add to cart) — data-testids verified from DOM
    private static final By MINI_CART          = By.cssSelector("div[data-testid='cart-dropdown']");
    private static final By MINI_CART_ITEM     = By.cssSelector("[data-testid='cart-item']");
    private static final By MINI_CART_PRODUCT  = By.cssSelector("[data-testid='cart-item-title']");
    private static final By MINI_CART_SUBTOTAL = By.cssSelector("[data-testid='cart-subtotal']");
    // go-to-cart-button là <a> tag (verified from DOM)
    private static final By GO_TO_CART_BTN     = By.cssSelector("a[data-testid='go-to-cart-button']");
    private static final By MINI_CART_CLOSE    = By.cssSelector("[data-testid='close-cart-btn']");

    private final String baseProductUrl;

    public StoreFrontProductPage(WebDriver driver) {
        super(driver);
        com.medusa.automation.config.ConfigReader config = com.medusa.automation.config.ConfigReader.getInstance();
        this.baseProductUrl = config.getStoreFrontUrl() + config.getStoreFrontBasePath() + "/products/";
        // Wait cho Product title sau khi navigate — PRODUCT_TITLE có thể là h2
        try {
            waitForVisible(PRODUCT_TITLE);
        } catch (TimeoutException e) {
            log.warn("Product title không xuất hiện trong 15s — trang có thể bị lỗi");
        }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    /**
     * Navigate trực tiếp đến product theo slug.
     * Ví dụ: navigateToProduct("sweatshirt") → /gb/products/sweatshirt
     */
    public StoreFrontProductPage navigateToProduct(String slug) {
        driver.get(baseProductUrl + slug);
        waitForVisible(PRODUCT_TITLE);
        log.info("Navigated directly to product: {}", slug);
        return this;
    }

    // ── Page verifications ────────────────────────────────────────────────────

    public boolean isProductDetailPageDisplayed() {
        // Product title (h2 data-testid='product-title') luôn hiển thị kể cả khi chưa chọn size
        return isElementPresent(PRODUCT_TITLE);
    }

    public String getProductTitle() {
        return getText(PRODUCT_TITLE);
    }

    public String getProductPrice() {
        return getText(PRODUCT_PRICE);
    }

    public boolean isProductDescriptionDisplayed() {
        return isDisplayed(PRODUCT_DESCRIPTION);
    }

    // ── Gallery ───────────────────────────────────────────────────────────────

    public boolean isMainImageDisplayed() {
        return isDisplayed(MAIN_IMAGE);
    }

    public boolean areThumbnailsDisplayed() {
        return !driver.findElements(THUMBNAIL_IMAGES).isEmpty();
    }

    // ── Breadcrumb ────────────────────────────────────────────────────────────

    public boolean isBreadcrumbDisplayed() {
        return isDisplayed(BREADCRUMB) || isDisplayed(BREADCRUMB_STORE);
    }

    public StoreFrontStorePage clickBreadcrumbStore() {
        click(BREADCRUMB_STORE);
        log.info("Clicked Store link in breadcrumb");
        return new StoreFrontStorePage(driver);
    }

    // ── Variants — Color ──────────────────────────────────────────────────────

    public boolean areColorOptionsDisplayed() {
        return !driver.findElements(COLOR_OPTIONS).isEmpty();
    }

    public int getColorOptionCount() {
        return driver.findElements(COLOR_OPTIONS).size();
    }

    public StoreFrontProductPage selectFirstColor() {
        List<WebElement> colors = driver.findElements(COLOR_OPTIONS);
        if (!colors.isEmpty()) {
            colors.get(0).click();
            log.info("Selected first color option");
        }
        return this;
    }

    public StoreFrontProductPage selectColorByIndex(int index) {
        List<WebElement> colors = driver.findElements(COLOR_OPTIONS);
        if (index < colors.size()) {
            colors.get(index).click();
            log.info("Selected color at index: {}", index);
        }
        return this;
    }

    // ── Variants — Size ───────────────────────────────────────────────────────

    public boolean areSizeOptionsDisplayed() {
        // Wait ngắn cho React hydrate xong trước khi check
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions
                    .presenceOfElementLocated(SIZE_OPTIONS));
            return !driver.findElements(SIZE_OPTIONS).isEmpty();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public StoreFrontProductPage selectSize(String size) {
        By sizeBtn;
        switch (size.toUpperCase()) {
            case "S":  sizeBtn = SIZE_S_BTN;  break;
            case "M":  sizeBtn = SIZE_M_BTN;  break;
            case "L":  sizeBtn = SIZE_L_BTN;  break;
            case "XL": sizeBtn = SIZE_XL_BTN; break;
            default:
                sizeBtn = By.xpath("//button[normalize-space(text())='" + size + "']");
        }
        click(sizeBtn);
        log.info("Selected size: {}", size);
        return this;
    }

    public StoreFrontProductPage selectFirstSize() {
        List<WebElement> sizes = driver.findElements(SIZE_OPTIONS);
        if (!sizes.isEmpty()) {
            WebElement sizeBtn = sizes.get(0);
            // Scroll vào view và click đơn giản — đủ để trigger React state
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', behavior:'instant'});", sizeBtn);
            sizeBtn.click();
            log.info("Selected first available size: {}", sizeBtn.getText().trim());
            // Wait cho Add to Cart button trở thành clickable (React đã xử lý selection)
            try {
                wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .elementToBeClickable(By.cssSelector("button[data-testid='add-product-button']")));
                log.info("Add to Cart button became clickable after size selection");
            } catch (TimeoutException e) {
                log.warn("Add to Cart button vẫn chưa clickable sau khi chọn size");
            }
        }
        return this;
    }

    // ── Quantity ──────────────────────────────────────────────────────────────

    public String getQuantityValue() {
        if (isDisplayed(QTY_INPUT)) {
            return getAttribute(QTY_INPUT, "value");
        }
        return "1";
    }

    public StoreFrontProductPage increaseQuantity() {
        click(QTY_INCREASE);
        log.info("Increased quantity");
        return this;
    }

    public StoreFrontProductPage decreaseQuantity() {
        click(QTY_DECREASE);
        log.info("Decreased quantity");
        return this;
    }

    // ── Add to Cart ───────────────────────────────────────────────────────────

    public boolean isAddToCartButtonDisplayed() {
        return isDisplayed(ADD_TO_CART_BTN);
    }

    public boolean isAddToCartButtonEnabled() {
        try {
            return waitForVisible(ADD_TO_CART_BTN).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    public StoreFrontProductPage clickAddToCart() {
        // Wait cho button clickable sau khi chọn variant
        WebElement btn = wait.until(
            org.openqa.selenium.support.ui.ExpectedConditions
                .elementToBeClickable(ADD_TO_CART_BTN));
        btn.click();
        log.info("Clicked Add to Cart");

        // Smart wait: đợi cookie _medusa_cart_id xuất hiện (chứng tỏ API call thành công)
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(d -> d.manage().getCookies().stream()
                    .anyMatch(c -> c.getName().contains("cart")));
            log.info("Cart cookie confirmed — Add to Cart successful");
        } catch (TimeoutException e) {
            log.warn("Cart cookie chưa xuất hiện sau 10s");
        }

        return this;
    }


    // ── Related products ──────────────────────────────────────────────────────

    public boolean isRelatedProductsSectionDisplayed() {
        return isDisplayed(RELATED_PRODUCTS);
    }

    // ── Mini Cart (after add to cart) ─────────────────────────────────────────

    public boolean isMiniCartDisplayed() {
        try {
            // Cart dropdown xuất hiện sau khi add — dùng presence check vì có thể ẩn nhanh
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .presenceOfElementLocated(MINI_CART));
            return isElementPresent(MINI_CART) || isElementPresent(GO_TO_CART_BTN);
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isMiniCartProductDisplayed() {
        return isDisplayed(MINI_CART_PRODUCT);
    }

    public String getMiniCartSubtotal() {
        return isDisplayed(MINI_CART_SUBTOTAL) ? getText(MINI_CART_SUBTOTAL) : "";
    }

    public StoreFrontCartPage clickGoToCart() {
        click(GO_TO_CART_BTN);
        waitForUrlContains("/cart");
        log.info("Clicked Go to Cart — navigating to cart page");
        return new StoreFrontCartPage(driver);
    }
}
