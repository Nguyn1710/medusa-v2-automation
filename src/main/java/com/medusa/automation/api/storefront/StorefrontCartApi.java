package com.medusa.automation.api.storefront;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * StorefrontCartApi — API client cho Cart & Checkout flow của Storefront.
 *
 * Cart flow theo thứ tự:
 *  1. createCart(regionId)          → cartId
 *  2. addLineItem(cartId, ...)      → cart updated
 *  3. addShippingMethod(cartId, ...) → cart with shipping
 *  4. (Payment steps — scope ngoài class này)
 *
 * Tất cả methods trả về raw Response để test tự assert.
 */
public class StorefrontCartApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(StorefrontCartApi.class);

    // ====================================================
    // Cart CRUD
    // ====================================================

    /**
     * Tạo cart mới với region_id.
     * POST /store/carts
     *
     * @param regionId ID của region (lấy từ GET /store/regions)
     * @return Response chứa { cart: { id, ... } }
     */
    public Response createCart(String regionId) {
        log.info("[StorefrontCart] createCart | regionId: {}", regionId);
        Map<String, Object> body = new HashMap<>();
        if (regionId != null) {
            body.put("region_id", regionId);
        }

        Response response = withPublishableKey()
                .body(body)
                .post(ApiConstants.STORE_CARTS);

        logResponse(response, "createCart");
        return response;
    }

    /**
     * Tạo cart không có body (thiếu region_id) — dùng cho negative test.
     * POST /store/carts với body rỗng {}
     */
    public Response createCartEmpty() {
        log.info("[StorefrontCart] createCartEmpty (no body)");

        Response response = withPublishableKey()
                .body("{}")
                .post(ApiConstants.STORE_CARTS);

        logResponse(response, "createCartEmpty");
        return response;
    }

    /**
     * Lấy thông tin cart theo ID.
     * GET /store/carts/{id}
     *
     * @param cartId ID của cart
     * @return Response chứa cart object
     */
    public Response getCart(String cartId) {
        log.info("[StorefrontCart] getCart | cartId: {}", cartId);

        Response response = withPublishableKey()
                .pathParam("id", cartId)
                .get("/store/carts/{id}");

        logResponse(response, "getCart");
        return response;
    }

    /**
     * Lấy cart KHÔNG có publishable key — negative test.
     * GET /store/carts/{id} không có x-publishable-api-key header.
     *
     * @param cartId ID của cart
     * @return Response (expect 401)
     */
    public Response getCartNoAuth(String cartId) {
        log.info("[StorefrontCart] getCartNoAuth | cartId: {}", cartId);

        Response response = withNoAuth()
                .pathParam("id", cartId)
                .get("/store/carts/{id}");

        logResponse(response, "getCartNoAuth");
        return response;
    }

    // ====================================================
    // Line Items
    // ====================================================

    /**
     * Thêm sản phẩm vào cart.
     * POST /store/carts/{id}/line-items
     *
     * @param cartId    ID của cart
     * @param variantId ID của product variant
     * @param quantity  số lượng (phải > 0)
     * @return Response chứa cart updated
     */
    public Response addLineItem(String cartId, String variantId, int quantity) {
        log.info("[StorefrontCart] addLineItem | cartId: {} | variantId: {} | qty: {}",
                cartId, variantId, quantity);

        Map<String, Object> body = new HashMap<>();
        body.put("variant_id", variantId);
        body.put("quantity", quantity);

        Response response = withPublishableKey()
                .pathParam("id", cartId)
                .body(body)
                .post(ApiConstants.STORE_CART_LINE_ITEMS);

        logResponse(response, "addLineItem");
        return response;
    }

    /**
     * Thêm sản phẩm với quantity âm — negative test.
     */
    public Response addLineItemNegativeQty(String cartId, String variantId) {
        log.info("[StorefrontCart] addLineItemNegativeQty | cartId: {}", cartId);

        Map<String, Object> body = new HashMap<>();
        body.put("variant_id", variantId);
        body.put("quantity", -1);

        Response response = withPublishableKey()
                .pathParam("id", cartId)
                .body(body)
                .post(ApiConstants.STORE_CART_LINE_ITEMS);

        logResponse(response, "addLineItemNegativeQty");
        return response;
    }

    /**
     * Thêm sản phẩm thiếu variant_id — negative test.
     */
    public Response addLineItemMissingVariant(String cartId) {
        log.info("[StorefrontCart] addLineItemMissingVariant | cartId: {}", cartId);

        Map<String, Object> body = new HashMap<>();
        body.put("quantity", 1);
        // variant_id intentionally missing

        Response response = withPublishableKey()
                .pathParam("id", cartId)
                .body(body)
                .post(ApiConstants.STORE_CART_LINE_ITEMS);

        logResponse(response, "addLineItemMissingVariant");
        return response;
    }

    // ====================================================
    // Shipping Method
    // ====================================================

    /**
     * Thêm shipping method vào cart.
     * POST /store/carts/{id}/shipping-methods
     *
     * @param cartId           ID của cart
     * @param shippingOptionId ID của shipping option
     * @return Response chứa cart updated
     */
    public Response addShippingMethod(String cartId, String shippingOptionId) {
        log.info("[StorefrontCart] addShippingMethod | cartId: {} | optionId: {}",
                cartId, shippingOptionId);

        Map<String, Object> body = new HashMap<>();
        body.put("option_id", shippingOptionId);

        Response response = withPublishableKey()
                .pathParam("id", cartId)
                .body(body)
                .post(ApiConstants.STORE_CART_SHIPPING);

        logResponse(response, "addShippingMethod");
        return response;
    }

    // ====================================================
    // Helper endpoints (dùng để lấy data cho test setup)
    // ====================================================

    /**
     * Lấy danh sách regions — dùng trong @BeforeClass để lấy regionId.
     * GET /store/regions
     */
    public Response getRegions() {
        log.info("[StorefrontCart] getRegions");

        Response response = withPublishableKey()
                .get(ApiConstants.STORE_REGIONS);

        logResponse(response, "getRegions");
        return response;
    }

    /**
     * Lấy shipping options cho cart — dùng trong test để lấy shippingOptionId.
     * GET /store/shipping-options?cart_id={cartId}
     */
    public Response getShippingOptions(String cartId) {
        log.info("[StorefrontCart] getShippingOptions | cartId: {}", cartId);

        Response response = withPublishableKey()
                .queryParam("cart_id", cartId)
                .get(ApiConstants.STORE_SHIPPING_OPTIONS);

        logResponse(response, "getShippingOptions");
        return response;
    }

    /**
     * Lấy product variant ID đầu tiên từ catalog — dùng để setup test data.
     * GET /store/products
     */
    public Response getProducts() {
        log.info("[StorefrontCart] getProducts");

        Response response = withPublishableKey()
                .queryParam("limit", 1)
                .get(ApiConstants.STORE_PRODUCTS);

        logResponse(response, "getProducts");
        return response;
    }
}
