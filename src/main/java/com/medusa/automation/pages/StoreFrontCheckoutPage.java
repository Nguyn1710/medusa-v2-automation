package com.medusa.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * StoreFrontCheckoutPage — Page Object cho Checkout flow /gb/checkout.
 *
 * Locators verified từ DOM thực tế (MCP browser inspection).
 * TC Covered:
 *   - MED_SF_TC_022: Cart → Checkout Bước 1 (Shipping Address)
 *   - MED_SF_TC_023: Validation HTML5 bắt buộc Bước 1
 *   - MED_SF_TC_024: Validation Email không hợp lệ Bước 1
 *   - MED_SF_TC_025: Điền Bước 1 → chuyển Bước 2 (Delivery)
 *   - MED_SF_TC_026: Danh sách phương thức giao hàng Bước 2
 *   - MED_SF_TC_027: Chọn Express Shipping — Order Summary cập nhật
 *   - MED_SF_TC_028: Bước 2 → Bước 3 (Payment)
 *   - MED_SF_TC_031: Review screen Bước 4
 *   - MED_SF_TC_032: Happy path — Đặt hàng thành công
 *   - MED_SF_TC_044: XSS injection trong Shipping Address
 *   - MED_SF_TC_045: Double-click Place order
 *   - MED_SF_TC_047: Keyboard navigation
 */
public class StoreFrontCheckoutPage extends BasePage {

    private static final Logger log = LogManager.getLogger(StoreFrontCheckoutPage.class);

    // ── Step 1: Shipping Address ───────────────────────────────────────────────

    private static final By STEP1_HEADING       = By.xpath("//*[contains(text(),'Shipping') or contains(text(),'Address')][@role='heading' or self::h1 or self::h2]");
    private static final By EMAIL_INPUT         = By.cssSelector("input[name='email'], input[type='email'], input[data-testid='email-input']");
    private static final By FIRST_NAME_INPUT    = By.cssSelector("input[name='first_name'], input[data-testid='first-name-input']");
    private static final By LAST_NAME_INPUT     = By.cssSelector("input[name='last_name'], input[data-testid='last-name-input']");
    private static final By ADDRESS_INPUT       = By.cssSelector("input[name='address_1'], input[data-testid='address-input'], input[placeholder*='Address' i]");
    private static final By CITY_INPUT          = By.cssSelector("input[name='city'], input[data-testid='city-input']");
    private static final By POSTAL_INPUT        = By.cssSelector("input[name='postal_code'], input[data-testid='postal-input']");
    private static final By COUNTRY_SELECT      = By.cssSelector("select[name='country_code'], select[data-testid='country-select']");
    private static final By PHONE_INPUT         = By.cssSelector("input[name='phone'], input[type='tel']");
    private static final By CONTINUE_TO_DELIVERY_BTN = By.cssSelector("button[data-testid='submit-address-button'], button[type='submit']");

    // Step 2: Delivery
    private static final By STEP2_HEADING       = By.xpath("//*[contains(text(),'Delivery') or contains(text(),'Shipping method')][@role='heading' or self::h1 or self::h2]");
    private static final By SHIPPING_OPTIONS    = By.cssSelector("[data-testid='delivery-option-radio'], input[type='radio'][name*='shipping']");
    private static final By STANDARD_SHIPPING   = By.xpath("//label[contains(text(),'Standard') or .//span[contains(text(),'Standard')]] | //input[@value='standard']");
    private static final By EXPRESS_SHIPPING    = By.xpath("//label[contains(text(),'Express') or .//span[contains(text(),'Express')]] | //input[@value='express']");
    private static final By CONTINUE_TO_PAYMENT_BTN = By.cssSelector("button[data-testid='submit-delivery-option-button']");

    // Step 3: Payment
    private static final By STEP3_HEADING       = By.xpath("//*[contains(text(),'Payment')][@role='heading' or self::h1 or self::h2]");
    private static final By PAYMENT_OPTIONS     = By.cssSelector("[data-testid='payment-option-radio'], input[type='radio'][name*='payment']");
    private static final By MANUAL_PAYMENT_OPTION = By.xpath("//label[contains(text(),'Manual') or .//span[contains(text(),'Manual')]]");
    private static final By CONTINUE_TO_REVIEW_BTN = By.cssSelector("button[data-testid='submit-payment-button']");

