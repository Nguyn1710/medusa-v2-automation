package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * StoreFrontHomePage — Page Object cho Storefront Homepage /gb.
 *
 * Locators verified từ DOM thực tế (MCP browser inspection).
 * TC Covered:
 *   - MED_SF_TC_001: Cấu trúc trang chủ /gb (Header, Hero, Footer)
 *   - MED_SF_TC_002: Mở và đóng Menu Navigation Overlay
 *   - MED_SF_TC_048: Kiểm tra URL prefix /gb + currency
 */
public class StoreFrontHomePage extends BasePage {

    private static final Logger log = LogManager.getLogger(StoreFrontHomePage.class);

    // ── Locators (verified from DOM — MCP inspection) ────────────────────────

    // Header locators
    private static final By MENU_BUTTON        = By.cssSelector("button[data-testid='nav-menu-button']");
    private static final By BRAND_TEXT         = By.xpath("//*[contains(text(),'MEDUSA STORE') and (self::a or self::span or self::p)]");
    private static final By HEADER_ACCOUNT     = By.cssSelector("header a[href*='/account']");
    private static final By HEADER_CART        = By.cssSelector("header a[href*='/cart']");
    private static final By CART_COUNT_BADGE   = By.cssSelector("header a[href*='/cart'] span");

    // Hero section
    private static final By HERO_HEADING       = By.cssSelector("h1, h2[class*='h1'], [data-testid='hero-heading']");
    private static final By HERO_CTA_LINK      = By.xpath("//a[contains(text(),'Visit the tutorial')]");

    // Footer
    private static final By FOOTER_COPYRIGHT   = By.xpath("//footer//*[contains(text(),'All rights reserved')]");
    private static final By FOOTER_SHIRTS      = By.cssSelector("footer a[href*='shirts']");
    private static final By FOOTER_SWEATSHIRTS = By.cssSelector("footer a[href*='sweatshirts']");
    private static final By FOOTER_PANTS       = By.cssSelector("footer a[href*='pants']");
    private static final By FOOTER_MERCH       = By.cssSelector("footer a[href*='merch']");
    private static final By FOOTER_GITHUB      = By.cssSelector("footer a[href*='github']");
    private static final By FOOTER_DOCS        = By.cssSelector("footer a[href*='docs']");

    // Menu overlay
    private static final By OVERLAY_STORE_LINK  = By.xpath("//a[normalize-space(text())='Store']");
    private static final By OVERLAY_HOME_LINK   = By.xpath("//a[normalize-space(text())='Home']");
    private static final By OVERLAY_SEARCH_LINK = By.xpath("//a[normalize-space(text())='Search']");
    private static final By OVERLAY_ACCOUNT_LINK = By.xpath("//nav//a[normalize-space(text())='Account']");
    private static final By OVERLAY_CART_LINK   = By.xpath("//nav//a[normalize-space(text())='Cart']");
    private static final By OVERLAY_CLOSE_BTN   = By.cssSelector("button[data-testid='close-menu-btn'], button[aria-label='Close menu']");
    private static final By SHIPPING_COUNTRY    = By.xpath("//*[contains(text(),'Shipping to')]");

    private final String homeUrl;
    private final String basePath;

    public StoreFrontHomePage(WebDriver driver) {
        super(driver);
        ConfigReader config = ConfigReader.getInstance();
        this.homeUrl = config.getStoreFrontUrl() + config.getStoreFrontBasePath();
        this.basePath = config.getStoreFrontBasePath();
    }

    // ── Navigation ───────────────────────────────────────────────────────────

