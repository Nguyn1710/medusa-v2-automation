package com.medusa.automation.api.storefront;

import com.medusa.automation.api.base.ApiConstants;
import com.medusa.automation.api.base.BaseApiClient;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * StorefrontAuthApi — API client cho tất cả Authentication endpoints của Storefront.
 *
 * Endpoints được wrap:
 *  - POST /auth/customer/emailpass/register  → lấy registration token
 *  - POST /auth/customer/emailpass           → login, trả về JWT
 *  - POST /auth/customer/emailpass/reset-password → gửi reset pw
 *  - POST /auth/customer/emailpass/update    → cập nhật password mới
 *  - POST /store/customers                   → tạo customer profile
 *
 * Mỗi method trả về raw Response để test class tự assert linh hoạt.
 */
public class StorefrontAuthApi extends BaseApiClient {

    private static final Logger log = LogManager.getLogger(StorefrontAuthApi.class);

    // ====================================================
    // Public API Methods — trả về raw Response
    // ====================================================

    /**
     * Bước 1 của Register flow: lấy registration JWT token.
     * POST /auth/customer/emailpass/register
     *
     * @param email    email của customer mới
     * @param password password của customer mới
     * @return Response chứa token field
     */
    public Response registerToken(String email, String password) {
        log.info("[StorefrontAuth] registerToken | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.STORE_AUTH_REGISTER);

        logResponse(response, "registerToken");
        return response;
    }

    /**
     * Bước 2 của Register flow: tạo customer profile với registration token.
     * POST /store/customers
     * Header: Authorization: Bearer {registrationToken}
     *
     * @param registrationToken token từ bước registerToken()
     * @param email             email của customer
     * @param firstName         tên
     * @param lastName          họ
     * @return Response chứa customer object
     */
    public Response createCustomer(String registrationToken, String email,
                                   String firstName, String lastName) {
        log.info("[StorefrontAuth] createCustomer | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("first_name", firstName);
        body.put("last_name", lastName);

        Response response = withBearerAndPublishableKey(registrationToken)
                .body(body)
                .post(ApiConstants.STORE_CUSTOMERS);

        logResponse(response, "createCustomer");
        return response;
    }

    /**
     * Login customer — trả về raw Response.
     * POST /auth/customer/emailpass
     *
     * @param email    email đã đăng ký
     * @param password password
     * @return Response, body chứa token nếu thành công (200)
     */
    public Response loginRaw(String email, String password) {
        log.info("[StorefrontAuth] login | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.STORE_AUTH_LOGIN);

        logResponse(response, "login");
        return response;
    }

    /**
     * Login customer — trả về JWT token string (dùng cho setup fixtures).
     * Throw RuntimeException nếu login thất bại.
     *
     * @param email    email
     * @param password password
     * @return JWT token string
     */
    public String login(String email, String password) {
        Response response = loginRaw(email, password);
        if (response.getStatusCode() != ApiConstants.STATUS_OK) {
            log.warn("Login thất bại | status: {} | body: {}",
                    response.getStatusCode(), response.getBody().asString());
            return null;
        }
        String token = response.jsonPath().getString("token");
        log.info("[StorefrontAuth] Login OK, token extracted");
        return token;
    }

    /**
     * Gửi yêu cầu reset password — emits auth.password_reset event.
     * POST /auth/customer/emailpass/reset-password
     *
     * @param identifier email của customer
     * @return Response (201 nếu thành công)
     */
    public Response resetPassword(String identifier) {
        log.info("[StorefrontAuth] resetPassword | identifier: {}", identifier);
        Map<String, Object> body = new HashMap<>();
        body.put("identifier", identifier);

        Response response = withNoAuth()
                .body(body)
                .post(ApiConstants.STORE_AUTH_RESET_PW);

        logResponse(response, "resetPassword");
        return response;
    }

    /**
     * Cập nhật password mới dùng reset token.
     * POST /auth/customer/emailpass/update
     * Header: Authorization: Bearer {resetToken}
     *
     * @param resetToken token nhận từ email (mocked trong tests)
     * @param email      email
     * @param newPassword password mới
     * @return Response (200 + { success: true } nếu OK)
     */
    public Response updatePassword(String resetToken, String email, String newPassword) {
        log.info("[StorefrontAuth] updatePassword | email: {}", email);
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", newPassword);

        Response response = withBearerToken(resetToken)
                .body(body)
                .post(ApiConstants.STORE_AUTH_UPDATE_PW);

        logResponse(response, "updatePassword");
        return response;
    }
}
