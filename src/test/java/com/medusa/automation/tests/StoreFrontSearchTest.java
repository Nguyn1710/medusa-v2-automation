package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.StoreFrontSearchPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontSearchTest — Automation tests cho chức năng Tìm kiếm.
 *
 * TCs:
 *   - MED_SF_TC_039: Mở trang /gb/search — kết quả mặc định
 *   - MED_SF_TC_040: Tìm kiếm realtime 'sweatshirt'
 *   - MED_SF_TC_041: Từ khóa không khớp — 'No results found.'
 *   - MED_SF_TC_042: Nút Cancel reset search
 *   - MED_SF_TC_043: XSS Injection trong Search
 */
@Epic("Storefront")
@Feature("Search")
public class StoreFrontSearchTest extends BaseTest {

    // ─── MED_SF_TC_039 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_039")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Mở trang /gb/search — kiểm tra Search input và sản phẩm mặc định")
    public void testSearchPageDisplaysDefaultProducts() {
        StoreFrontSearchPage searchPage = new StoreFrontSearchPage(driver);
        searchPage.navigateTo();

        Assert.assertTrue(searchPage.isSearchInputDisplayed(),
                "Search input với placeholder 'Search products...' phải hiển thị");
        Assert.assertTrue(searchPage.areDefaultProductsDisplayed(),
                "Trang Search mặc định phải hiển thị danh sách sản phẩm (grid)");
        int count = searchPage.getResultCount();
        Assert.assertTrue(count > 0,
                "Phải có ít nhất 1 sản phẩm hiển thị mặc định — count: " + count);
    }

    // ─── MED_SF_TC_040 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_040")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tìm kiếm realtime 'sweatshirt' — kết quả cập nhật ngay, hiển thị sản phẩm khớp")
    public void testRealtimeSearchFiltersResults() {
        StoreFrontSearchPage searchPage = new StoreFrontSearchPage(driver);
        searchPage.navigateTo();

        searchPage.typeSearchKeyword("sweatshirt");
        searchPage.waitForRealtimeResults();

        // Verify: kết quả phải xuất hiện (> 0) sau khi gõ keyword
        int filteredCount = searchPage.getResultCount();
        Assert.assertTrue(filteredCount > 0,
                "Sau khi gõ 'sweatshirt', phải có ít nhất 1 kết quả — count: " + filteredCount);
        Assert.assertTrue(searchPage.hasResultsContaining("sweatshirt"),
                "Sau khi gõ 'sweatshirt', kết quả phải hiển thị sản phẩm khớp");
    }

    // ─── MED_SF_TC_041 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_041")
    @Severity(SeverityLevel.NORMAL)
    @Description("Tìm kiếm với từ khóa không khớp — Hiển thị 'No results found.' và không crash app")
    public void testNoResultsFoundForUnknownKeyword() {
        StoreFrontSearchPage searchPage = new StoreFrontSearchPage(driver);
        searchPage.navigateTo();

        searchPage.typeSearchKeyword("nonexistingitem12345xyzabc");

        Assert.assertTrue(searchPage.isNoResultsMessageDisplayed(),
                "Phải hiển thị thông báo 'No results found.' khi không tìm thấy sản phẩm");
        Assert.assertFalse(searchPage.isJavaScriptAlertPresent(),
                "Không được có JavaScript alert — app không bị crash");
    }

    // ─── MED_SF_TC_042 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_042")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Nút '× Cancel' xóa từ khóa và reset danh sách về tất cả sản phẩm")
    public void testCancelButtonResetsSearch() {
        StoreFrontSearchPage searchPage = new StoreFrontSearchPage(driver);
        searchPage.navigateTo();

        searchPage.typeSearchKeyword("sweatshirt");
        searchPage.waitForRealtimeResults();
        int filteredCount = searchPage.getResultCount();

        searchPage.clickCancelButton();

        Assert.assertTrue(searchPage.isSearchInputEmpty(),
                "Sau khi click Cancel, search input phải được xóa trống");
        int afterCancelCount = searchPage.getResultCount();
        Assert.assertTrue(afterCancelCount >= filteredCount,
                "Sau Cancel, kết quả phải reset về danh sách đầy đủ — count: " + afterCancelCount);
    }

    // ─── MED_SF_TC_043 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_043")
    @Severity(SeverityLevel.BLOCKER)
    @Description("XSS Injection trong ô Search — React/Next.js phải escape HTML, không có alert popup")
    public void testXssInjectionInSearchIsEscaped() {
        StoreFrontSearchPage searchPage = new StoreFrontSearchPage(driver);
        searchPage.navigateTo();

        String xssPayload = "<script>alert('XSS_SF')</script>";
        searchPage.typeSearchKeyword(xssPayload);

        Assert.assertFalse(searchPage.isJavaScriptAlertPresent(),
                "Không được có JavaScript alert — XSS phải bị blocked/escaped");
        Assert.assertTrue(searchPage.isNoResultsMessageDisplayed()
                        || !searchPage.getSearchInputValue().contains("<script>"),
                "Search phải hiển thị 'No results found.' và không thực thi script");
    }
}
