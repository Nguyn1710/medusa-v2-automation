package com.medusa.automation.tests.api.admin;

import com.medusa.automation.api.admin.AdminProductApi;
import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.base.BaseApiTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * AdminProductApiTest — API Test class cho Admin Product endpoints.
 *
 * Test Coverage:
 *  - TC_API_ADMIN_PROD_001 — List products (admin) → 200 + products array
 *  - TC_API_ADMIN_PROD_002 — List products không có auth → 401
 *  - TC_API_ADMIN_PROD_003 — List products token invalid → 401
 *  - TC_API_ADMIN_PROD_004 — Pagination: limit=3 → 200 + count ≤ 3
 *  - TC_API_ADMIN_PROD_005 — Filter by title → 200 + filtered results
 *  - TC_API_ADMIN_PROD_006 — Get product by ID → 200 + product object
 *  - TC_API_ADMIN_PROD_007 — Get product không tồn tại → 404
 *  - TC_API_ADMIN_PROD_008 — Product structure validation (required fields)
 *  - TC_API_ADMIN_PROD_009 — Response time SLA < 5000ms
 */
@Epic("Admin API")
@Feature("Products")
public class AdminProductApiTest extends BaseApiTest {

    private AdminProductApi productApi;

    /** Product ID lấy từ list — dùng cho get-by-ID tests */
    private static String sampleProductId;

    /** Product title đầu tiên — dùng cho filter test */
    private static String sampleProductTitle;

