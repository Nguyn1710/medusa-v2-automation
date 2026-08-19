package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * StoreFrontCartPage — Page Object cho trang Giỏ hàng /gb/cart.
 *
 * Locators verified từ DOM thực tế.
 * TC Covered:
 *   - MED_SF_TC_017: Cấu trúc trang /gb/cart khi có sản phẩm
 *   - MED_SF_TC_018: Thay đổi số lượng sản phẩm trong Cart
 *   - MED_SF_TC_019: Xóa sản phẩm khỏi Cart
 *   - MED_PROMO_TC_020: Áp dụng mã khuyến mãi hợp lệ
 *   - MED_PROMO_TC_021: Mã khuyến mãi không hợp lệ
 */
public class StoreFrontCartPage extends BasePage {

    private static final Logger log = LogManager.getLogger(StoreFrontCartPage.class);

    // ── Locators (verified from DEBUG test — actual data-testids) ─────────────

    // Page structure
    private static final By PAGE_HEADING        = By.tagName("h1");
    private static final By CART_CONTAINER      = By.cssSelector("[data-testid='cart-container']");
    private static final By EMPTY_CART_MSG      = By.xpath("//*[contains(text(),'Your shopping bag is empty') or contains(text(),'empty cart') or contains(text(),'Your cart is empty')]");

    // Cart items — actual testid là 'product-row', KHÔNG PHẢI 'cart-item'
    private static final By CART_ITEMS          = By.cssSelector("[data-testid='product-row']");
    private static final By ITEM_TITLE          = By.cssSelector("[data-testid='product-title']");
    private static final By ITEM_VARIANT        = By.cssSelector("[data-testid='product-variant']");
    private static final By ITEM_PRICE          = By.cssSelector("[data-testid='product-unit-price']");
    private static final By ITEM_TOTAL          = By.cssSelector("[data-testid='product-price']");

    // Quantity controls — product-select-button trên cart page
    private static final By QTY_SELECT          = By.cssSelector("[data-testid='product-select-button']");
    private static final By QTY_DECREASE        = By.xpath("//button[@data-testid='decrement-button' or @aria-label='Decrease quantity']");
    private static final By QTY_INCREASE        = By.xpath("//button[@data-testid='increment-button' or @aria-label='Increase quantity']");
    private static final By QTY_INPUT           = By.cssSelector("input[data-testid='quantity-input'], input[type='number']");

    // Delete button
    private static final By DELETE_ITEM_BTN     = By.cssSelector("button[data-testid='cart-item-remove-button'], button[data-testid='product-delete-button'], button[aria-label*='remove' i]");

    // Order summary — verified testids
    private static final By SUBTOTAL            = By.cssSelector("[data-testid='cart-subtotal']");
    private static final By SHIPPING            = By.cssSelector("[data-testid='cart-shipping']");
    private static final By TAXES               = By.cssSelector("[data-testid='cart-taxes']");
    private static final By ORDER_TOTAL         = By.cssSelector("[data-testid='cart-total']");

    // Promo/Discount code
    private static final By PROMO_TOGGLE_BTN    = By.cssSelector("[data-testid='add-discount-button']");
    private static final By PROMO_CODE_INPUT    = By.cssSelector("input[data-testid='discount-code-input'], input[placeholder*='code' i], input[name*='discount' i]");
    private static final By PROMO_APPLY_BTN     = By.cssSelector("button[data-testid='submit-discount-button'], button[type='submit']");
    private static final By PROMO_ERROR_MSG     = By.cssSelector("[data-testid='discount-error'], [class*='error'], p.text-rose");
    private static final By PROMO_SUCCESS       = By.cssSelector("[data-testid='discount-applied'], [class*='discount-applied']");

    // Checkout button
    private static final By GO_TO_CHECKOUT_BTN  = By.cssSelector("a[data-testid='checkout-button'], [data-testid='checkout-button']");

    // Sign in button on cart page
    private static final By SIGN_IN_BTN         = By.cssSelector("[data-testid='sign-in-button']");

    private final String cartUrl;

