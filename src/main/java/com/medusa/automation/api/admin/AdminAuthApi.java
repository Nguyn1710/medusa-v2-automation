package com.medusa.automation.api.admin;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * AdminAuthApi — API client cho Admin Authentication endpoints.
 *
 * Endpoints:
 *  - POST /auth/user/emailpass  → Admin login, trả về JWT token
 *
 * Lưu ý: Admin API dùng route prefix /auth/user/ thay vì /auth/customer/
 */
public class AdminAuthApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(AdminAuthApi.class);

    // ====================================================
    // Admin Auth Methods
    // ====================================================

    /**
     * Admin login — trả về raw Response.
     * POST /auth/user/emailpass
     *
     * @param email    admin email
     * @param password admin password
     * @return Response chứa { token: "..." } nếu 200
     */
    public Response loginRaw(String email, String password) {
        log.info("[AdminAuth] loginRaw | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.ADMIN_AUTH_LOGIN);

        logResponse(response, "adminLogin");
        return response;
    }

    /**
     * Admin login — trả về JWT token string (dùng cho setup fixtures).
     * Trả về null nếu login thất bại (không throw exception).
     *
     * @param email    admin email
     * @param password admin password
     * @return JWT token string, hoặc null nếu thất bại
     */
    public String login(String email, String password) {
        Response response = loginRaw(email, password);
        if (response.getStatusCode() != ApiConstants.STATUS_OK) {
            log.warn("Admin login thất bại | status: {} | body: {}",
                    response.getStatusCode(), response.getBody().asString());
            return null;
        }
        String token = response.jsonPath().getString("token");
        log.info("[AdminAuth] Admin login OK, token extracted");
        return token;
    }

    /**
     * Admin login với email sai — negative test.
     * POST /auth/user/emailpass với email không tồn tại.
     *
     * @return Response (expect 401)
     */
    public Response loginWrongEmail(String password) {
        log.info("[AdminAuth] loginWrongEmail (expect 401)");
        Map<String, Object> body = new HashMap<>();
        body.put("email", "wrong_user_" + System.currentTimeMillis() + "@invalid.com");
        body.put("password", password);

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.ADMIN_AUTH_LOGIN);

        logResponse(response, "loginWrongEmail");
        return response;
    }

    /**
     * Admin login với password sai — negative test.
     *
     * @param email admin email đúng
     * @return Response (expect 401)
     */
    public Response loginWrongPassword(String email) {
        log.info("[AdminAuth] loginWrongPassword | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "WrongPassword_999!");

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.ADMIN_AUTH_LOGIN);

        logResponse(response, "loginWrongPassword");
        return response;
    }

    /**
     * Admin login thiếu email field — negative test.
     *
     * @return Response (expect 400/422)
     */
    public Response loginMissingEmail(String password) {
        log.info("[AdminAuth] loginMissingEmail (expect 400/422)");
        Map<String, Object> body = new HashMap<>();
        body.put("password", password);
        // email field intentionally missing

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.ADMIN_AUTH_LOGIN);

        logResponse(response, "loginMissingEmail");
        return response;
    }

    /**
     * Admin login thiếu password field — negative test.
     *
     * @param email admin email
     * @return Response (expect 400/422)
     */
    public Response loginMissingPassword(String email) {
        log.info("[AdminAuth] loginMissingPassword | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        // password field intentionally missing

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.ADMIN_AUTH_LOGIN);

        logResponse(response, "loginMissingPassword");
        return response;
    }

    /**
     * Admin login với body hoàn toàn rỗng — negative test.
     *
     * @return Response (expect 400/422)
     */
    public Response loginEmptyBody() {
        log.info("[AdminAuth] loginEmptyBody (expect 400/422)");

        Response response = withNoAuth()
                .body("{}")
                .post(ApiConstants.ADMIN_AUTH_LOGIN);

        logResponse(response, "loginEmptyBody");
        return response;
    }
}
