package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * BasePage — common methods cho tất cả Page Object classes.
 * Không chứa locator hay business logic cụ thể của từng page.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    private static final Logger log = LogManager.getLogger(BasePage.class);

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigReader.getInstance().getExplicitWait();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    // ── Wait helpers ────────────────────────────────────────────────────────

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected boolean waitForUrlContains(String urlFragment) {
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    protected boolean waitForTextPresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    protected boolean waitForElementInvisible(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    // ── Action helpers ───────────────────────────────────────────────────────

    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        log.debug("Click: {}", locator);
        element.click();
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        log.debug("Type '{}' into: {}", text, locator);
    }

    protected void typeWithTab(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text, Keys.TAB);
        log.debug("Type '{}' + TAB into: {}", text, locator);
    }

    protected void pressEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
        log.debug("Press ENTER on: {}", locator);
    }

    protected void pressTab(By locator) {
        waitForVisible(locator).sendKeys(Keys.TAB);
        log.debug("Press TAB on: {}", locator);
    }

    // ── Query helpers ────────────────────────────────────────────────────────

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ── Cookie helpers ────────────────────────────────────────────────────────

    protected void deleteAllCookies() {
        driver.manage().deleteAllCookies();
        log.debug("All cookies deleted");
    }

    // ── JavaScript helpers ────────────────────────────────────────────────────

    protected Object executeJs(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }
}