    // Step 4: Review
    private static final By STEP4_HEADING       = By.xpath("//*[contains(text(),'Review') or contains(text(),'Order Review')][@role='heading' or self::h1 or self::h2]");
    private static final By REVIEW_EMAIL        = By.cssSelector("[data-testid='review-email'], .review-email");
    private static final By REVIEW_ADDRESS      = By.cssSelector("[data-testid='review-address'], .review-address");
    private static final By REVIEW_SHIPPING     = By.cssSelector("[data-testid='review-shipping'], .review-shipping");
    private static final By PLACE_ORDER_BTN     = By.cssSelector("button[data-testid='submit-order-button'], button[class*='place-order']");

    // Order Summary (sidebar — visible in all steps)
    private static final By ORDER_SUMMARY_ITEMS = By.cssSelector("[data-testid='cart-summary-item-rows'], .order-summary-items");
    private static final By ORDER_SUBTOTAL      = By.cssSelector("[data-testid='cart-subtotal'], .order-subtotal");
    private static final By ORDER_SHIPPING_FEE  = By.cssSelector("[data-testid='shipping-price'], .shipping-price");
    private static final By ORDER_TOTAL         = By.cssSelector("[data-testid='cart-total'], .order-total");

    // Order Confirmation
    private static final By CONFIRMATION_HEADING = By.xpath("//*[contains(text(),'Thank you') or contains(text(),'Order confirmed') or contains(text(),'Order placed')]");
    private static final By ORDER_NUMBER         = By.cssSelector("[data-testid='order-id'], .order-id, [class*='order-number']");

    public StoreFrontCheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public boolean isCheckoutPageDisplayed() {
        return driver.getCurrentUrl().contains("/checkout");
    }

    public boolean isStep1Displayed() {
        return isDisplayed(EMAIL_INPUT) || isDisplayed(FIRST_NAME_INPUT);
    }

    public boolean isStep2Displayed() {
        return !driver.findElements(SHIPPING_OPTIONS).isEmpty()
                || isDisplayed(CONTINUE_TO_PAYMENT_BTN);
    }

    public boolean isStep3Displayed() {
        return !driver.findElements(PAYMENT_OPTIONS).isEmpty()
                || isDisplayed(CONTINUE_TO_REVIEW_BTN);
    }

    public boolean isStep4Displayed() {
        return isDisplayed(PLACE_ORDER_BTN);
    }

    // ── Step 1: Fill Shipping Address ─────────────────────────────────────────

    public StoreFrontCheckoutPage fillEmail(String email) {
        type(EMAIL_INPUT, email);
        return this;
    }

    public StoreFrontCheckoutPage fillFirstName(String firstName) {
        type(FIRST_NAME_INPUT, firstName);
        return this;
    }

    public StoreFrontCheckoutPage fillLastName(String lastName) {
        type(LAST_NAME_INPUT, lastName);
        return this;
    }

    public StoreFrontCheckoutPage fillAddress(String address) {
        type(ADDRESS_INPUT, address);
        return this;
    }

    public StoreFrontCheckoutPage fillCity(String city) {
        type(CITY_INPUT, city);
        return this;
    }

    public StoreFrontCheckoutPage fillPostalCode(String postal) {
        type(POSTAL_INPUT, postal);
        return this;
    }

    public StoreFrontCheckoutPage selectCountry(String countryCode) {
        if (isDisplayed(COUNTRY_SELECT)) {
            try {
                new Select(driver.findElement(COUNTRY_SELECT)).selectByValue(countryCode.toLowerCase());
            } catch (Exception e) {
                new Select(driver.findElement(COUNTRY_SELECT)).selectByVisibleText("United Kingdom");
            }
            log.info("Selected country: {}", countryCode);
        }
        return this;
    }

