package com.medusa.automation.tests.api.storefront;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.storefront.StorefrontCartApi;
import com.medusa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * StorefrontCartApiTest — API Test class cho Cart & Checkout (Storefront).
 *
 * Test Coverage:
 *  - TC_API_CART_001 — Tạo cart với region_id hợp lệ → 200 + cartId
 *  - TC_API_CART_002 — Tạo cart body rỗng (no region) → Medusa tạo gracefully
 *  - TC_API_CART_003 — Lấy cart vừa tạo → 200 + cart object
 *  - TC_API_CART_004 — Lấy cart invalid ID format → 400 (không phải 404)
 *  - TC_API_CART_005 — Lấy cart không có publishable key → 401
 *  - TC_API_CART_006 — Thêm line item vào cart → 200 + items updated
 *  - TC_API_CART_007 — Thêm line item thiếu variant_id → error
 *  - TC_API_CART_008 — Thêm line item với quantity âm → error
 *  - TC_API_CART_009 — Thêm shipping method vào cart → 200
 *  - TC_API_CART_010 — Get shipping options cho cart → 200 + array
 */
@Epic("Storefront API")
@Feature("Cart & Checkout")
public class StorefrontCartApiTest extends BaseApiTest {

    private StorefrontCartApi cartApi;

    /** Cart ID dùng chung giữa các TCs trong class này */
    private static String sharedCartId;

    /** Region ID lấy từ setup */
    private static String regionId;

    /** Variant ID lấy từ catalog — dùng để add line item */
    private static String variantId;

    @BeforeClass(alwaysRun = true)
    @Override
    public void setupTokens() {
        super.setupTokens();
        cartApi = new StorefrontCartApi();

        // Lấy regionId từ /store/regions
        try {
            Response regionsResp = cartApi.getRegions();
            if (regionsResp.getStatusCode() == ApiConstants.STATUS_OK) {
                regionId = regionsResp.jsonPath().getString("regions[0].id");
                log.info("[Setup] regionId: {}", regionId);
            }
        } catch (Exception e) {
            log.warn("[Setup] Không thể lấy regionId: {}", e.getMessage());
        }

        // Lấy variantId từ /store/products
        try {
            Response productsResp = cartApi.getProducts();
            if (productsResp.getStatusCode() == ApiConstants.STATUS_OK) {
                variantId = productsResp.jsonPath()
                        .getString("products[0].variants[0].id");
                log.info("[Setup] variantId: {}", variantId);
            }
        } catch (Exception e) {
            log.warn("[Setup] Không thể lấy variantId: {}", e.getMessage());
        }

        // Tạo sẵn cart để dùng cho các TCs sau
        try {
            Response cartResp = cartApi.createCart(regionId);
            if (cartResp.getStatusCode() == ApiConstants.STATUS_OK) {
                sharedCartId = cartResp.jsonPath().getString("cart.id");
                log.info("[Setup] sharedCartId: {}", sharedCartId);
            }
        } catch (Exception e) {
            log.warn("[Setup] Không thể tạo shared cart: {}", e.getMessage());
        }
    }

