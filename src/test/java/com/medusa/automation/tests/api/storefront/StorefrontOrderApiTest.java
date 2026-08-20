package com.medusa.automation.tests.api.storefront;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.storefront.StorefrontOrderApi;
import com.medusa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * StorefrontOrderApiTest — API Test class cho Storefront Order endpoints.
 *
 * Test Coverage:
 *  - TC_API_ORDER_001 — Lấy danh sách orders của customer đã login → 200
 *  - TC_API_ORDER_002 — Lấy orders không có auth → 401
 *  - TC_API_ORDER_003 — Lấy orders với Bearer token không hợp lệ → 401
 *  - TC_API_ORDER_004 — Pagination: limit=5 → 200 + count ≤ 5
 *  - TC_API_ORDER_005 — Lấy order by ID (authenticated) → 200
 *  - TC_API_ORDER_006 — Lấy order by ID chỉ publishable key → 200 (public endpoint)
 *  - TC_API_ORDER_007 — Lấy order ID không tồn tại → 400/404
 *  - TC_API_ORDER_008 — Response structure validation cho order list
 *  - TC_API_ORDER_009 — Response time SLA cho order list
 */
@Epic("Storefront API")
@Feature("Orders")
public class StorefrontOrderApiTest extends BaseApiTest {

    private StorefrontOrderApi orderApi;

    /** Order ID lấy từ danh sách để dùng cho get-by-ID tests */
    private static String sampleOrderId;

