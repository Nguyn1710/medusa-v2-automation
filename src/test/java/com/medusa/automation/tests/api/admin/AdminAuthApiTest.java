package com.medusa.automation.tests.api.admin;

import com.medusa.automation.api.admin.AdminAuthApi;
import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.base.BaseApiTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * AdminAuthApiTest — API Test class cho Admin Authentication.
 *
 * Covers:
 *  - TC_API_ADMIN_AUTH_001 — Admin login thành công → 200 + token
 *  - TC_API_ADMIN_AUTH_002 — Admin login sai password → 401
 *  - TC_API_ADMIN_AUTH_003 — Admin login email không tồn tại → 401
 *  - TC_API_ADMIN_AUTH_004 — Admin login thiếu email field → 400/422
 *  - TC_API_ADMIN_AUTH_005 — Admin login thiếu password field → 400/422
 *  - TC_API_ADMIN_AUTH_006 — Admin login body hoàn toàn rỗng → 400/422
 */
@Epic("Admin API")
@Feature("Authentication")
public class AdminAuthApiTest extends BaseApiTest {

    private AdminAuthApi adminAuthApi;

    @BeforeClass(alwaysRun = true)
    @Override
    public void setupTokens() {
        super.setupTokens();
        adminAuthApi = new AdminAuthApi();
    }

    // ====================================================
    // HAPPY PATH
    // ====================================================

    @Test(priority = 1, groups = {"api", "admin", "auth", "positive"})
    @Description("TC_API_ADMIN_AUTH_001 — Admin login thành công với credentials hợp lệ → 200 + JWT token")
    @Story("Admin Login")
    @Severity(SeverityLevel.BLOCKER)
    public void TC_API_ADMIN_AUTH_001_login_validCredentials_returns200WithToken() {
        Response response = adminAuthApi.loginRaw(
                config.getAdminEmail(),
                config.getAdminPassword()
        );

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_OK,
                "TC_API_ADMIN_AUTH_001: Admin login hợp lệ phải trả về 200");
        assertNotNull(response.jsonPath().getString("token"),
                "TC_API_ADMIN_AUTH_001: Response phải chứa token field");
        assertFalse(response.jsonPath().getString("token").isEmpty(),
                "TC_API_ADMIN_AUTH_001: Token không được rỗng");

        // Response time SLA
        assertTrue(response.getTime() < ApiConstants.MAX_RESPONSE_TIME_MS,
                "TC_API_ADMIN_AUTH_001: Response time vượt SLA " + ApiConstants.MAX_RESPONSE_TIME_MS + "ms | actual: " + response.getTime() + "ms");

        log.info("TC_API_ADMIN_AUTH_001 PASS | time: {}ms", response.getTime());
    }

    // ====================================================
    // NEGATIVE — WRONG CREDENTIALS
    // ====================================================

    @Test(priority = 2, groups = {"api", "admin", "auth", "negative"})
    @Description("TC_API_ADMIN_AUTH_002 — Admin login sai password → 401 Unauthorized")
    @Story("Admin Login")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_AUTH_002_login_wrongPassword_returns401() {
        Response response = adminAuthApi.loginWrongPassword(config.getAdminEmail());

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_AUTH_002: Sai password phải trả về 401 | actual: " + response.getStatusCode());

        // Đảm bảo response KHÔNG chứa token khi thất bại
        assertNull(response.jsonPath().getString("token"),
                "TC_API_ADMIN_AUTH_002: Response thất bại KHÔNG được chứa token");

        log.info("TC_API_ADMIN_AUTH_002 PASS | status: {}", response.getStatusCode());
    }

    @Test(priority = 3, groups = {"api", "admin", "auth", "negative"})
    @Description("TC_API_ADMIN_AUTH_003 — Admin login với email không tồn tại → 401")
    @Story("Admin Login")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_AUTH_003_login_nonExistentEmail_returns401() {
        Response response = adminAuthApi.loginWrongEmail(config.getAdminPassword());

        assertEquals(response.getStatusCode(), ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_AUTH_003: Email không tồn tại phải trả về 401 | actual: " + response.getStatusCode());

        log.info("TC_API_ADMIN_AUTH_003 PASS | status: {}", response.getStatusCode());
    }

    // ====================================================
    // NEGATIVE — MISSING FIELDS
    // ====================================================

    @Test(priority = 4, groups = {"api", "admin", "auth", "negative"})
    @Description("TC_API_ADMIN_AUTH_004 — Admin login thiếu email field → 400/422")
    @Story("Admin Login")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_AUTH_004_login_missingEmail_returns400() {
        Response response = adminAuthApi.loginMissingEmail(config.getAdminPassword());

        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST
                || response.getStatusCode() == ApiConstants.STATUS_UNPROCESSABLE
                || response.getStatusCode() == ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_AUTH_004: Thiếu email phải trả về 400/422/401 | actual: " + response.getStatusCode()
        );

        log.info("TC_API_ADMIN_AUTH_004 PASS | status: {}", response.getStatusCode());
    }

    @Test(priority = 5, groups = {"api", "admin", "auth", "negative"})
    @Description("TC_API_ADMIN_AUTH_005 — Admin login thiếu password field → 400/422")
    @Story("Admin Login")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_API_ADMIN_AUTH_005_login_missingPassword_returns400() {
        Response response = adminAuthApi.loginMissingPassword(config.getAdminEmail());

        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST
                || response.getStatusCode() == ApiConstants.STATUS_UNPROCESSABLE
                || response.getStatusCode() == ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_AUTH_005: Thiếu password phải trả về 400/422/401 | actual: " + response.getStatusCode()
        );

        log.info("TC_API_ADMIN_AUTH_005 PASS | status: {}", response.getStatusCode());
    }

    @Test(priority = 6, groups = {"api", "admin", "auth", "negative"})
    @Description("TC_API_ADMIN_AUTH_006 — Admin login với body hoàn toàn rỗng → 400/422")
    @Story("Admin Login")
    @Severity(SeverityLevel.NORMAL)
    public void TC_API_ADMIN_AUTH_006_login_emptyBody_returns400() {
        Response response = adminAuthApi.loginEmptyBody();

        assertTrue(
                response.getStatusCode() == ApiConstants.STATUS_BAD_REQUEST
                || response.getStatusCode() == ApiConstants.STATUS_UNPROCESSABLE
                || response.getStatusCode() == ApiConstants.STATUS_UNAUTHORIZED,
                "TC_API_ADMIN_AUTH_006: Body rỗng phải trả về 400/422/401 | actual: " + response.getStatusCode()
        );
        assertNotEquals(response.getStatusCode(), ApiConstants.STATUS_SERVER_ERROR,
                "TC_API_ADMIN_AUTH_006: Server KHÔNG được trả về 500");

        log.info("TC_API_ADMIN_AUTH_006 PASS | status: {}", response.getStatusCode());
    }
}
