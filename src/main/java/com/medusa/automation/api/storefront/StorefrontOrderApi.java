package com.medusa.automation.api.storefront;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * StorefrontOrderApi — API client cho Order endpoints của Storefront.
 *
 * Endpoints:
 *  - GET /store/orders          → danh sách đơn hàng của customer đang login
 *  - GET /store/orders/{id}     → chi tiết đơn hàng theo ID
 *
 * ⚠️ Tất cả order endpoints yêu cầu Bearer token (x-authenticated: true).
 */
public class StorefrontOrderApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(StorefrontOrderApi.class);

    // ====================================================
    // Order endpoints
    // ====================================================

    /**
     * Lấy danh sách đơn hàng của customer đang đăng nhập.
     * GET /store/orders
     * Requires: Bearer token
     *
     * @param customerToken JWT token của customer
     * @return Response chứa { orders: [...], count, limit, offset }
     */
    public Response getOrders(String customerToken) {
        log.info("[StorefrontOrder] getOrders | token present: {}", customerToken != null);

        Response response = withBearerAndPublishableKey(customerToken)
                .get(ApiConstants.STORE_ORDERS);

        logResponse(response, "getOrders");
        return response;
    }

    /**
     * Lấy đơn hàng với pagination.
     * GET /store/orders?limit={limit}&offset={offset}
     *
     * @param customerToken JWT token
     * @param limit         số records trên 1 trang
     * @param offset        bắt đầu từ vị trí nào
     * @return Response
     */
    public Response getOrdersPaginated(String customerToken, int limit, int offset) {
        log.info("[StorefrontOrder] getOrdersPaginated | limit: {} | offset: {}", limit, offset);

        Response response = withBearerAndPublishableKey(customerToken)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .get(ApiConstants.STORE_ORDERS);

        logResponse(response, "getOrdersPaginated");
        return response;
    }

    /**
     * Gọi GET /store/orders mà không có Authorization header.
     * Dùng cho negative test — expect 401.
     *
     * @return Response (expect 401)
     */
    public Response getOrdersNoAuth() {
        log.info("[StorefrontOrder] getOrdersNoAuth (expect 401)");

        Response response = withPublishableKey()
                .get(ApiConstants.STORE_ORDERS);

        logResponse(response, "getOrdersNoAuth");
        return response;
    }

    /**
     * Lấy chi tiết đơn hàng theo ID.
     * GET /store/orders/{id}
     * Requires: Bearer token
     *
     * @param customerToken JWT token
     * @param orderId       ID của order
     * @return Response chứa { order: {...} }
     */
    public Response getOrderById(String customerToken, String orderId) {
        log.info("[StorefrontOrder] getOrderById | orderId: {}", orderId);

        Response response = withBearerAndPublishableKey(customerToken)
                .pathParam("id", orderId)
                .get(ApiConstants.STORE_ORDER_BY_ID);

        logResponse(response, "getOrderById");
        return response;
    }

    /**
     * Lấy order theo ID không có auth — negative test.
     *
     * @param orderId ID của order
     * @return Response (expect 401)
     */
    public Response getOrderByIdNoAuth(String orderId) {
        log.info("[StorefrontOrder] getOrderByIdNoAuth | orderId: {}", orderId);

        Response response = withPublishableKey()
                .pathParam("id", orderId)
                .get(ApiConstants.STORE_ORDER_BY_ID);

        logResponse(response, "getOrderByIdNoAuth");
        return response;
    }

    /**
     * Lấy order với ID không tồn tại — negative test (expect 404).
     *
     * @param customerToken JWT token
     * @return Response (expect 404)
     */
    public Response getOrderNonExistent(String customerToken) {
        String fakeId = "order_nonexistent_" + System.currentTimeMillis();
        log.info("[StorefrontOrder] getOrderNonExistent | fakeId: {}", fakeId);

        Response response = withBearerAndPublishableKey(customerToken)
                .pathParam("id", fakeId)
                .get(ApiConstants.STORE_ORDER_BY_ID);

        logResponse(response, "getOrderNonExistent");
        return response;
    }
}
