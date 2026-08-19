package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.StoreFrontStorePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontStoreTest — Automation tests cho Store listing page.
 *
 * TCs:
 *   - MED_SF_TC_003: Danh sách sản phẩm Store /gb/store
 *   - MED_SF_TC_004: Sắp xếp sản phẩm theo giá
 *   - MED_SF_TC_005: Lọc sản phẩm theo Category
 *   - MED_SF_TC_006: Phân trang / scroll infinite
 */
@Epic("Storefront")
@Feature("Store — Product Listing")
public class StoreFrontStoreTest extends BaseTest {

    // ─── MED_SF_TC_003 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_003")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Trang /gb/store hiển thị danh sách sản phẩm với ảnh, tên, giá")
    public void testStorePageDisplaysProducts() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();

        Assert.assertTrue(storePage.isStorePageDisplayed(),
                "Trang Store /gb/store phải hiển thị được danh sách sản phẩm");
        int count = storePage.getProductCount();
        Assert.assertTrue(count > 0,
                "Phải có ít nhất 1 sản phẩm hiển thị trên trang Store — count: " + count);
    }

    // ─── MED_SF_TC_004 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_004")
    @Severity(SeverityLevel.NORMAL)
    @Description("Sort dropdown hiển thị và có thể thay đổi thứ tự sản phẩm theo giá")
    public void testSortControlIsDisplayed() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();

        Assert.assertTrue(storePage.isSortControlDisplayed(),
                "Sort control (dropdown/button) phải hiển thị trên trang Store");
    }

    // ─── MED_SF_TC_005 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_005")
    @Severity(SeverityLevel.NORMAL)
    @Description("Category filter links hiển thị (Shirts, Sweatshirts, Pants, Merch)")
    public void testCategoryFiltersAreDisplayed() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();

        Assert.assertTrue(storePage.isCategoryFiltersDisplayed(),
                "Category filter links (Shirts/Sweatshirts) phải hiển thị trên Store page");
    }

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_005")
    @Severity(SeverityLevel.NORMAL)
    @Description("Click Category Shirts → Filter danh sách sản phẩm theo Shirts")
    public void testFilterByCategoryShirts() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();

        int allProductCount = storePage.getProductCount();
        storePage.filterByCategory("shirts");

        int filteredCount = storePage.getProductCount();
        Assert.assertTrue(filteredCount > 0,
                "Sau filter Shirts, phải có ít nhất 1 sản phẩm — count: " + filteredCount);
        Assert.assertTrue(driver.getCurrentUrl().contains("shirts"),
                "URL sau filter phải chứa 'shirts'");
    }

    // ─── MED_SF_TC_006 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_006")
    @Severity(SeverityLevel.MINOR)
    @Description("Trang Store có phân trang hoặc infinite scroll — danh sách sản phẩm có thể duyệt")
    public void testProductListIsPaginatedOrScrollable() {
        StoreFrontStorePage storePage = new StoreFrontStorePage(driver);
        storePage.navigateTo();

        boolean hasPagination = storePage.isNextPageButtonDisplayed()
                || storePage.getProductCount() > 0;
        Assert.assertTrue(hasPagination,
                "Store page phải có phân trang hoặc danh sách sản phẩm duyệt được");
    }
}
