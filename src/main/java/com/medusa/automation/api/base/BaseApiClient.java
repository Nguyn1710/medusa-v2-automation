package com.medusa.automation.api.base;

import com.medusa.automation.config.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * BaseApiClient — cấu hình chung cho tất cả API client classes.
 *
 * Cung cấp:
 * - requestSpec với baseURI, default headers, logging
 * - Phương thức helper: get(), post(), put(), patch(), delete()
 * - Auth header builder: withBearerToken()
 *
 * Mỗi API client (StorefrontAuthApi, AdminOrderApi, ...) extends class này.
 */
public abstract class BaseApiClient {

    protected static final Logger log = LogManager.getLogger(BaseApiClient.class);
    protected static final ConfigReader config = ConfigReader.getInstance();

    protected final RequestSpecification baseSpec;
    protected final String baseUrl;

    protected BaseApiClient() {
        this.baseUrl = config.getApiBaseUrl();

        // Thiết lập base spec dùng chung: baseURI + content-type + logging
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        log.debug("BaseApiClient khởi tạo với baseUrl: {}", baseUrl);
    }

    // ====================================================
    // Helper: build request với Bearer token
    // ====================================================

    /**
     * Tạo RequestSpecification với Authorization: Bearer {token}
     */
    protected RequestSpecification withBearerToken(String token) {
        return given()
                .spec(baseSpec)
                .header(ApiConstants.HEADER_AUTHORIZATION, "Bearer " + token);
    }

    /**
     * Tạo RequestSpecification với x-publishable-api-key (Storefront API)
     */
    protected RequestSpecification withPublishableKey() {
        String pubKey = config.getPublishableKey();
        return given()
                .spec(baseSpec)
                .header(ApiConstants.HEADER_PUBLISHABLE_KEY, pubKey);
    }

    /**
     * Tạo RequestSpecification với cả publishable key + Bearer token
     * Dùng cho Storefront APIs cần auth (orders, customer profile)
     */
    protected RequestSpecification withBearerAndPublishableKey(String token) {
        String pubKey = config.getPublishableKey();
        return given()
                .spec(baseSpec)
                .header(ApiConstants.HEADER_PUBLISHABLE_KEY, pubKey)
                .header(ApiConstants.HEADER_AUTHORIZATION, "Bearer " + token);
    }

    /**
     * Tạo RequestSpecification không cần auth (public endpoints)
     */
    protected RequestSpecification withNoAuth() {
        return given().spec(baseSpec);
    }

    // ====================================================
    // Helpers: log response info
    // ====================================================

    protected void logResponse(Response response, String context) {
        log.info("[{}] Status: {} | Time: {}ms",
                context,
                response.getStatusCode(),
                response.getTime());
    }

    // ====================================================
    // Utility: Build JSON body từ Map
    // ====================================================

    protected Map<String, Object> body(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("body() cần số lượng tham số chẵn (key-value pairs)");
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
        }
        return map;
    }
}