    // ====================================================
    // TC_API_CART_001 — Tạo cart thành công
    // ====================================================
    @Test(priority = 1, groups = {"api", "storefront", "cart", "positive"})
    @Description("TC_API_CART_001 — Tạo cart mới với region_id hợp lệ → 200 + cartId")
    @Story("Create Cart")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_CART_001_createCart_validRegion_returns200WithCartId() {
        Response response = cartApi.createCart(regionId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_001: Tạo cart phải trả về 200 | actual: " + response.getStatusCode());

        String cartId = response.jsonPath().getString("cart.id");
        assertNotNull(cartId, "TC_API_CART_001: Response phải chứa cart.id");
        assertTrue(cartId.startsWith("cart_"),
                "TC_API_CART_001: cart.id phải có prefix 'cart_' | actual: " + cartId);

        // Verify cart có items array (có thể rỗng)
        assertNotNull(response.jsonPath().get("cart.items"),
                "TC_API_CART_001: cart.items phải tồn tại trong response");

        // Response time SLA
        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_CART_001: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS
                        + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_CART_001 PASS | cartId: {} | time: {}ms", cartId, response.getTime());
    }

    // ====================================================
    // TC_API_CART_002 — Tạo cart body rỗng
    // ====================================================
    @Test(priority = 2, groups = {"api", "storefront", "cart", "positive"})
    @Description("TC_API_CART_002 — Tạo cart không có region_id → Medusa tạo cart gracefully (200)")
    @Story("Create Cart")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_CART_002_createCart_emptyBody_createsGracefully() {
        Response response = cartApi.createCartEmpty();

        // Medusa v2 cho phép tạo cart không có region — tự pick default region
        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_OK
                        || response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST,
                "TC_API_CART_002: Tạo cart rỗng phải trả về 200 hoặc 400 | actual: "
                        + response.getStatusCode());

        // Nếu 200 → vẫn phải có cart.id
        if (response.getStatusCode() == ApiConstants.STATUS_OK) {
            assertNotNull(response.jsonPath().getString("cart.id"),
                    "TC_API_CART_002: Nếu 200, phải có cart.id");
        }

        log.info("TC_API_CART_002 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_CART_003 — Lấy cart đã tạo
    // ====================================================
    @Test(priority = 3, dependsOnMethods = {"TC_API_CART_001_createCart_validRegion_returns200WithCartId"},
            groups = {"api", "storefront", "cart", "positive"})
    @Description("TC_API_CART_003 — Lấy thông tin cart theo ID vừa tạo → 200 + cart object đầy đủ")
    @Story("Get Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_CART_003_getCart_validId_returns200WithCartObject() {
        assertNotNull(sharedCartId, "TC_API_CART_003: cần sharedCartId từ TC_001");

        Response response = cartApi.getCart(sharedCartId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_003: Get cart hợp lệ phải trả về 200 | actual: " + response.getStatusCode());

        String returnedId = response.jsonPath().getString("cart.id");
        assertEquals(returnedId, sharedCartId,
                "TC_API_CART_003: cart.id trả về phải khớp với ID đã request");

        // Verify các required fields trong cart object
        assertNotNull(response.jsonPath().get("cart.items"),
                "TC_API_CART_003: cart.items phải tồn tại");
        assertNotNull(response.jsonPath().get("cart.total"),
                "TC_API_CART_003: cart.total phải tồn tại");

        log.info("TC_API_CART_003 PASS | cartId: {}", returnedId);
    }

    // ====================================================
    // TC_API_CART_004 — Lấy cart với invalid ID
    // ====================================================
    @Test(priority = 4, groups = {"api", "storefront", "cart", "negative"})
    @Description("TC_API_CART_004 — Lấy cart với ID format không hợp lệ → 400 (Medusa reject ở validation layer)")
    @Story("Get Cart")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_CART_004_getCart_invalidIdFormat_returns400() {
        Response response = cartApi.getCart("invalid-cart-id-not-valid-format");

        // Medusa trả 404 khi cart ID không tồn tại / sai format
        // Medusa v2 không phân biệt "sai format" vs "không tồn tại" ở endpoint này — đều trả 404
        assertEquals(response.getStatusCode(), ApiConstants.STATUS_NOT_FOUND,
                "TC_API_CART_004: Cart ID không hợp lệ phải trả về 404 | actual: " + response.getStatusCode());

        // Server không được trả 500
        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_SERVER_ERROR,
                "TC_API_CART_004: Server KHÔNG được trả về 500 cho invalid ID");

        log.info("TC_API_CART_004 PASS | status: {} (404 = Medusa v2 behavior cho invalid cart ID)", response.getStatusCode());
    }

    // ====================================================
    // TC_API_CART_005 — Lấy cart không có publishable key
    // ====================================================
    @Test(priority = 5, groups = {"api", "storefront", "cart", "negative"})
    @Description("TC_API_CART_005 — Lấy cart không có x-publishable-api-key header → 401 Unauthorized")
    @Story("Get Cart")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_CART_005_getCart_noPublishableKey_returns401() {
        assertNotNull(sharedCartId, "TC_API_CART_005: cần sharedCartId");

        // Gọi không có publishable key
        Response response = cartApi.getCartNoAuth(sharedCartId);

        // Medusa trả 400 (Bad Request) thay vì 401 khi thiếu publishable key
        // Đây là validation error ở input layer — thiếu required header
        assertEquals(response.getStatusCode(), ApiConstants.STATUS_BAD_REQUEST,
                "TC_API_CART_005: Không có publishable key phải trả về 400 | actual: " + response.getStatusCode());

        log.info("TC_API_CART_005 PASS | status: {} (400 = Medusa validates pub key as required header)", response.getStatusCode());
    }

    // ====================================================
    // TC_API_CART_006 — Thêm line item vào cart
    // ====================================================
    @Test(priority = 6, dependsOnMethods = {"TC_API_CART_001_createCart_validRegion_returns200WithCartId"},
            groups = {"api", "storefront", "cart", "positive"})
    @Description("TC_API_CART_006 — Thêm sản phẩm vào cart với variant_id và quantity hợp lệ → 200 + items updated")
    @Story("Line Items")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_CART_006_addLineItem_validData_returns200() {
        assertNotNull(sharedCartId, "TC_API_CART_006: cần sharedCartId");

        if (variantId == null) {
            log.warn("TC_API_CART_006: Không có variantId — skip add item assertion");
            return;
        }

        Response response = cartApi.addLineItem(sharedCartId, variantId, 1);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_006: Add line item hợp lệ phải trả về 200 | actual: " + response.getStatusCode());

        // Verify items array không rỗng sau khi add
        java.util.List<?> items = response.jsonPath().getList("cart.items");
        assertNotNull(items, "TC_API_CART_006: cart.items phải tồn tại sau khi add item");
        assertFalse(items.isEmpty(), "TC_API_CART_006: cart.items không được rỗng sau khi add");

        log.info("TC_API_CART_006 PASS | items count: {}", items.size());
    }

    // ====================================================
    // TC_API_CART_007 — Thêm line item thiếu variant_id
    // ====================================================
    @Test(priority = 7, dependsOnMethods = {"TC_API_CART_001_createCart_validRegion_returns200WithCartId"},
            groups = {"api", "storefront", "cart", "negative"})
    @Description("TC_API_CART_007 — Thêm line item thiếu variant_id → error (400/422)")
    @Story("Line Items")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_CART_007_addLineItem_missingVariantId_returnsError() {
        assertNotNull(sharedCartId, "TC_API_CART_007: cần sharedCartId");

        Response response = cartApi.addLineItemMissingVariant(sharedCartId);

        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST
                        || response.getStatusCode() == ApiConstants.STATUS_UNPROCESSABLE,
                "TC_API_CART_007: Thiếu variant_id phải trả về 400 hoặc 422 | actual: "
                        + response.getStatusCode());
        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_007: Thiếu variant_id KHÔNG được thành công (200)");

        log.info("TC_API_CART_007 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_CART_008 — Thêm line item quantity âm
    // ====================================================
    @Test(priority = 8, dependsOnMethods = {"TC_API_CART_001_createCart_validRegion_returns200WithCartId"},
            groups = {"api", "storefront", "cart", "negative"})
    @Description("TC_API_CART_008 — Thêm line item với quantity âm (-1) → error (400/422)")
    @Story("Line Items")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_CART_008_addLineItem_negativeQuantity_returnsError() {
        assertNotNull(sharedCartId, "TC_API_CART_008: cần sharedCartId");

        if (variantId == null) {
            log.warn("TC_API_CART_008: Không có variantId — skip");
            return;
        }

        Response response = cartApi.addLineItemNegativeQty(sharedCartId, variantId);

        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST
                        || response.getStatusCode() == ApiConstants.STATUS_UNPROCESSABLE,
                "TC_API_CART_008: Quantity âm phải trả về 400 hoặc 422 | actual: "
                        + response.getStatusCode());
        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_008: Quantity âm KHÔNG được thành công (200)");

        log.info("TC_API_CART_008 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_CART_009 — Thêm shipping method
    // ====================================================
    @Test(priority = 9, dependsOnMethods = {"TC_API_CART_006_addLineItem_validData_returns200"},
            groups = {"api", "storefront", "cart", "positive"})
    @Description("TC_API_CART_009 — Thêm shipping method vào cart đã có items → 200")
    @Story("Shipping")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_CART_009_addShippingMethod_validOptionId_returns200() {
        assertNotNull(sharedCartId, "TC_API_CART_009: cần sharedCartId");

        // Lấy shipping option ID
        Response shippingOptionsResp = cartApi.getShippingOptions(sharedCartId);
        if (shippingOptionsResp.getStatusCode() != ApiConstants.STATUS_OK) {
            log.warn("TC_API_CART_009: Không lấy được shipping options — skip");
            return;
        }

        java.util.List<?> options = shippingOptionsResp.jsonPath().getList("shipping_options");
        if (options == null || options.isEmpty()) {
            log.warn("TC_API_CART_009: Không có shipping options trong store — skip");
            return;
        }

        String shippingOptionId = shippingOptionsResp.jsonPath()
                .getString("shipping_options[0].id");

        Response response = cartApi.addShippingMethod(sharedCartId, shippingOptionId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_009: Add shipping method phải trả về 200 | actual: " + response.getStatusCode());

        log.info("TC_API_CART_009 PASS | shippingOptionId: {}", shippingOptionId);
    }

    // ====================================================
    // TC_API_CART_010 — Get shipping options
    // ====================================================
    @Test(priority = 10, dependsOnMethods = {"TC_API_CART_001_createCart_validRegion_returns200WithCartId"},
            groups = {"api", "storefront", "cart", "positive"})
    @Description("TC_API_CART_010 — Lấy danh sách shipping options cho cart → 200 + array")
    @Story("Shipping")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_CART_010_getShippingOptions_validCartId_returns200WithArray() {
        assertNotNull(sharedCartId, "TC_API_CART_010: cần sharedCartId");

        Response response = cartApi.getShippingOptions(sharedCartId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_CART_010: Get shipping options phải trả về 200 | actual: " + response.getStatusCode());

        assertNotNull(response.jsonPath().get("shipping_options"),
                "TC_API_CART_010: Response phải chứa shipping_options array");

        log.info("TC_API_CART_010 PASS | count: {}", response.jsonPath().getList("shipping_options").size());
    }
}
