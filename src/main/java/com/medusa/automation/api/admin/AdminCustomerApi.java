package com.medusa.automation.api.admin;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AdminCustomerApi — API client cho Admin Customer endpoints.
 *
 * Endpoints:
 *  - GET /admin/customers         → list tất cả customers
 *  - GET /admin/customers/{id}    → chi tiết customer theo ID
 *
 * ⚠️ Tất cả endpoints yêu cầu Admin Bearer token.
 */
public class AdminCustomerApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(AdminCustomerApi.class);

    // ====================================================
    // List Customers
    // ====================================================

    /**
     * Lấy danh sách tất cả customers — admin view.
     * GET /admin/customers
     *
     * @param adminToken Admin JWT token
     * @return Response chứa { customers: [...], count, limit, offset }
     */
    public Response listCustomers(String adminToken) {
        log.info("[AdminCustomer] listCustomers");

        Response response = withBearerToken(adminToken)
                .get(ApiConstants.ADMIN_CUSTOMERS);

        logResponse(response, "listCustomers");
        return response;
    }

    /**
     * Lấy customers với pagination.
     * GET /admin/customers?limit={limit}&offset={offset}
     *
     * @param adminToken Admin JWT token
     * @param limit      số records mỗi trang
     * @param offset     vị trí bắt đầu
     * @return Response
     */
    public Response listCustomersPaginated(String adminToken, int limit, int offset) {
        log.info("[AdminCustomer] listCustomersPaginated | limit: {} | offset: {}", limit, offset);

        Response response = withBearerToken(adminToken)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .get(ApiConstants.ADMIN_CUSTOMERS);

        logResponse(response, "listCustomersPaginated");
        return response;
    }

    /**
     * Gọi list customers không có token — negative test.
     *
     * @return Response (expect 401)
     */
    public Response listCustomersNoAuth() {
        log.info("[AdminCustomer] listCustomersNoAuth (expect 401)");

        Response response = withNoAuth()
                .get(ApiConstants.ADMIN_CUSTOMERS);

        logResponse(response, "listCustomersNoAuth");
        return response;
    }

    /**
     * Lấy customers với invalid token — negative test.
     *
     * @return Response (expect 401)
     */
    public Response listCustomersInvalidToken() {
        log.info("[AdminCustomer] listCustomersInvalidToken (expect 401)");

        Response response = withBearerToken("invalid.jwt.token")
                .get(ApiConstants.ADMIN_CUSTOMERS);

        logResponse(response, "listCustomersInvalidToken");
        return response;
    }

    // ====================================================
    // Get Customer by ID
    // ====================================================

    /**
     * Lấy chi tiết customer theo ID.
     * GET /admin/customers/{id}
     *
     * @param adminToken Admin JWT token
     * @param customerId ID của customer
     * @return Response chứa { customer: {...} }
     */
    public Response getCustomerById(String adminToken, String customerId) {
        log.info("[AdminCustomer] getCustomerById | customerId: {}", customerId);

        Response response = withBearerToken(adminToken)
                .pathParam("id", customerId)
                .get(ApiConstants.ADMIN_CUSTOMERS + "/{id}");

        logResponse(response, "getCustomerById");
        return response;
    }

    /**
     * Lấy customer với ID không tồn tại — negative test.
     *
     * @param adminToken Admin JWT token
     * @return Response (expect 404)
     */
    public Response getCustomerNonExistent(String adminToken) {
        String fakeId = "cus_nonexistent_" + System.currentTimeMillis();
        log.info("[AdminCustomer] getCustomerNonExistent | fakeId: {}", fakeId);

        Response response = withBearerToken(adminToken)
                .pathParam("id", fakeId)
                .get(ApiConstants.ADMIN_CUSTOMERS + "/{id}");

        logResponse(response, "getCustomerNonExistent");
        return response;
    }
}
