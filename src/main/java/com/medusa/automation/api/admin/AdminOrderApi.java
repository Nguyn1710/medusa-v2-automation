package com.medusa.automation.api.admin;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AdminOrderApi — API client cho Admin Order endpoints.
 *
 * Endpoints:
 *  - GET /admin/orders          → list tất cả orders
 *  - GET /admin/orders/{id}     → chi tiết order theo ID
 *
 * ⚠️ Tất cả endpoints yêu cầu Admin Bearer token.
 */
public class AdminOrderApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(AdminOrderApi.class);

    // ====================================================
    // List Orders
    // ====================================================

    /**
     * Lấy danh sách tất cả orders — admin view.
     * GET /admin/orders
     * Requires: Admin Bearer token
     *
     * @param adminToken Admin JWT token
     * @return Response chứa { orders: [...], count, limit, offset }
     */
    public Response listOrders(String adminToken) {
        log.info("[AdminOrder] listOrders");

        Response response = withBearerToken(adminToken)
                .get(ApiConstants.ADMIN_ORDERS);

        logResponse(response, "listOrders");
        return response;
    }

    /**
     * Lấy danh sách orders với pagination.
     * GET /admin/orders?limit={limit}&offset={offset}
     *
     * @param adminToken Admin JWT token
     * @param limit      số records mỗi trang (default 20 theo Medusa)
     * @param offset     vị trí bắt đầu
     * @return Response
     */
    public Response listOrdersPaginated(String adminToken, int limit, int offset) {
        log.info("[AdminOrder] listOrdersPaginated | limit: {} | offset: {}", limit, offset);

        Response response = withBearerToken(adminToken)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .get(ApiConstants.ADMIN_ORDERS);

        logResponse(response, "listOrdersPaginated");
        return response;
    }

    /**
     * Gọi list orders không có token — negative test.
     * GET /admin/orders không có Authorization header.
     *
     * @return Response (expect 401)
     */
    public Response listOrdersNoAuth() {
        log.info("[AdminOrder] listOrdersNoAuth (expect 401)");

        Response response = withNoAuth()
                .get(ApiConstants.ADMIN_ORDERS);

        logResponse(response, "listOrdersNoAuth");
        return response;
    }

    /**
     * Gọi list orders với token invalid — negative test.
     *
     * @return Response (expect 401)
     */
    public Response listOrdersInvalidToken() {
        log.info("[AdminOrder] listOrdersInvalidToken (expect 401)");

        Response response = withBearerToken("invalid.jwt.token.value")
                .get(ApiConstants.ADMIN_ORDERS);

        logResponse(response, "listOrdersInvalidToken");
        return response;
    }

    // ====================================================
    // Get Order by ID
    // ====================================================

    /**
     * Lấy chi tiết order theo ID.
     * GET /admin/orders/{id}
     * Requires: Admin Bearer token
     *
     * @param adminToken Admin JWT token
     * @param orderId    ID của order
     * @return Response chứa { order: {...} }
     */
    public Response getOrderById(String adminToken, String orderId) {
        log.info("[AdminOrder] getOrderById | orderId: {}", orderId);

        Response response = withBearerToken(adminToken)
                .pathParam("id", orderId)
                .get(ApiConstants.ADMIN_ORDER_BY_ID);

        logResponse(response, "getOrderById");
        return response;
    }

    /**
     * Lấy order với ID không tồn tại — negative test.
     * GET /admin/orders/nonexistent_id
     *
     * @param adminToken Admin JWT token
     * @return Response (expect 404)
     */
    public Response getOrderNonExistent(String adminToken) {
        String fakeId = "order_fake_" + System.currentTimeMillis();
        log.info("[AdminOrder] getOrderNonExistent | fakeId: {}", fakeId);

        Response response = withBearerToken(adminToken)
                .pathParam("id", fakeId)
                .get(ApiConstants.ADMIN_ORDER_BY_ID);

        logResponse(response, "getOrderNonExistent");
        return response;
    }

    /**
     * Lấy order theo ID không có auth — negative test.
     *
     * @param orderId ID của order
     * @return Response (expect 401)
     */
    public Response getOrderByIdNoAuth(String orderId) {
        log.info("[AdminOrder] getOrderByIdNoAuth | orderId: {}", orderId);

        Response response = withNoAuth()
                .pathParam("id", orderId)
                .get(ApiConstants.ADMIN_ORDER_BY_ID);

        logResponse(response, "getOrderByIdNoAuth");
        return response;
    }
}