    @BeforeClass(alwaysRun = true)
    @Override
    public void setupTokens() {
        super.setupTokens();
        orderApi = new StorefrontOrderApi();

        // Lấy sẵn 1 order ID để dùng cho TC_005, TC_006
        try {
            if (customerToken != null) {
                Response ordersResp = orderApi.getOrders(customerToken);
                if (ordersResp.getStatusCode() == ApiConstants.STATUS_OK) {
                    List<?> orders = ordersResp.jsonPath().getList("orders");
                    if (orders != null && !orders.isEmpty()) {
                        sampleOrderId = ordersResp.jsonPath().getString("orders[0].id");
                        log.info("[Setup] sampleOrderId: {}", sampleOrderId);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[Setup] Không lấy được sampleOrderId: {}", e.getMessage());
        }
    }

    // ====================================================
    // TC_API_ORDER_001 — List orders (authenticated)
    // ====================================================
    @Test(priority = 1, groups = {"api", "storefront", "orders", "positive"})
    @Description("TC_API_ORDER_001 — Lấy danh sách orders của customer đã login → 200 + orders array")
    @Story("List Orders")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_ORDER_001_getOrders_authenticated_returns200WithArray() {
        assertNotNull(customerToken, "TC_API_ORDER_001: cần customerToken hợp lệ");

        Response response = orderApi.getOrders(customerToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_001: Get orders phải trả về 200 | actual: " + response.getStatusCode());

        // Verify pagination metadata
        assertNotNull(response.jsonPath().get("orders"),
                "TC_API_ORDER_001: Response phải chứa orders field");
        assertNotNull(response.jsonPath().get("count"),
                "TC_API_ORDER_001: Response phải chứa count field");
        assertNotNull(response.jsonPath().get("limit"),
                "TC_API_ORDER_001: Response phải chứa limit field");
        assertNotNull(response.jsonPath().get("offset"),
                "TC_API_ORDER_001: Response phải chứa offset field");

        log.info("TC_API_ORDER_001 PASS | count: {} | time: {}ms",
                response.jsonPath().get("count"), response.getTime());
    }

    // ====================================================
    // TC_API_ORDER_002 — List orders without auth
    // ====================================================
    @Test(priority = 2, groups = {"api", "storefront", "orders", "negative"})
    @Description("TC_API_ORDER_002 — Lấy orders không có Authorization header → 401")
    @Story("List Orders")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ORDER_002_getOrders_noAuth_returns401() {
        Response response = orderApi.getOrdersNoAuth();

        // Storefront /store/orders không có Bearer token — Medusa trả 401
        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ORDER_002: Không có auth phải trả về 401 | actual: " + response.getStatusCode());

        // Không được có orders data trong error response
        assertNull(response.jsonPath().get("orders"),
                "TC_API_ORDER_002: Error response KHÔNG được chứa orders data");

        log.info("TC_API_ORDER_002 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ORDER_003 — Invalid token
    // ====================================================
    @Test(priority = 3, groups = {"api", "storefront", "orders", "negative"})
    @Description("TC_API_ORDER_003 — Lấy orders với Bearer token không hợp lệ → 401")
    @Story("List Orders")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ORDER_003_getOrders_invalidToken_returns401() {
        Response response = orderApi.getOrders("invalid.bearer.token.value");

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ORDER_003: Token không hợp lệ phải trả về 401 | actual: " + response.getStatusCode());

        log.info("TC_API_ORDER_003 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ORDER_004 — Pagination
    // ====================================================
    @Test(priority = 4, groups = {"api", "storefront", "orders", "positive"})
    @Description("TC_API_ORDER_004 — Lấy orders với limit=5 → 200 + số items ≤ 5")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ORDER_004_getOrders_paginationLimit5_returns200WithMaxItems() {
        assertNotNull(customerToken, "TC_API_ORDER_004: cần customerToken");

        Response response = orderApi.getOrdersPaginated(customerToken, 5, 0);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_004: Pagination request phải trả về 200 | actual: " + response.getStatusCode());

        List<?> orders = response.jsonPath().getList("orders");
        assertNotNull(orders, "TC_API_ORDER_004: orders phải tồn tại");
        assertTrue(orders.size() <= 5,
                "TC_API_ORDER_004: orders count phải ≤ 5 | actual: " + orders.size());

        // Verify limit field trong response khớp với request
        int returnedLimit = response.jsonPath().getInt("limit");
        assertEquals(returnedLimit, 5,
                "TC_API_ORDER_004: limit trong response phải là 5 | actual: " + returnedLimit);

        log.info("TC_API_ORDER_004 PASS | returned: {} items | limit: {}", orders.size(), returnedLimit);
    }

    // ====================================================
    // TC_API_ORDER_005 — Get order by ID (authenticated)
    // ====================================================
    @Test(priority = 5, groups = {"api", "storefront", "orders", "positive"})
    @Description("TC_API_ORDER_005 — Lấy chi tiết order by ID (có Bearer token) → 200 + order object")
    @Story("Get Order Detail")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ORDER_005_getOrderById_authenticated_returns200() {
        assertNotNull(customerToken, "TC_API_ORDER_005: cần customerToken");

        if (sampleOrderId == null) {
            log.warn("TC_API_ORDER_005: Không có sampleOrderId (customer chưa có order) — skip");
            return;
        }

        Response response = orderApi.getOrderById(customerToken, sampleOrderId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_005: Get order by ID phải trả về 200 | actual: " + response.getStatusCode());

        String returnedId = response.jsonPath().getString("order.id");
        assertEquals(returnedId, sampleOrderId,
                "TC_API_ORDER_005: order.id phải khớp với request | expected: " + sampleOrderId);

        // Verify required fields trong order response
        assertNotNull(response.jsonPath().get("order.status"),
                "TC_API_ORDER_005: order.status phải tồn tại");
        assertNotNull(response.jsonPath().get("order.email"),
                "TC_API_ORDER_005: order.email phải tồn tại");

        log.info("TC_API_ORDER_005 PASS | orderId: {}", returnedId);
    }

    // ====================================================
    // TC_API_ORDER_006 — Get order by ID (publishable key only — public endpoint)
    // ====================================================
    @Test(priority = 6, groups = {"api", "storefront", "orders", "positive"})
    @Description("TC_API_ORDER_006 — Lấy order bằng publishable key (không cần Bearer) → 200 (public endpoint by design)")
    @Story("Get Order Detail")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ORDER_006_getOrderById_publishableKeyOnly_returns200() {
        // KNOWN BEHAVIOR: GET /store/orders/{id} là public endpoint (x-authenticated: false)
        // Chỉ cần publishable key, không cần Bearer token
        // Ref: openapi spec line 5859: x-authenticated: false
        if (sampleOrderId == null) {
            log.warn("TC_API_ORDER_006: Không có sampleOrderId — skip");
            return;
        }

        Response response = orderApi.getOrderByIdNoAuth(sampleOrderId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_006: Public order endpoint với pub key phải trả 200 | actual: "
                        + response.getStatusCode());

        log.info("TC_API_ORDER_006 PASS | KNOWN: /store/orders/{id} là public endpoint theo spec");
    }

    // ====================================================
    // TC_API_ORDER_007 — Order not found
    // ====================================================
    @Test(priority = 7, groups = {"api", "storefront", "orders", "negative"})
    @Description("TC_API_ORDER_007 — Lấy order với ID không tồn tại → 400/404")
    @Story("Get Order Detail")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ORDER_007_getOrderById_nonExistentId_returnsError() {
        assertNotNull(customerToken, "TC_API_ORDER_007: cần customerToken");

        Response response = orderApi.getOrderNonExistent(customerToken);

        // Medusa có thể trả 400 (invalid format) hoặc 404 (not found)
        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST
                        || response.getStatusCode() == ApiConstants.STATUS_NOT_FOUND,
                "TC_API_ORDER_007: Order không tồn tại phải trả 400 hoặc 404 | actual: "
                        + response.getStatusCode());

        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_007: Order không tồn tại KHÔNG được trả 200");
        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_SERVER_ERROR,
                "TC_API_ORDER_007: Server KHÔNG được trả 500");

        log.info("TC_API_ORDER_007 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ORDER_008 — Response structure validation
    // ====================================================
    @Test(priority = 8, groups = {"api", "storefront", "orders", "positive"})
    @Description("TC_API_ORDER_008 — Validate response structure của order list: có orders array, count, limit, offset")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ORDER_008_getOrders_responseStructure_isValid() {
        assertNotNull(customerToken, "TC_API_ORDER_008: cần customerToken");

        Response response = orderApi.getOrders(customerToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_008: Response status phải là 200");

        // Validate pagination envelope
        int count = response.jsonPath().getInt("count");
        int limit = response.jsonPath().getInt("limit");
        int offset = response.jsonPath().getInt("offset");

        assertTrue(count >= 0, "TC_API_ORDER_008: count phải >= 0");
        assertTrue(limit > 0, "TC_API_ORDER_008: limit phải > 0");
        assertTrue(offset >= 0, "TC_API_ORDER_008: offset phải >= 0");

        log.info("TC_API_ORDER_008 PASS | count: {} | limit: {} | offset: {}", count, limit, offset);
    }

    // ====================================================
    // TC_API_ORDER_009 — Response time SLA
    // ====================================================
    @Test(priority = 9, groups = {"api", "storefront", "orders", "positive"})
    @Description("TC_API_ORDER_009 — Response time của order list endpoint phải < 5000ms (SLA)")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ORDER_009_getOrders_responseTime_withinSLA() {
        assertNotNull(customerToken, "TC_API_ORDER_009: cần customerToken");

        Response response = orderApi.getOrders(customerToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ORDER_009: Response status phải là 200");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ORDER_009: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS
                        + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_ORDER_009 PASS | time: {}ms (SLA: {}ms)", response.getTime(), ApiConstants.MAX_RESPONSE_TIME_MS);
    }
}
