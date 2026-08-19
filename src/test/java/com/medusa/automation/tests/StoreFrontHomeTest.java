package com.medusa.automation.tests;

import com.medusa.automation.base.BaseTest;
import com.medusa.automation.pages.StoreFrontHomePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * StoreFrontHomeTest — Automation tests cho Storefront Homepage & Navigation.
 *
 * TCs:
 *   - MED_SF_TC_001: Cấu trúc trang chủ /gb (Header, Hero, Footer)
 *   - MED_SF_TC_002: Mở và đóng Menu Navigation Overlay
 *   - MED_SF_TC_048: Kiểm tra URL prefix /gb + currency EUR
 */
@Epic("Storefront")
@Feature("Homepage & Navigation")
public class StoreFrontHomeTest extends BaseTest {

    // ─── MED_SF_TC_001 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Cấu trúc trang chủ /gb hiển thị đầy đủ Header, Hero section và Footer")
    public void testHomepageHeaderIsDisplayed() {
        StoreFrontHomePage home = new StoreFrontHomePage(driver);
        home.navigateTo();

        Assert.assertTrue(home.isMenuButtonDisplayed(),
                "Menu button phải hiển thị ở Header (góc trên bên trái)");
        Assert.assertTrue(home.isAccountLinkInHeaderDisplayed(),
                "Account icon/link phải hiển thị trong Header");
        Assert.assertTrue(home.isCartLinkInHeaderDisplayed(),
                "Cart icon/link phải hiển thị trong Header");
    }

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_001")
    @Severity(SeverityLevel.NORMAL)
    @Description("Hero section của trang chủ /gb hiển thị heading và CTA button")
    public void testHomepageHeroSectionIsDisplayed() {
        StoreFrontHomePage home = new StoreFrontHomePage(driver);
        home.navigateTo();

        Assert.assertTrue(home.isHeroHeadingDisplayed(),
                "Hero heading (h1) phải hiển thị trên homepage");
        String headingText = home.getHeroHeadingText();
        Assert.assertFalse(headingText.isEmpty(),
                "Hero heading text không được rỗng");
    }

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_001")
    @Severity(SeverityLevel.MINOR)
    @Description("Footer của trang chủ /gb hiển thị copyright, category links và Medusa links")
    public void testHomepageFooterIsDisplayed() {
        StoreFrontHomePage home = new StoreFrontHomePage(driver);
        home.navigateTo();

        Assert.assertTrue(home.isFooterCopyrightDisplayed(),
                "Footer copyright text phải hiển thị");
        Assert.assertTrue(home.isFooterCategoryLinksDisplayed(),
                "Footer phải có ít nhất 1 category link (Shirts/Sweatshirts)");
        Assert.assertTrue(home.isFooterGithubLinkDisplayed(),
                "Footer phải có GitHub link trong section Medusa");
    }

    // ─── MED_SF_TC_002 ────────────────────────────────────────────────────────

    @Test(groups = {"smoke", "regression"})
    @Story("MED_SF_TC_002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click Menu button → Overlay mở ra và hiển thị các links điều hướng")
    public void testMenuOverlayOpensWithNavigationLinks() {
        StoreFrontHomePage home = new StoreFrontHomePage(driver);
        home.navigateTo();

        home.openMenuOverlay();

        Assert.assertTrue(home.isMenuOverlayDisplayed(),
                "Menu overlay phải hiển thị sau khi click Menu button");
        Assert.assertTrue(home.isOverlayStoreLinkDisplayed(),
                "Link 'Store' phải có trong menu overlay");
        Assert.assertTrue(home.isOverlaySearchLinkDisplayed(),
                "Link 'Search' phải có trong menu overlay");
    }

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_002")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Click nút X (đóng) → Overlay đóng mượt mà, quay về trang cũ")
    public void testMenuOverlayClosesWithXButton() {
        StoreFrontHomePage home = new StoreFrontHomePage(driver);
        home.navigateTo();

        home.openMenuOverlay();
        Assert.assertTrue(home.isMenuOverlayDisplayed(),
                "Menu overlay phải mở trước khi test đóng");

        home.closeMenuOverlay();
        Assert.assertTrue(home.isMenuOverlayClosed(),
                "Menu overlay phải đóng sau khi click nút X");
    }

    // ─── MED_SF_TC_048 ────────────────────────────────────────────────────────

    @Test(groups = {"regression"})
    @Story("MED_SF_TC_048")
    @Severity(SeverityLevel.NORMAL)
    @Description("Kiểm tra cấu trúc URL với mã quốc gia /gb (United Kingdom)")
    public void testUrlContainsCountryPrefix() {
        StoreFrontHomePage home = new StoreFrontHomePage(driver);
        home.navigateTo();

        String currentUrl = home.getCurrentUrl();
        Assert.assertTrue(home.isUrlContainsBasePath(),
                "URL phải chứa prefix /gb — URL hiện tại: " + currentUrl);
    }
}
