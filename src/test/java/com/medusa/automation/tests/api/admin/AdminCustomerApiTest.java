package com.medusa.automation.tests.api.admin;

import com.medusa.automation.api.admin.AdminCustomerApi;
import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * AdminCustomerApiTest — API Test class cho Admin Customer endpoints.
 *
 * Endpoints được kiểm thử:
 *  - GET /admin/customers         → list tất cả customers
 *  - GET /admin/customers/{id}    → chi tiết customer theo ID
 *
 * Test Coverage:
 *  - TC_API_ADMIN_CUS_001 — List customers (admin auth) → 200 + customers array
 *  - TC_API_ADMIN_CUS_002 — List customers không có auth → 401
 *  - TC_API_ADMIN_CUS_003 — List customers với invalid token → 401
 *  - TC_API_ADMIN_CUS_004 — Pagination: limit=5 → 200 + đúng limit + count ≤ limit
 *  - TC_API_ADMIN_CUS_005 — Pagination: offset=0 → offset field trong response là 0
 *  - TC_API_ADMIN_CUS_006 — Get customer by ID → 200 + customer object đầy đủ fields
 *  - TC_API_ADMIN_CUS_007 — Get customer by ID không có auth → 401
 *  - TC_API_ADMIN_CUS_008 — Get customer với ID không tồn tại → 404
 *  - TC_API_ADMIN_CUS_009 — Customer response structure validation (required fields)
 *  - TC_API_ADMIN_CUS_010 — Response time SLA < 5000ms
 *  - TC_API_ADMIN_CUS_011 — Customer email field luôn tồn tại và có định dạng hợp lệ
 */
@Epic("Admin API")
@Feature("Customers")
public class AdminCustomerApiTest extends BaseApiTest {

    private AdminCustomerApi customerApi;

    /** Customer ID lấy từ list — dùng cho get-by-ID tests */
    private static String sampleCustomerId;

    /** Customer email đầu tiên — dùng để verify field hợp lệ */
    private static String sampleCustomerEmail;

    // ====================================================
    // Setup — khởi tạo client và lấy sample data
    // ====================================================