    public StoreFrontCartPage(WebDriver driver) {
        super(driver);
        ConfigReader config = ConfigReader.getInstance();
        this.cartUrl = config.getStoreFrontUrl() + config.getStoreFrontBasePath() + "/cart";
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public StoreFrontCartPage navigateTo() {
        driver.get(cartUrl);
        // Wait cho cart content render (React async) — chờ URL load xong
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("/cart"));
        } catch (Exception ignored) {}
        log.info("Navigated to Cart page: {}", cartUrl);
        return this;
    }

    // ── Page verifications ────────────────────────────────────────────────────

    public boolean isCartPageDisplayed() {
        return driver.getCurrentUrl().contains("/cart");
    }

    public boolean isCartEmpty() {
        return isDisplayed(EMPTY_CART_MSG);
    }

    public boolean hasCartItems() {
        // Wait cho cart items render (React async sau navigate)
        try {
            wait.until(org.openqa.selenium.support.ui.ExpectedConditions
                .presenceOfElementLocated(CART_ITEMS));
        } catch (TimeoutException e) {
            return false;
        }
        return !driver.findElements(CART_ITEMS).isEmpty();
    }

    public int getCartItemCount() {
        return driver.findElements(CART_ITEMS).size();
    }

    public String getSubtotal() {
        return isDisplayed(SUBTOTAL) ? getText(SUBTOTAL) : "";
    }

    // ── Quantity controls ─────────────────────────────────────────────────────

    public String getQuantityValue() {
        if (isDisplayed(QTY_INPUT)) {
            return getAttribute(QTY_INPUT, "value");
        }
        return "1";
    }

    public StoreFrontCartPage increaseQuantity() {
        click(QTY_INCREASE);
        log.info("Increased item quantity in cart");
        return this;
    }

    public StoreFrontCartPage decreaseQuantity() {
        click(QTY_DECREASE);
        log.info("Decreased item quantity in cart");
        return this;
    }

    public StoreFrontCartPage setQuantityTo(int targetQty) {
        int current = Integer.parseInt(getQuantityValue().replaceAll("\\D+", "1"));
        while (current < targetQty) {
            increaseQuantity();
            current++;
        }
        while (current > targetQty) {
            decreaseQuantity();
            current--;
        }
        return this;
    }

    // ── Delete item ───────────────────────────────────────────────────────────

    public StoreFrontCartPage deleteFirstItem() {
        List<WebElement> deleteBtns = driver.findElements(DELETE_ITEM_BTN);
        if (!deleteBtns.isEmpty()) {
            deleteBtns.get(0).click();
            log.info("Deleted first item from cart");
        }
        return this;
    }

    // ── Promo code ────────────────────────────────────────────────────────────

    public StoreFrontCartPage openPromoCodeInput() {
        if (isDisplayed(PROMO_TOGGLE_BTN)) {
            click(PROMO_TOGGLE_BTN);
        }
        waitForVisible(PROMO_CODE_INPUT);
        log.info("Opened promo code input");
        return this;
    }

    public StoreFrontCartPage enterPromoCode(String code) {
        type(PROMO_CODE_INPUT, code);
        log.info("Entered promo code: {}", code);
        return this;
    }

    public StoreFrontCartPage applyPromoCode(String code) {
        openPromoCodeInput();
        enterPromoCode(code);
        click(PROMO_APPLY_BTN);
        log.info("Applied promo code: {}", code);
        return this;
    }

    public boolean isPromoErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(PROMO_ERROR_MSG)) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getPromoErrorMessage() {
        return isDisplayed(PROMO_ERROR_MSG) ? getText(PROMO_ERROR_MSG) : "";
    }

    public boolean isPromoAppliedSuccessfully() {
        return isDisplayed(PROMO_SUCCESS);
    }

    // ── Go to checkout ────────────────────────────────────────────────────────

    public StoreFrontCheckoutPage clickGoToCheckout() {
        click(GO_TO_CHECKOUT_BTN);
        waitForUrlContains("/checkout");
        log.info("Clicked Go to Checkout");
        return new StoreFrontCheckoutPage(driver);
    }
}
