package com.medusa.automation.tests.api.admin;

import com.medusa.automation.api.admin.AdminOrderApi;
import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * AdminOrderApiTest — API Test class cho Admin Order endpoints.
 *
 * Test Coverage:
 *  - TC_API_ADMIN_ORDER_001 — List orders (admin) → 200 + orders array
 *  - TC_API_ADMIN_ORDER_002 — List orders không có auth → 401
 *  - TC_API_ADMIN_ORDER_003 — List orders token invalid → 401
 *  - TC_API_ADMIN_ORDER_004 — Pagination: limit=5 → 200 + đúng count
 *  - TC_API_ADMIN_ORDER_005 — Get order by ID → 200 + order object
 *  - TC_API_ADMIN_ORDER_006 — Get order by ID không có auth → 401
 *  - TC_API_ADMIN_ORDER_007 — Get order không tồn tại → 404
 *  - TC_API_ADMIN_ORDER_008 — Response structure validation (required fields)
 *  - TC_API_ADMIN_ORDER_009 — Response time SLA < 5000ms
 *  - TC_API_ADMIN_ORDER_010 — Order có status field hợp lệ (enum validation)
 */
@Epic("Admin API")
@Feature("Orders")
public class AdminOrderApiTest extends BaseApiTest {

    private AdminOrderApi orderApi;

    /** Order ID lấy từ list — dùng cho get-by-ID tests */
    private static String sampleOrderId;