    public StoreFrontHomePage navigateTo() {
        driver.get(homeUrl);
        waitForVisible(HERO_HEADING);
        log.info("Navigated to StoreFront Homepage: {}", homeUrl);
        return this;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isUrlContainsBasePath() {
        return driver.getCurrentUrl().contains(basePath);
    }

    // ── Header verifications ─────────────────────────────────────────────────

    public boolean isMenuButtonDisplayed() {
        return isDisplayed(MENU_BUTTON);
    }

    public boolean isBrandTextDisplayed() {
        return isDisplayed(BRAND_TEXT);
    }

    public boolean isAccountLinkInHeaderDisplayed() {
        return isDisplayed(HEADER_ACCOUNT);
    }

    public boolean isCartLinkInHeaderDisplayed() {
        return isDisplayed(HEADER_CART);
    }

    public String getCartItemCount() {
        try {
            return getText(CART_COUNT_BADGE).trim();
        } catch (Exception e) {
            return "0";
        }
    }

    // ── Hero verifications ───────────────────────────────────────────────────

    public boolean isHeroHeadingDisplayed() {
        return isDisplayed(HERO_HEADING);
    }

    public String getHeroHeadingText() {
        return getText(HERO_HEADING);
    }

    public boolean isHeroCtaDisplayed() {
        return isDisplayed(HERO_CTA_LINK);
    }

    // ── Footer verifications ─────────────────────────────────────────────────

    public boolean isFooterCopyrightDisplayed() {
        return isDisplayed(FOOTER_COPYRIGHT);
    }

    public String getFooterCopyrightText() {
        return getText(FOOTER_COPYRIGHT);
    }

    public boolean isFooterCategoryLinksDisplayed() {
        return isDisplayed(FOOTER_SHIRTS) || isDisplayed(FOOTER_SWEATSHIRTS);
    }

    public boolean isFooterShirtsLinkDisplayed() {
        return isDisplayed(FOOTER_SHIRTS);
    }

    public boolean isFooterSweatshirtsLinkDisplayed() {
        return isDisplayed(FOOTER_SWEATSHIRTS);
    }

    public boolean isFooterPantsLinkDisplayed() {
        return isDisplayed(FOOTER_PANTS);
    }

    public boolean isFooterMerchLinkDisplayed() {
        return isDisplayed(FOOTER_MERCH);
    }

    public boolean isFooterGithubLinkDisplayed() {
        return isDisplayed(FOOTER_GITHUB);
    }

    public boolean isFooterDocsLinkDisplayed() {
        return isDisplayed(FOOTER_DOCS);
    }

    // ── Menu Overlay actions ─────────────────────────────────────────────────

    public StoreFrontHomePage openMenuOverlay() {
        click(MENU_BUTTON);
        waitForVisible(OVERLAY_STORE_LINK);
        log.info("Opened Menu Navigation Overlay");
        return this;
    }

    public boolean isMenuOverlayDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(OVERLAY_STORE_LINK)) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isOverlayHomeLinkDisplayed() {
        return isDisplayed(OVERLAY_HOME_LINK);
    }

    public boolean isOverlayStoreLinkDisplayed() {
        return isDisplayed(OVERLAY_STORE_LINK);
    }

    public boolean isOverlaySearchLinkDisplayed() {
        return isDisplayed(OVERLAY_SEARCH_LINK);
    }

    public boolean isOverlayAccountLinkDisplayed() {
        return isDisplayed(OVERLAY_ACCOUNT_LINK);
    }

    public boolean isOverlayCartLinkDisplayed() {
        return isDisplayed(OVERLAY_CART_LINK);
    }

    public boolean isShippingCountryDisplayed() {
        return isDisplayed(SHIPPING_COUNTRY);
    }

    public String getShippingCountryText() {
        return getText(SHIPPING_COUNTRY);
    }

    public StoreFrontHomePage closeMenuOverlay() {
        click(OVERLAY_CLOSE_BTN);
        waitForElementInvisible(OVERLAY_STORE_LINK);
        log.info("Closed Menu Navigation Overlay");
        return this;
    }

    public boolean isMenuOverlayClosed() {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(OVERLAY_STORE_LINK));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ── Navigation to other pages ─────────────────────────────────────────────

    public StoreFrontAccountPage clickAccountLink() {
        click(HEADER_ACCOUNT);
        return new StoreFrontAccountPage(driver);
    }

    public StoreFrontCartPage clickCartLink() {
        click(HEADER_CART);
        return new StoreFrontCartPage(driver);
    }
}