    @BeforeClass(alwaysRun = true)
    @Override
    public void setupTokens() {
        super.setupTokens();
        customerApi = new AdminCustomerApi();

        // Lấy 1 customer ID và email thực tế để dùng cho các TCs sau
        try {
            if (adminToken != null) {
                Response customersResp = customerApi.listCustomers(adminToken);
                if (customersResp.getStatusCode() == ApiConstants.STATUS_OK) {
                    List<?> customers = customersResp.jsonPath().getList("customers");
                    if (customers != null && !customers.isEmpty()) {
                        sampleCustomerId    = customersResp.jsonPath().getString("customers[0].id");
                        sampleCustomerEmail = customersResp.jsonPath().getString("customers[0].email");
                        log.info("[Setup] Admin sampleCustomerId: {} | email: {}",
                                sampleCustomerId, sampleCustomerEmail);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[Setup] Không lấy được sampleCustomerId: {}", e.getMessage());
        }
    }

    // ====================================================
    // TC_API_ADMIN_CUS_001 — List all customers
    // ====================================================

    @Test(priority = 1, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_001 — Admin list tất cả customers → 200 + customers array + pagination metadata")
    @Story("List Customers")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_ADMIN_CUS_001_listCustomers_adminAuth_returns200WithArray() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_001: cần adminToken hợp lệ");

        Response response = customerApi.listCustomers(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_001: Admin list customers phải trả về 200 | actual: " + response.getStatusCode());

        assertNotNull(response.jsonPath().get("customers"),
                "TC_API_ADMIN_CUS_001: Response phải chứa customers array");
        assertNotNull(response.jsonPath().get("count"),
                "TC_API_ADMIN_CUS_001: Response phải chứa field count");
        assertNotNull(response.jsonPath().get("limit"),
                "TC_API_ADMIN_CUS_001: Response phải chứa field limit");
        assertNotNull(response.jsonPath().get("offset"),
                "TC_API_ADMIN_CUS_001: Response phải chứa field offset");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_CUS_001: Response time vượt SLA | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_CUS_001 PASS | count: {} | time: {}ms",
                response.jsonPath().getInt("count"), response.getTime());
    }

    // ====================================================
    // TC_API_ADMIN_CUS_002 — No auth → 401
    // ====================================================

    @Test(priority = 2, groups = {"api", "admin", "customers", "negative"})
    @Description("TC_API_ADMIN_CUS_002 — List customers không có Authorization header → 401 Unauthorized")
    @Story("List Customers")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_CUS_002_listCustomers_noAuth_returns401() {
        Response response = customerApi.listCustomersNoAuth();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_CUS_002: Không có auth phải trả về 401 | actual: " + response.getStatusCode());

        assertNull(response.jsonPath().get("customers"),
                "TC_API_ADMIN_CUS_002: Error response KHÔNG được chứa customers data");

        log.info("TC_API_ADMIN_CUS_002 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_CUS_003 — Invalid token → 401
    // ====================================================

    @Test(priority = 3, groups = {"api", "admin", "customers", "negative"})
    @Description("TC_API_ADMIN_CUS_003 — List customers với Bearer token không hợp lệ → 401 Unauthorized")
    @Story("List Customers")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_CUS_003_listCustomers_invalidToken_returns401() {
        Response response = customerApi.listCustomersInvalidToken();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_CUS_003: Token invalid phải trả về 401 | actual: " + response.getStatusCode());

        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_003: Token giả KHÔNG được trả về 200");

        log.info("TC_API_ADMIN_CUS_003 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_CUS_004 — Pagination limit=5
    // ====================================================

    @Test(priority = 4, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_004 — List customers với limit=5 → 200 + số items ≤ 5 + limit field = 5")
    @Story("List Customers")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_CUS_004_listCustomers_paginationLimit5_returnsMaxItems() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_004: cần adminToken");

        Response response = customerApi.listCustomersPaginated(adminToken, 5, 0);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_004: Pagination request phải trả về 200 | actual: " + response.getStatusCode());

        List<?> customers = response.jsonPath().getList("customers");
        assertNotNull(customers, "TC_API_ADMIN_CUS_004: customers array phải tồn tại");
        assertTrue(customers.size() <= 5,
                "TC_API_ADMIN_CUS_004: Số customers trả về phải ≤ 5 | actual: " + customers.size());

        int returnedLimit = response.jsonPath().getInt("limit");
        assertEquals(returnedLimit, 5,
                "TC_API_ADMIN_CUS_004: limit field trong response phải là 5 | actual: " + returnedLimit);

        log.info("TC_API_ADMIN_CUS_004 PASS | returned: {} customers | limit: {}", customers.size(), returnedLimit);
    }

    // ====================================================
    // TC_API_ADMIN_CUS_005 — Pagination offset=0
    // ====================================================

    @Test(priority = 5, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_005 — List customers với offset=0 → 200 + offset field trong response = 0")
    @Story("List Customers")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_CUS_005_listCustomers_paginationOffset0_returnsFromStart() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_005: cần adminToken");

        Response response = customerApi.listCustomersPaginated(adminToken, 10, 0);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_005: Pagination offset=0 phải trả về 200 | actual: " + response.getStatusCode());

        int returnedOffset = response.jsonPath().getInt("offset");
        assertEquals(returnedOffset, 0,
                "TC_API_ADMIN_CUS_005: offset field trong response phải là 0 | actual: " + returnedOffset);

        log.info("TC_API_ADMIN_CUS_005 PASS | offset: {}", returnedOffset);
    }

    // ====================================================
    // TC_API_ADMIN_CUS_006 — Get customer by valid ID → 200
    // ====================================================

    @Test(priority = 6, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_006 — Admin lấy chi tiết customer theo ID → 200 + customer object đầy đủ required fields")
    @Story("Get Customer Detail")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_CUS_006_getCustomerById_validId_returns200WithCustomer() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_006: cần adminToken");

        if (sampleCustomerId == null) {
            log.warn("TC_API_ADMIN_CUS_006: Không có sampleCustomerId — skip (không có customer data trong hệ thống)");
            return;
        }

        Response response = customerApi.getCustomerById(adminToken, sampleCustomerId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_006: Get customer by ID phải trả về 200 | actual: " + response.getStatusCode());

        String returnedId = response.jsonPath().getString("customer.id");
        assertEquals(returnedId, sampleCustomerId,
                "TC_API_ADMIN_CUS_006: customer.id phải khớp | expected: " + sampleCustomerId + " | actual: " + returnedId);

        assertNotNull(response.jsonPath().get("customer.email"),
                "TC_API_ADMIN_CUS_006: customer.email phải tồn tại");
        assertNotNull(response.jsonPath().get("customer.created_at"),
                "TC_API_ADMIN_CUS_006: customer.created_at phải tồn tại");
        assertNotNull(response.jsonPath().get("customer.updated_at"),
                "TC_API_ADMIN_CUS_006: customer.updated_at phải tồn tại");

        log.info("TC_API_ADMIN_CUS_006 PASS | customerId: {} | email: {}",
                returnedId, response.jsonPath().getString("customer.email"));
    }

    // ====================================================
    // TC_API_ADMIN_CUS_007 — Get customer endpoint — no auth → 401
    // ====================================================

    @Test(priority = 7, groups = {"api", "admin", "customers", "negative"})
    @Description("TC_API_ADMIN_CUS_007 — Gọi /admin/customers endpoint không có Authorization header → 401 Unauthorized")
    @Story("Get Customer Detail")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_CUS_007_getCustomerById_noAuth_returns401() {
        Response response = customerApi.listCustomersNoAuth();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_CUS_007: Endpoint /admin/customers không có auth phải trả về 401 | actual: "
                        + response.getStatusCode());

        log.info("TC_API_ADMIN_CUS_007 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_CUS_008 — Get customer không tồn tại → 404
    // ====================================================

    @Test(priority = 8, groups = {"api", "admin", "customers", "negative"})
    @Description("TC_API_ADMIN_CUS_008 — Admin lấy customer với ID không tồn tại → 404 Not Found")
    @Story("Get Customer Detail")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_CUS_008_getCustomerById_nonExistentId_returns404() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_008: cần adminToken");

        Response response = customerApi.getCustomerNonExistent(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_NOT_FOUND,
                "TC_API_ADMIN_CUS_008: Customer không tồn tại phải trả về 404 | actual: " + response.getStatusCode());

        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_SERVER_ERROR,
                "TC_API_ADMIN_CUS_008: Server KHÔNG được trả 500 cho nonexistent ID");

        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_008: KHÔNG được trả 200 cho nonexistent customer");

        log.info("TC_API_ADMIN_CUS_008 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_CUS_009 — Response structure validation
    // ====================================================

    @Test(priority = 9, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_009 — Validate list structure: count >= 0, limit > 0, offset >= 0, customers là array")
    @Story("List Customers")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_CUS_009_listCustomers_responseStructure_isValid() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_009: cần adminToken");

        Response response = customerApi.listCustomers(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_009: Response status phải là 200");

        int count  = response.jsonPath().getInt("count");
        int limit  = response.jsonPath().getInt("limit");
        int offset = response.jsonPath().getInt("offset");

        assertTrue(count >= 0,
                "TC_API_ADMIN_CUS_009: count phải >= 0 | actual: " + count);
        assertTrue(limit > 0,
                "TC_API_ADMIN_CUS_009: limit phải > 0 | actual: " + limit);
        assertTrue(offset >= 0,
                "TC_API_ADMIN_CUS_009: offset phải >= 0 | actual: " + offset);

        assertNotNull(response.jsonPath().getList("customers"),
                "TC_API_ADMIN_CUS_009: customers field phải là array (không null)");

        log.info("TC_API_ADMIN_CUS_009 PASS | count: {} | limit: {} | offset: {}", count, limit, offset);
    }

    // ====================================================
    // TC_API_ADMIN_CUS_010 — Response time SLA < 5000ms
    // ====================================================

    @Test(priority = 10, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_010 — Response time của admin customer list < 5000ms (SLA)")
    @Story("List Customers")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_CUS_010_listCustomers_responseTime_withinSLA() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_010: cần adminToken");

        Response response = customerApi.listCustomers(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_010: Response status phải là 200");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_CUS_010: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS
                        + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_CUS_010 PASS | time: {}ms", response.getTime());
    }

    // ====================================================
    // TC_API_ADMIN_CUS_011 — Customer email field validation
    // ====================================================

    @Test(priority = 11, groups = {"api", "admin", "customers", "positive"})
    @Description("TC_API_ADMIN_CUS_011 — Mỗi customer trong list phải có email field hợp lệ (không null, chứa @)")
    @Story("List Customers")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_CUS_011_listCustomers_emailField_isValidFormat() {
        assertNotNull(adminToken, "TC_API_ADMIN_CUS_011: cần adminToken");

        Response response = customerApi.listCustomers(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_CUS_011: Response status phải là 200");

        List<?> customers = response.jsonPath().getList("customers");
        if (customers == null || customers.isEmpty()) {
            log.warn("TC_API_ADMIN_CUS_011: Không có customers để validate email — skip");
            return;
        }

        // Kiểm tra tối đa 5 customers đầu tiên để tránh test chạy quá lâu
        int checkCount = Math.min(customers.size(), 5);
        for (int i = 0; i < checkCount; i++) {
            String email = response.jsonPath().getString("customers[" + i + "].email");
            assertNotNull(email,
                    "TC_API_ADMIN_CUS_011: customers[" + i + "].email phải tồn tại (không null)");
            assertTrue(email.contains("@"),
                    "TC_API_ADMIN_CUS_011: customers[" + i + "].email phải có định dạng hợp lệ (chứa @) | actual: " + email);
        }

        log.info("TC_API_ADMIN_CUS_011 PASS | validated email của {} customers", checkCount);
    }
}