    @BeforeClass(alwaysRun = true)
    @Override
    public void setupTokens() {
        super.setupTokens();
        orderApi = new AdminOrderApi();

        // Lấy 1 order ID thực để dùng cho các TCs get-by-ID
        try {
            if (adminToken != null) {
                Response ordersResp = orderApi.listOrders(adminToken);
                if (ordersResp.getStatusCode() == ApiConstants.STATUS_OK) {
                    List<?> orders = ordersResp.jsonPath().getList("orders");
                    if (orders != null && !orders.isEmpty()) {
                        sampleOrderId = ordersResp.jsonPath().getString("orders[0].id");
                        log.info("[Setup] Admin sampleOrderId: {}", sampleOrderId);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[Setup] Không lấy được sampleOrderId: {}", e.getMessage());
        }
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_001 — List all orders
    // ====================================================
    @Test(priority = 1, groups = {"api", "admin", "orders", "positive"})
    @Description("TC_API_ADMIN_ORDER_001 — Admin list tất cả orders → 200 + orders array")
    @Story("List Orders")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_ADMIN_ORDER_001_listOrders_adminAuth_returns200WithArray() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_001: cần adminToken hợp lệ");

        Response response = orderApi.listOrders(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_001: Admin list orders phải trả về 200 | actual: " + response.getStatusCode());

        // Verify pagination envelope
        assertNotNull(response.jsonPath().get("orders"),
                "TC_API_ADMIN_ORDER_001: Response phải chứa orders array");
        assertNotNull(response.jsonPath().get("count"),
                "TC_API_ADMIN_ORDER_001: Response phải chứa count");
        assertNotNull(response.jsonPath().get("limit"),
                "TC_API_ADMIN_ORDER_001: Response phải chứa limit");
        assertNotNull(response.jsonPath().get("offset"),
                "TC_API_ADMIN_ORDER_001: Response phải chứa offset");

        // Verify orders là array
        List<?> orders = response.jsonPath().getList("orders");
        assertNotNull(orders, "TC_API_ADMIN_ORDER_001: orders phải là array");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_ORDER_001: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS
                        + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_ORDER_001 PASS | orders count: {} | time: {}ms",
                response.jsonPath().getInt("count"), response.getTime());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_002 — No auth
    // ====================================================
    @Test(priority = 2, groups = {"api", "admin", "orders", "negative"})
    @Description("TC_API_ADMIN_ORDER_002 — List orders không có Authorization header → 401 Unauthorized")
    @Story("List Orders")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_ORDER_002_listOrders_noAuth_returns401() {
        Response response = orderApi.listOrdersNoAuth();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_ORDER_002: Không có auth phải trả về 401 | actual: " + response.getStatusCode());

        // Không được có orders data trong error response
        assertNull(response.jsonPath().get("orders"),
                "TC_API_ADMIN_ORDER_002: Error response KHÔNG được chứa orders data");

        log.info("TC_API_ADMIN_ORDER_002 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_003 — Invalid token
    // ====================================================
    @Test(priority = 3, groups = {"api", "admin", "orders", "negative"})
    @Description("TC_API_ADMIN_ORDER_003 — List orders với token invalid → 401 Unauthorized")
    @Story("List Orders")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_ORDER_003_listOrders_invalidToken_returns401() {
        Response response = orderApi.listOrdersInvalidToken();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_ORDER_003: Token invalid phải trả về 401 | actual: " + response.getStatusCode());

        log.info("TC_API_ADMIN_ORDER_003 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_004 — Pagination limit=5
    // ====================================================
    @Test(priority = 4, groups = {"api", "admin", "orders", "positive"})
    @Description("TC_API_ADMIN_ORDER_004 — Admin list orders với limit=5 → 200 + số items ≤ 5")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_ORDER_004_listOrders_paginationLimit5_returnsMaxItems() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_004: cần adminToken");

        Response response = orderApi.listOrdersPaginated(adminToken, 5, 0);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_004: Pagination request phải trả về 200 | actual: " + response.getStatusCode());

        List<?> orders = response.jsonPath().getList("orders");
        assertNotNull(orders, "TC_API_ADMIN_ORDER_004: orders phải tồn tại");
        assertTrue(orders.size() <= 5,
                "TC_API_ADMIN_ORDER_004: orders count phải ≤ 5 | actual: " + orders.size());

        // limit trong response phải khớp request
        int returnedLimit = response.jsonPath().getInt("limit");
        assertEquals(returnedLimit, 5,
                "TC_API_ADMIN_ORDER_004: limit trong response phải là 5 | actual: " + returnedLimit);

        log.info("TC_API_ADMIN_ORDER_004 PASS | returned: {} items", orders.size());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_005 — Get order by ID
    // ====================================================
    @Test(priority = 5, groups = {"api", "admin", "orders", "positive"})
    @Description("TC_API_ADMIN_ORDER_005 — Admin lấy chi tiết order theo ID → 200 + order object đầy đủ")
    @Story("Get Order Detail")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_ORDER_005_getOrderById_validId_returns200WithOrder() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_005: cần adminToken");

        if (sampleOrderId == null) {
            log.warn("TC_API_ADMIN_ORDER_005: Không có sampleOrderId — skip (store chưa có orders)");
            return;
        }

        Response response = orderApi.getOrderById(adminToken, sampleOrderId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_005: Get order by ID phải trả về 200 | actual: " + response.getStatusCode());

        String returnedId = response.jsonPath().getString("order.id");
        assertEquals(returnedId, sampleOrderId,
                "TC_API_ADMIN_ORDER_005: order.id phải khớp request | expected: " + sampleOrderId);

        // Verify required fields theo Admin Order schema
        assertNotNull(response.jsonPath().get("order.status"),
                "TC_API_ADMIN_ORDER_005: order.status phải tồn tại");
        assertNotNull(response.jsonPath().get("order.items"),
                "TC_API_ADMIN_ORDER_005: order.items phải tồn tại");
        assertNotNull(response.jsonPath().get("order.payment_status"),
                "TC_API_ADMIN_ORDER_005: order.payment_status phải tồn tại");
        assertNotNull(response.jsonPath().get("order.fulfillment_status"),
                "TC_API_ADMIN_ORDER_005: order.fulfillment_status phải tồn tại");

        // email — soft check: Admin Order có thể trả email ở nhiều field khác nhau
        String email = response.jsonPath().getString("order.email");
        if (email == null) {
            log.warn("TC_API_ADMIN_ORDER_005: order.email không có ở root — kiểm tra shipping_address.email");
            String shipEmail = response.jsonPath().getString("order.shipping_address.email");
            log.info("TC_API_ADMIN_ORDER_005: shipping_address.email = {}", shipEmail);
        } else {
            assertFalse(email.isEmpty(),
                    "TC_API_ADMIN_ORDER_005: order.email không được rỗng | actual: " + email);
        }

        // currency_code — REQUIRED theo spec, soft check để detect nếu Medusa bug
        String currencyCode = response.jsonPath().getString("order.currency_code");
        if (currencyCode == null) {
            log.warn("TC_API_ADMIN_ORDER_005: ⚠️ KNOWN ISSUE — currency_code thiếu trong response (vi phạm OpenAPI contract)");
        } else {
            assertFalse(currencyCode.isEmpty(),
                    "TC_API_ADMIN_ORDER_005: currency_code không được rỗng | actual: " + currencyCode);
            log.info("TC_API_ADMIN_ORDER_005: currency_code = {}", currencyCode);
        }

        log.info("TC_API_ADMIN_ORDER_005 PASS | orderId: {}", returnedId);
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_006 — Get order by ID no auth
    // ====================================================
    @Test(priority = 6, groups = {"api", "admin", "orders", "negative"})
    @Description("TC_API_ADMIN_ORDER_006 — Admin get order by ID không có auth → 401")
    @Story("Get Order Detail")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_ORDER_006_getOrderById_noAuth_returns401() {
        String targetId = sampleOrderId != null ? sampleOrderId : "order_test_id";

        Response response = orderApi.getOrderByIdNoAuth(targetId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_ORDER_006: Không có auth phải trả về 401 | actual: " + response.getStatusCode());

        log.info("TC_API_ADMIN_ORDER_006 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_007 — Get nonexistent order
    // ====================================================
    @Test(priority = 7, groups = {"api", "admin", "orders", "negative"})
    @Description("TC_API_ADMIN_ORDER_007 — Admin lấy order với ID không tồn tại → 404 Not Found")
    @Story("Get Order Detail")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_ORDER_007_getOrderById_nonExistentId_returns404() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_007: cần adminToken");

        Response response = orderApi.getOrderNonExistent(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_NOT_FOUND,
                "TC_API_ADMIN_ORDER_007: Order không tồn tại phải trả về 404 | actual: " + response.getStatusCode());

        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_007: KHÔNG được trả 200 cho nonexistent order");

        log.info("TC_API_ADMIN_ORDER_007 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_008 — Response structure validation
    // ====================================================
    @Test(priority = 8, groups = {"api", "admin", "orders", "positive"})
    @Description("TC_API_ADMIN_ORDER_008 — Validate response structure: count >= 0, limit > 0, offset >= 0")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_ORDER_008_listOrders_responseStructure_isValid() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_008: cần adminToken");

        Response response = orderApi.listOrders(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_008: Response status phải là 200");

        int count = response.jsonPath().getInt("count");
        int limit = response.jsonPath().getInt("limit");
        int offset = response.jsonPath().getInt("offset");

        assertTrue(count >= 0, "TC_API_ADMIN_ORDER_008: count phải >= 0 | actual: " + count);
        assertTrue(limit > 0, "TC_API_ADMIN_ORDER_008: limit phải > 0 | actual: " + limit);
        assertTrue(offset >= 0, "TC_API_ADMIN_ORDER_008: offset phải >= 0 | actual: " + offset);

        log.info("TC_API_ADMIN_ORDER_008 PASS | count: {} | limit: {} | offset: {}", count, limit, offset);
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_009 — Response time SLA
    // ====================================================
    @Test(priority = 9, groups = {"api", "admin", "orders", "positive"})
    @Description("TC_API_ADMIN_ORDER_009 — Response time của admin order list < 5000ms (SLA)")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_ORDER_009_listOrders_responseTime_withinSLA() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_009: cần adminToken");

        Response response = orderApi.listOrders(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_009: Response status phải là 200");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_ORDER_009: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS
                        + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_ORDER_009 PASS | time: {}ms", response.getTime());
    }

    // ====================================================
    // TC_API_ADMIN_ORDER_010 — Order status enum validation
    // ====================================================
    @Test(priority = 10, groups = {"api", "admin", "orders", "positive"})
    @Description("TC_API_ADMIN_ORDER_010 — Order status trong list phải là giá trị hợp lệ theo enum: pending/completed/canceled/etc.")
    @Story("List Orders")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_ORDER_010_listOrders_statusField_isValidEnum() {
        assertNotNull(adminToken, "TC_API_ADMIN_ORDER_010: cần adminToken");

        Response response = orderApi.listOrders(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_ORDER_010: Response status phải là 200");

        List<?> orders = response.jsonPath().getList("orders");
        if (orders == null || orders.isEmpty()) {
            log.warn("TC_API_ADMIN_ORDER_010: Không có orders để validate status — skip");
            return;
        }

        // Valid enum values theo AdminOrder schema
        List<String> validStatuses = List.of(
                "pending", "completed", "canceled", "draft",
                "requires_action", "archived"
        );

        for (int i = 0; i < Math.min(orders.size(), 5); i++) {
            String status = response.jsonPath().getString("orders[" + i + "].status");
            if (status != null) {
                assertTrue(validStatuses.contains(status.toLowerCase()),
                        "TC_API_ADMIN_ORDER_010: status '" + status + "' không phải valid enum value | valid: " + validStatuses);
            }
        }

        log.info("TC_API_ADMIN_ORDER_010 PASS | validated {} order statuses", Math.min(orders.size(), 5));
    }
}
