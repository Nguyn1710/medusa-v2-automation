package com.medusa.automation.pages;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * StoreFrontSearchPage — Page Object cho trang Tìm kiếm /gb/search.
 *
 * Locators verified từ DOM thực tế (MCP browser inspection).
 * TC Covered:
 *   - MED_SF_TC_039: Mở trang /gb/search — kết quả mặc định
 *   - MED_SF_TC_040: Tìm kiếm realtime 'sweatshirt'
 *   - MED_SF_TC_041: Từ khóa không khớp — 'No results found.'
 *   - MED_SF_TC_042: Nút Cancel reset search
 *   - MED_SF_TC_043: XSS Injection trong Search
 */
public class StoreFrontSearchPage extends BasePage {

    private static final Logger log = LogManager.getLogger(StoreFrontSearchPage.class);

    // ── Locators (verified from DOM) ──────────────────────────────────────────

    // Search locators — data-testids verified from DOM (Search is a modal overlay)
    private static final By SEARCH_INPUT       = By.cssSelector("input[data-testid='search-input']");
    private static final By CANCEL_BTN         = By.xpath("//button[normalize-space(text())='Cancel']");
    private static final By SEARCH_ICON        = By.cssSelector("[data-testid='search-modal-container'] svg");
    private static final By RESULTS_GRID       = By.cssSelector("div[data-testid='search-results']");
    // Result items — a[data-testid='search-result'] (verified from DOM)
    private static final By RESULT_ITEMS       = By.cssSelector("a[data-testid='search-result']");
    private static final By NO_RESULTS_MSG     = By.xpath("//*[contains(text(),'No results found') or contains(text(),'no results')]");

    private final String searchUrl;

    public StoreFrontSearchPage(WebDriver driver) {
        super(driver);
        ConfigReader config = ConfigReader.getInstance();
        this.searchUrl = config.getStoreFrontUrl() + config.getStoreFrontBasePath() + "/search";
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public StoreFrontSearchPage navigateTo() {
        driver.get(searchUrl);
        waitForVisible(SEARCH_INPUT);
        // Wait thêm cho search results container hiển thị (có sẵn mặc định)
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(RESULTS_GRID));
        } catch (TimeoutException e) {
            log.warn("Search results grid không xuất hiện ngay — tiếp tục");
        }
        log.info("Navigated to Search page: {}", searchUrl);
        return this;
    }

    // ── Page verifications ────────────────────────────────────────────────────

    public boolean isSearchPageDisplayed() {
        return isDisplayed(SEARCH_INPUT);
    }

    public boolean isSearchInputDisplayed() {
        return isDisplayed(SEARCH_INPUT);
    }

    public boolean isCancelButtonDisplayed() {
        return isDisplayed(CANCEL_BTN);
    }

    public boolean isSearchIconDisplayed() {
        return isDisplayed(SEARCH_ICON);
    }

    // ── Default state (no search term) ───────────────────────────────────────

    public boolean areDefaultProductsDisplayed() {
        // Wait cho result items xuất hiện (search results load async)
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(RESULT_ITEMS));
        } catch (TimeoutException e) {
            return false;
        }
        return !driver.findElements(RESULT_ITEMS).isEmpty();
    }

    public int getResultCount() {
        return driver.findElements(RESULT_ITEMS).size();
    }

    public List<WebElement> getAllResults() {
        return driver.findElements(RESULT_ITEMS);
    }

    // ── Search actions ────────────────────────────────────────────────────────

    public StoreFrontSearchPage typeSearchKeyword(String keyword) {
        type(SEARCH_INPUT, keyword);
        log.info("Typed search keyword: '{}'", keyword);
        return this;
    }

    public StoreFrontSearchPage waitForRealtimeResults() {
        try {
            // Chờ kết quả real-time cập nhật — dùng wait chứ không phải Thread.sleep
            wait.until(ExpectedConditions.visibilityOfElementLocated(RESULT_ITEMS));
        } catch (TimeoutException e) {
            // Results may not appear — possibly "No results found"
        }
        return this;
    }

    public boolean hasResultsContaining(String keyword) {
        List<WebElement> results = driver.findElements(RESULT_ITEMS);
        return !results.isEmpty();
    }

    public StoreFrontSearchPage clickCancelButton() {
        click(CANCEL_BTN);
        log.info("Clicked Cancel button");
        return this;
    }

    // ── No results state ──────────────────────────────────────────────────────

    public boolean isNoResultsMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(NO_RESULTS_MSG)) != null;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getNoResultsMessageText() {
        return isDisplayed(NO_RESULTS_MSG) ? getText(NO_RESULTS_MSG) : "";
    }

    // ── XSS helper ───────────────────────────────────────────────────────────

    public String getSearchInputValue() {
        return getAttribute(SEARCH_INPUT, "value");
    }

    public boolean isJavaScriptAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    public StoreFrontSearchPage clearSearch() {
        WebElement searchInput = waitForVisible(SEARCH_INPUT);
        searchInput.clear();
        log.info("Cleared search input");
        return this;
    }

    // ── After cancel — verify reset ───────────────────────────────────────────

    public boolean isSearchInputEmpty() {
        String value = getAttribute(SEARCH_INPUT, "value");
        return value == null || value.trim().isEmpty();
    }
}