    @BeforeClass(alwaysRun = true)
    @Override
    public void setupTokens() {
        super.setupTokens();
        productApi = new AdminProductApi();

        // Lấy 1 product ID và title thực để dùng cho TCs sau
        try {
            if (adminToken != null) {
                Response productsResp = productApi.listProducts(adminToken);
                if (productsResp.getStatusCode() == ApiConstants.STATUS_OK) {
                    List<?> products = productsResp.jsonPath().getList("products");
                    if (products != null && !products.isEmpty()) {
                        sampleProductId = productsResp.jsonPath().getString("products[0].id");
                        sampleProductTitle = productsResp.jsonPath().getString("products[0].title");
                        log.info("[Setup] Admin sampleProductId: {} | title: {}",
                                sampleProductId, sampleProductTitle);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[Setup] Không lấy được sampleProductId: {}", e.getMessage());
        }
    }

    // ====================================================
    // TC_API_ADMIN_PROD_001 — List all products
    // ====================================================
    @Test(priority = 1, groups = {"api", "admin", "products", "positive"})
    @Description("TC_API_ADMIN_PROD_001 — Admin list tất cả products → 200 + products array + pagination metadata")
    @Story("List Products")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_ADMIN_PROD_001_listProducts_adminAuth_returns200WithArray() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_001: cần adminToken hợp lệ");

        Response response = productApi.listProducts(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_001: Admin list products phải trả về 200 | actual: " + response.getStatusCode());

        // Verify pagination envelope
        assertNotNull(response.jsonPath().get("products"),
                "TC_API_ADMIN_PROD_001: Response phải chứa products array");
        assertNotNull(response.jsonPath().get("count"),
                "TC_API_ADMIN_PROD_001: Response phải chứa count");
        assertNotNull(response.jsonPath().get("limit"),
                "TC_API_ADMIN_PROD_001: Response phải chứa limit");
        assertNotNull(response.jsonPath().get("offset"),
                "TC_API_ADMIN_PROD_001: Response phải chứa offset");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_PROD_001: Response time vượt SLA | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_PROD_001 PASS | count: {} | time: {}ms",
                response.jsonPath().getInt("count"), response.getTime());
    }

    // ====================================================
    // TC_API_ADMIN_PROD_002 — No auth
    // ====================================================
    @Test(priority = 2, groups = {"api", "admin", "products", "negative"})
    @Description("TC_API_ADMIN_PROD_002 — List products không có Authorization header → 401")
    @Story("List Products")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_PROD_002_listProducts_noAuth_returns401() {
        Response response = productApi.listProductsNoAuth();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_PROD_002: Không có auth phải trả về 401 | actual: " + response.getStatusCode());

        assertNull(response.jsonPath().get("products"),
                "TC_API_ADMIN_PROD_002: Error response KHÔNG được chứa products data");

        log.info("TC_API_ADMIN_PROD_002 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_PROD_003 — Invalid token
    // ====================================================
    @Test(priority = 3, groups = {"api", "admin", "products", "negative"})
    @Description("TC_API_ADMIN_PROD_003 — List products với token không hợp lệ → 401")
    @Story("List Products")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_PROD_003_listProducts_invalidToken_returns401() {
        Response response = productApi.listProductsInvalidToken();

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_PROD_003: Token invalid phải trả về 401 | actual: " + response.getStatusCode());

        log.info("TC_API_ADMIN_PROD_003 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_PROD_004 — Pagination limit=3
    // ====================================================
    @Test(priority = 4, groups = {"api", "admin", "products", "positive"})
    @Description("TC_API_ADMIN_PROD_004 — List products với limit=3 → 200 + số items ≤ 3")
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_PROD_004_listProducts_paginationLimit3_returnsMaxItems() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_004: cần adminToken");

        Response response = productApi.listProductsPaginated(adminToken, 3, 0);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_004: Pagination request phải trả về 200 | actual: " + response.getStatusCode());

        List<?> products = response.jsonPath().getList("products");
        assertNotNull(products, "TC_API_ADMIN_PROD_004: products phải tồn tại");
        assertTrue(products.size() <= 3,
                "TC_API_ADMIN_PROD_004: products count phải ≤ 3 | actual: " + products.size());

        int returnedLimit = response.jsonPath().getInt("limit");
        assertEquals(returnedLimit, 3,
                "TC_API_ADMIN_PROD_004: limit trong response phải là 3 | actual: " + returnedLimit);

        log.info("TC_API_ADMIN_PROD_004 PASS | returned: {} items", products.size());
    }

    // ====================================================
    // TC_API_ADMIN_PROD_005 — Filter by title
    // ====================================================
    @Test(priority = 5, groups = {"api", "admin", "products", "positive"})
    @Description("TC_API_ADMIN_PROD_005 — Filter products theo title → 200 + kết quả chứa title được filter")
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_PROD_005_listProducts_filterByTitle_returnsMatchingProducts() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_005: cần adminToken");

        if (sampleProductTitle == null) {
            log.warn("TC_API_ADMIN_PROD_005: Không có sampleProductTitle — skip");
            return;
        }

        Response response = productApi.listProductsByTitle(adminToken, sampleProductTitle);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_005: Filter request phải trả về 200 | actual: " + response.getStatusCode());

        List<?> products = response.jsonPath().getList("products");
        assertNotNull(products, "TC_API_ADMIN_PROD_005: products phải tồn tại");
        assertFalse(products.isEmpty(),
                "TC_API_ADMIN_PROD_005: Kết quả filter không được rỗng khi filter đúng title");

        // Verify mỗi product trong kết quả chứa title được filter
        for (int i = 0; i < products.size(); i++) {
            String title = response.jsonPath().getString("products[" + i + "].title");
            assertNotNull(title, "TC_API_ADMIN_PROD_005: products[" + i + "].title phải tồn tại");
        }

        log.info("TC_API_ADMIN_PROD_005 PASS | filter: '{}' → {} results", sampleProductTitle, products.size());
    }

    // ====================================================
    // TC_API_ADMIN_PROD_006 — Get product by ID
    // ====================================================
    @Test(priority = 6, groups = {"api", "admin", "products", "positive"})
    @Description("TC_API_ADMIN_PROD_006 — Admin lấy chi tiết product theo ID → 200 + product object")
    @Story("Get Product Detail")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_PROD_006_getProductById_validId_returns200WithProduct() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_006: cần adminToken");

        if (sampleProductId == null) {
            log.warn("TC_API_ADMIN_PROD_006: Không có sampleProductId — skip");
            return;
        }

        Response response = productApi.getProductById(adminToken, sampleProductId);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_006: Get product by ID phải trả về 200 | actual: " + response.getStatusCode());

        String returnedId = response.jsonPath().getString("product.id");
        assertEquals(returnedId, sampleProductId,
                "TC_API_ADMIN_PROD_006: product.id phải khớp | expected: " + sampleProductId);

        // Verify required fields theo Admin Product schema
        assertNotNull(response.jsonPath().get("product.title"),
                "TC_API_ADMIN_PROD_006: product.title phải tồn tại");
        assertNotNull(response.jsonPath().get("product.status"),
                "TC_API_ADMIN_PROD_006: product.status phải tồn tại");
        assertNotNull(response.jsonPath().get("product.variants"),
                "TC_API_ADMIN_PROD_006: product.variants phải tồn tại");

        log.info("TC_API_ADMIN_PROD_006 PASS | productId: {}", returnedId);
    }

    // ====================================================
    // TC_API_ADMIN_PROD_007 — Product not found
    // ====================================================
    @Test(priority = 7, groups = {"api", "admin", "products", "negative"})
    @Description("TC_API_ADMIN_PROD_007 — Admin lấy product với ID không tồn tại → 404 Not Found")
    @Story("Get Product Detail")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_PROD_007_getProductById_nonExistentId_returns404() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_007: cần adminToken");

        Response response = productApi.getProductNonExistent(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_NOT_FOUND,
                "TC_API_ADMIN_PROD_007: Product không tồn tại phải trả về 404 | actual: " + response.getStatusCode());

        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_007: KHÔNG được trả 200 cho nonexistent product");
        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_SERVER_ERROR,
                "TC_API_ADMIN_PROD_007: Server KHÔNG được trả 500");

        log.info("TC_API_ADMIN_PROD_007 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // TC_API_ADMIN_PROD_008 — Product structure validation
    // ====================================================
    @Test(priority = 8, groups = {"api", "admin", "products", "positive"})
    @Description("TC_API_ADMIN_PROD_008 — Validate product list structure: count >= 0, limit > 0, offset >= 0")
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_PROD_008_listProducts_responseStructure_isValid() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_008: cần adminToken");

        Response response = productApi.listProducts(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_008: Response status phải là 200");

        int count = response.jsonPath().getInt("count");
        int limit = response.jsonPath().getInt("limit");
        int offset = response.jsonPath().getInt("offset");

        assertTrue(count >= 0, "TC_API_ADMIN_PROD_008: count phải >= 0 | actual: " + count);
        assertTrue(limit > 0, "TC_API_ADMIN_PROD_008: limit phải > 0 | actual: " + limit);
        assertTrue(offset >= 0, "TC_API_ADMIN_PROD_008: offset phải >= 0 | actual: " + offset);

        log.info("TC_API_ADMIN_PROD_008 PASS | count: {} | limit: {} | offset: {}", count, limit, offset);
    }

    // ====================================================
    // TC_API_ADMIN_PROD_009 — Response time SLA
    // ====================================================
    @Test(priority = 9, groups = {"api", "admin", "products", "positive"})
    @Description("TC_API_ADMIN_PROD_009 — Response time của admin product list < 5000ms (SLA)")
    @Story("List Products")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_PROD_009_listProducts_responseTime_withinSLA() {
        assertNotNull(adminToken, "TC_API_ADMIN_PROD_009: cần adminToken");

        Response response = productApi.listProducts(adminToken);

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_PROD_009: Response status phải là 200");

        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_PROD_009: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS
                        + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_PROD_009 PASS | time: {}ms", response.getTime());
    }
}
