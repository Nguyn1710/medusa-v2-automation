package com.medusa.automation.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * AdminProductCreateDrawer — Drawer tạo sản phẩm mới.
 * Medusa Admin v2 dùng multi-step wizard: Details → Organize → Variants
 * DOM Recon: Drawer slide-in từ phải, tab-based navigation
 */
public class AdminProductCreateDrawer extends BasePage {

    private static final Logger log = LogManager.getLogger(AdminProductCreateDrawer.class);

    // ── Locators (verified từ DOM Recon) ──────────────────────────────────────
    private final By drawerContainer  = By.xpath("//div[@role='dialog'] | //aside[contains(@class,'drawer')]");
    private final By titleInput       = By.xpath("//input[@name='title' or @placeholder='Give your product a title']");
    private final By subtitleInput    = By.xpath("//input[@name='subtitle']");
    private final By handleInput      = By.xpath("//input[@name='handle']");
    private final By descriptionArea  = By.xpath("//div[@contenteditable='true'] | //textarea[@name='description']");
    private final By continueButton   = By.xpath("//button[.//span[text()='Continue']]");
    private final By publishButton    = By.xpath("//button[.//span[text()='Publish']]");
    private final By saveButton       = By.xpath("//button[.//span[text()='Save' or text()='Create']]");

    // Tabs
    private final By detailsTab   = By.xpath("//button[@role='tab'][.//span[contains(text(),'Details')]]");
    private final By organizeTab  = By.xpath("//button[@role='tab'][.//span[contains(text(),'Organize')]]");
    private final By variantsTab  = By.xpath("//button[@role='tab'][.//span[contains(text(),'Variants')]]");

    // Variants section
    private final By variantSwitch       = By.xpath("//button[@role='switch'][contains(@aria-label,'variant') or @name='has_variants']");
    private final By productOptionInput  = By.xpath("//input[@placeholder='Color, Size, Material...']");
    private final By optionValuesInput   = By.xpath("//input[@placeholder='Blue, Red, Black...']");

    public AdminProductCreateDrawer(WebDriver driver) {
        super(driver);
    }

    // ── Verification ───────────────────────────────────────────────────────────

    public boolean isDrawerDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleInput));
            return true;
        } catch (Exception e) {
            log.warn("Create Product drawer không hiển thị");
            return false;
        }
    }

    public boolean isTitleInputDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(titleInput)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isContinueButtonDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(continueButton)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ── Actions ─────────────────────────────────────────────────────────────────

    /**
     * Điền Title sản phẩm.
     */
    public AdminProductCreateDrawer fillTitle(String title) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(titleInput));
        input.clear();
        input.sendKeys(title);
        log.info("Filled product title: {}", title);
        return this;
    }

    /**
     * Click Continue để sang bước kế tiếp trong wizard.
     */
    public AdminProductCreateDrawer clickContinue() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        btn.click();
        log.info("Clicked Continue button in Create Product drawer");
        return this;
    }

    /**
     * Click Publish (bước cuối) để tạo sản phẩm và publish.
     * @return AdminProductListPage sau khi redirect
     */
    public AdminProductListPage clickPublish() {
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(publishButton));
            btn.click();
            log.info("Clicked Publish button — product created");
        } catch (Exception e) {
            // Fallback: Save button
            WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(saveButton));
            saveBtn.click();
            log.info("Clicked Save button (fallback) — product created");
        }
        return new AdminProductListPage(driver);
    }

    /**
     * Happy path: Điền title → Continue qua các bước → Publish.
     * Phù hợp cho test case tạo sản phẩm nhanh không có variants.
     */
    public AdminProductListPage createSimpleProduct(String title) {
        fillTitle(title);
        // Bước Details → Continue
        clickContinue();
        // Bước Organize → Continue
        clickContinue();
        // Bước Variants → Publish
        return clickPublish();
    }

    /**
     * Kiểm tra có validation error hiển thị không sau khi submit thiếu Title.
     */
    public boolean isValidationErrorDisplayed() {
        try {
            By errorLocator = By.xpath(
                "//p[contains(@class,'error') or contains(@class,'invalid')] | " +
                "//*[contains(@role,'alert') and contains(text(),'required')]"
            );
            wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
