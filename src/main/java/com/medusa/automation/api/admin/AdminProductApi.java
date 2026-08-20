package com.medusa.automation.api.admin;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AdminProductApi — API client cho Admin Product endpoints.
 *
 * Endpoints:
 *  - GET /admin/products          → list tất cả products
 *  - GET /admin/products/{id}     → chi tiết product theo ID
 *
 * ⚠️ Tất cả endpoints yêu cầu Admin Bearer token.
 */
public class AdminProductApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(AdminProductApi.class);

    // ====================================================
    // List Products
    // ====================================================

    /**
     * Lấy danh sách tất cả products — admin view.
     * GET /admin/products
     *
     * @param adminToken Admin JWT token
     * @return Response chứa { products: [...], count, limit, offset }
     */
    public Response listProducts(String adminToken) {
        log.info("[AdminProduct] listProducts");

        Response response = withBearerToken(adminToken)
                .get(ApiConstants.ADMIN_PRODUCTS);

        logResponse(response, "listProducts");
        return response;
    }

    /**
     * Lấy products với pagination.
     * GET /admin/products?limit={limit}&offset={offset}
     *
     * @param adminToken Admin JWT token
     * @param limit      số records mỗi trang
     * @param offset     vị trí bắt đầu
     * @return Response
     */
    public Response listProductsPaginated(String adminToken, int limit, int offset) {
        log.info("[AdminProduct] listProductsPaginated | limit: {} | offset: {}", limit, offset);

        Response response = withBearerToken(adminToken)
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .get(ApiConstants.ADMIN_PRODUCTS);

        logResponse(response, "listProductsPaginated");
        return response;
    }

    /**
     * Lọc products theo title.
     * GET /admin/products?title={title}
     *
     * @param adminToken Admin JWT token
     * @param title      title để filter
     * @return Response
     */
    public Response listProductsByTitle(String adminToken, String title) {
        log.info("[AdminProduct] listProductsByTitle | title: {}", title);

        Response response = withBearerToken(adminToken)
                .queryParam("title", title)
                .get(ApiConstants.ADMIN_PRODUCTS);

        logResponse(response, "listProductsByTitle");
        return response;
    }

    /**
     * Gọi list products không có token — negative test.
     * GET /admin/products không có Authorization header.
     *
     * @return Response (expect 401)
     */
    public Response listProductsNoAuth() {
        log.info("[AdminProduct] listProductsNoAuth (expect 401)");

        Response response = withNoAuth()
                .get(ApiConstants.ADMIN_PRODUCTS);

        logResponse(response, "listProductsNoAuth");
        return response;
    }

    /**
     * Gọi list products với token invalid — negative test.
     *
     * @return Response (expect 401)
     */
    public Response listProductsInvalidToken() {
        log.info("[AdminProduct] listProductsInvalidToken (expect 401)");

        Response response = withBearerToken("invalid.jwt.token.value")
                .get(ApiConstants.ADMIN_PRODUCTS);

        logResponse(response, "listProductsInvalidToken");
        return response;
    }

    // ====================================================
    // Get Product by ID
    // ====================================================

    /**
     * Lấy chi tiết product theo ID.
     * GET /admin/products/{id}
     *
     * @param adminToken Admin JWT token
     * @param productId  ID của product
     * @return Response chứa { product: {...} }
     */
    public Response getProductById(String adminToken, String productId) {
        log.info("[AdminProduct] getProductById | productId: {}", productId);

        Response response = withBearerToken(adminToken)
                .pathParam("id", productId)
                .get(ApiConstants.ADMIN_PRODUCTS + "/{id}");

        logResponse(response, "getProductById");
        return response;
    }

    /**
     * Lấy product với ID không tồn tại — negative test.
     *
     * @param adminToken Admin JWT token
     * @return Response (expect 404)
     */
    public Response getProductNonExistent(String adminToken) {
        String fakeId = "prod_nonexistent_" + System.currentTimeMillis();
        log.info("[AdminProduct] getProductNonExistent | fakeId: {}", fakeId);

        Response response = withBearerToken(adminToken)
                .pathParam("id", fakeId)
                .get(ApiConstants.ADMIN_PRODUCTS + "/{id}");

        logResponse(response, "getProductNonExistent");
        return response;
    }
}