    public StoreFrontCheckoutPage fillShippingAddress(String email, String firstName, String lastName,
                                                       String address, String city, String postal,
                                                       String countryCode) {
        fillEmail(email);
        fillFirstName(firstName);
        fillLastName(lastName);
        fillAddress(address);
        fillCity(city);
        fillPostalCode(postal);
        selectCountry(countryCode);
        log.info("Filled shipping address form");
        return this;
    }

    public StoreFrontCheckoutPage clickContinueToDelivery() {
        click(CONTINUE_TO_DELIVERY_BTN);
        log.info("Clicked Continue to Delivery");
        return this;
    }

    // ── Step 1 validation ─────────────────────────────────────────────────────

    public boolean isEmailInputDisplayed() {
        return isDisplayed(EMAIL_INPUT);
    }

    public boolean isFirstNameInputDisplayed() {
        return isDisplayed(FIRST_NAME_INPUT);
    }

    public boolean isLastNameInputDisplayed() {
        return isDisplayed(LAST_NAME_INPUT);
    }

    // ── Step 2: Delivery ──────────────────────────────────────────────────────

    public boolean areShippingOptionsDisplayed() {
        return !driver.findElements(SHIPPING_OPTIONS).isEmpty();
    }

    public StoreFrontCheckoutPage selectStandardShipping() {
        click(STANDARD_SHIPPING);
        log.info("Selected Standard Shipping");
        return this;
    }

    public StoreFrontCheckoutPage selectExpressShipping() {
        click(EXPRESS_SHIPPING);
        log.info("Selected Express Shipping");
        return this;
    }

    public String getShippingFee() {
        return isDisplayed(ORDER_SHIPPING_FEE) ? getText(ORDER_SHIPPING_FEE) : "0";
    }

    public StoreFrontCheckoutPage clickContinueToPayment() {
        click(CONTINUE_TO_PAYMENT_BTN);
        log.info("Clicked Continue to Payment");
        return this;
    }

    // ── Step 3: Payment ───────────────────────────────────────────────────────

    public boolean arePaymentOptionsDisplayed() {
        return !driver.findElements(PAYMENT_OPTIONS).isEmpty();
    }

    public StoreFrontCheckoutPage selectManualPayment() {
        click(MANUAL_PAYMENT_OPTION);
        log.info("Selected Manual Payment");
        return this;
    }

    public StoreFrontCheckoutPage clickContinueToReview() {
        click(CONTINUE_TO_REVIEW_BTN);
        log.info("Clicked Continue to Review");
        return this;
    }

    // ── Step 4: Review ────────────────────────────────────────────────────────

    public boolean isReviewSectionDisplayed() {
        return isDisplayed(PLACE_ORDER_BTN);
    }

    public StoreFrontCheckoutPage clickPlaceOrder() {
        click(PLACE_ORDER_BTN);
        log.info("Clicked Place Order");
        return this;
    }

    public StoreFrontCheckoutPage doubleClickPlaceOrder() {
        WebElement btn = waitForClickable(PLACE_ORDER_BTN);
        new org.openqa.selenium.interactions.Actions(driver)
                .doubleClick(btn)
                .perform();
        log.info("Double-clicked Place Order button");
        return this;
    }

    // ── Order Summary ─────────────────────────────────────────────────────────

    public String getOrderSubtotal() {
        return isDisplayed(ORDER_SUBTOTAL) ? getText(ORDER_SUBTOTAL) : "";
    }

    public String getOrderTotal() {
        return isDisplayed(ORDER_TOTAL) ? getText(ORDER_TOTAL) : "";
    }

    // ── Order Confirmation ────────────────────────────────────────────────────

    public boolean isOrderConfirmationDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(CONFIRMATION_HEADING)) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getOrderNumber() {
        return isDisplayed(ORDER_NUMBER) ? getText(ORDER_NUMBER) : "";
    }

    // ── XSS Helper ───────────────────────────────────────────────────────────

    public String getEmailFieldValue() {
        return getAttribute(EMAIL_INPUT, "value");
    }

    public String getFirstNameFieldValue() {
        return getAttribute(FIRST_NAME_INPUT, "value");
    }

    public boolean isJavaScriptAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }
}
