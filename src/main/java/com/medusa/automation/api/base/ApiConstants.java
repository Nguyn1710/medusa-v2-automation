package com.medusa.automation.api.base;

/**
 * ApiConstants — hằng số dùng chung cho tất cả API test classes.
 *
 * Tập trung paths, headers, status codes vào 1 nơi:
 * - Dễ cập nhật khi API thay đổi
 * - Không hardcode string rải rác trong test
 */
public final class ApiConstants {

    private ApiConstants() {}

    // ====================================================
    // HTTP Headers
    // ====================================================
    public static final String HEADER_CONTENT_TYPE     = "Content-Type";
    public static final String HEADER_AUTHORIZATION    = "Authorization";
    public static final String HEADER_PUBLISHABLE_KEY  = "x-publishable-api-key";
    public static final String CONTENT_TYPE_JSON       = "application/json";

    // ====================================================
    // HTTP Status Codes
    // ====================================================
    public static final int STATUS_OK          = 200;
    public static final int STATUS_CREATED     = 201;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_UNAUTHORIZED= 401;
    public static final int STATUS_FORBIDDEN   = 403;
    public static final int STATUS_NOT_FOUND   = 404;
    public static final int STATUS_CONFLICT    = 409;
    public static final int STATUS_UNPROCESSABLE = 422;
    public static final int STATUS_SERVER_ERROR  = 500;

    // ====================================================
    // Storefront API — Endpoint Paths
    // ====================================================
    /** POST /auth/customer/emailpass/register — lấy registration token */
    public static final String STORE_AUTH_REGISTER     = "/auth/customer/emailpass/register";

    /** POST /auth/customer/emailpass — login customer */
    public static final String STORE_AUTH_LOGIN        = "/auth/customer/emailpass";

    /** POST /auth/customer/emailpass/reset-password — tạo reset pw token */
    public static final String STORE_AUTH_RESET_PW     = "/auth/customer/emailpass/reset-password";

    /** POST /auth/customer/emailpass/update — reset password bằng token */
    public static final String STORE_AUTH_UPDATE_PW    = "/auth/customer/emailpass/update";

    /** POST /store/customers — tạo customer profile (sau khi có register token) */
    public static final String STORE_CUSTOMERS         = "/store/customers";

    /** GET /store/customers/me — lấy thông tin customer đang login */
    public static final String STORE_CUSTOMERS_ME      = "/store/customers/me";

    /** POST /store/carts — tạo cart mới */
    public static final String STORE_CARTS             = "/store/carts";

    /** POST /store/carts/{id}/line-items — thêm sản phẩm vào cart */
    public static final String STORE_CART_LINE_ITEMS   = "/store/carts/{id}/line-items";

    /** POST /store/carts/{id}/shipping-methods — set shipping */
    public static final String STORE_CART_SHIPPING     = "/store/carts/{id}/shipping-methods";

    /** GET /store/orders — lấy danh sách đơn hàng */
    public static final String STORE_ORDERS            = "/store/orders";

    /** GET /store/orders/{id} — lấy chi tiết đơn hàng */
    public static final String STORE_ORDER_BY_ID       = "/store/orders/{id}";

    /** GET /store/regions — lấy danh sách regions */
    public static final String STORE_REGIONS           = "/store/regions";

    /** GET /store/products — lấy danh sách sản phẩm */
    public static final String STORE_PRODUCTS          = "/store/products";

    /** GET /store/shipping-options — lấy shipping options cho cart */
    public static final String STORE_SHIPPING_OPTIONS  = "/store/shipping-options";

    // ====================================================
    // Admin API — Endpoint Paths
    // ====================================================
    /** POST /auth/user/emailpass — Admin login */
    public static final String ADMIN_AUTH_LOGIN        = "/auth/user/emailpass";

    /** GET /admin/orders — list orders (admin) */
    public static final String ADMIN_ORDERS            = "/admin/orders";

    /** GET /admin/orders/{id} — get order by ID (admin) */
    public static final String ADMIN_ORDER_BY_ID       = "/admin/orders/{id}";

    /** GET /admin/products — list products (admin) */
    public static final String ADMIN_PRODUCTS          = "/admin/products";

    /** GET /admin/customers — list customers (admin) */
    public static final String ADMIN_CUSTOMERS         = "/admin/customers";

    // ====================================================
    // Timeouts & Limits
    // ====================================================
    /** Response time threshold (ms) — SLA threshold cho assertion */
    public static final long MAX_RESPONSE_TIME_MS = 5000L;

    /** Default pagination limit */
    public static final int DEFAULT_PAGE_LIMIT = 10;
}
