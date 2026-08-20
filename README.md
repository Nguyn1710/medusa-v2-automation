# 🧪 Medusa v2 API Automation Framework

> **Automation test suite cho Medusa v2 E-Commerce Platform**  
> Java 11 · REST Assured 5.4 · TestNG 7.10 · Allure Report 2.28

---

## 📌 Tổng Quan

Framework này tự động kiểm thử toàn bộ **Medusa v2 REST API** bao gồm Storefront API và Admin API, bao phủ các scenario: Happy Path, Negative, Boundary, Security và Performance SLA.

| Tầng | Endpoints | Test Classes | Test Cases |
|---|---|---|---|
| **Storefront Auth** | `POST /auth/customer/emailpass/*` | `StorefrontAuthApiTest` | 11 TCs |
| **Storefront Cart** | `POST/GET /store/carts/*` | `StorefrontCartApiTest` | 10 TCs |
| **Storefront Orders** | `GET /store/orders/*` | `StorefrontOrderApiTest` | 9 TCs |
| **Admin Auth** | `POST /auth/user/emailpass` | `AdminAuthApiTest` | 6 TCs |
| **Admin Orders** | `GET /admin/orders/*` | `AdminOrderApiTest` | 10 TCs |
| **Admin Products** | `GET /admin/products/*` | `AdminProductApiTest` | 9 TCs |
| **TOTAL** | — | **6 test classes** | **55 TCs** |

---

## 🏗️ Kiến Trúc (Architecture)

```
selenium-java/
├── src/main/java/com/medusa/automation/
│   ├── api/
│   │   ├── base/
│   │   │   ├── BaseApiClient.java       # Base class: auth helpers, logging
│   │   │   └── ApiConstants.java        # HTTP status codes, endpoint paths
│   │   ├── storefront/
│   │   │   ├── StorefrontAuthApi.java   # Register, Login, Reset Password
│   │   │   ├── StorefrontCartApi.java   # Cart CRUD, Line Items, Shipping
│   │   │   └── StorefrontOrderApi.java  # Order List, Order Detail
│   │   ├── admin/
│   │   │   ├── AdminAuthApi.java        # Admin Login
│   │   │   ├── AdminOrderApi.java       # Admin Order List + Detail
│   │   │   ├── AdminProductApi.java     # Admin Product List + Detail
│   │   │   └── AdminCustomerApi.java    # Admin Customer List + Detail
│   │   └── utils/
│   │       └── ApiTestDataGenerator.java  # Unique test data generator
│   ├── config/
│   │   └── ConfigReader.java            # Reads test.properties
│   └── ...
│
├── src/test/java/com/medusa/automation/
│   ├── base/
│   │   └── BaseApiTest.java             # Suite setup, token management
│   └── tests/api/
│       ├── storefront/
│       │   ├── StorefrontAuthApiTest.java
│       │   ├── StorefrontCartApiTest.java
│       │   └── StorefrontOrderApiTest.java
│       └── admin/
│           ├── AdminAuthApiTest.java
│           ├── AdminOrderApiTest.java
│           └── AdminProductApiTest.java
│
├── config/
│   └── test.properties                  # Base URLs, credentials, timeouts
│
├── testng-api.xml                       # API test suite (separate từ UI tests)
└── pom.xml                              # Maven dependencies
```

### Layered Design

```
[Test Class] → [API Client] → [BaseApiClient] → [REST API]
     ↑               ↑               ↑
  Assertions    Endpoint methods  Auth headers, Logging
```

---

## 🚀 Chạy Tests

### Điều kiện tiên quyết

- Java 11+
- Maven 3.8+
- Kết nối đến Medusa backend (URL cấu hình trong `config/test.properties`)

### Cấu hình

Sao chép và chỉnh sửa file config:

```properties
# config/test.properties
api.base.url=https://your-medusa-backend.up.railway.app
api.publishable.key=pk_your_publishable_key
admin.email=admin@example.com
admin.password=your_admin_password
storefront.customer.email=customer@example.com
storefront.customer.password=your_customer_password
```

### Chạy API Test Suite

```bash
# Chạy toàn bộ API tests
mvn test -DsuiteFile=testng-api.xml

# Chạy theo nhóm (group filtering)
mvn test -DsuiteFile=testng-api.xml -Dgroups=positive
mvn test -DsuiteFile=testng-api.xml -Dgroups=negative
mvn test -DsuiteFile=testng-api.xml -Dgroups=admin

# Chạy 1 test class cụ thể
mvn test -Dtest=StorefrontAuthApiTest

# Chạy song song (đã cấu hình trong testng-api.xml)
# thread-count=2, parallel=classes
```

### Xem Allure Report

```bash
# Generate và mở report
mvn allure:report
mvn allure:serve
```

---

## 📊 Test Coverage

### Test Types

| Loại | Mô tả | Ví dụ |
|---|---|---|
| ✅ Happy Path | Request hợp lệ → response đúng schema + status | Login thành công → 200 + token |
| ❌ Negative | Input sai → error response phù hợp | Login sai password → 401 |
| 🔲 Boundary | Giới hạn giá trị | Quantity âm → error |
| 📄 Pagination | Phân trang | limit=5 → ≤ 5 items |
| ⚡ Performance | Response time SLA | Response < 5000ms |
| 🔒 Security | Auth bypass attempt | Call without token → 401 |

### Authentication Flow

```
                    ┌─────────────────┐
                    │ POST /auth/      │
                    │ customer/        │
                    │ emailpass/       │
                    │ register         │
                    └────────┬────────┘
                             │ registration token
                    ┌────────▼────────┐
                    │ POST /store/    │
                    │ customers       │
                    │ (create profile)│
                    └────────┬────────┘
                             │ customer created
                    ┌────────▼────────┐
                    │ POST /auth/     │
                    │ customer/       │
                    │ emailpass       │
                    │ (login)         │
                    └────────┬────────┘
                             │ JWT token
                    ┌────────▼────────┐
                    │ API calls with  │
                    │ Bearer token    │
                    └─────────────────┘
```

### Cart Flow

```
Create Cart → Add Line Item → Add Shipping → Complete Checkout
    ↓              ↓               ↓
  cartId      items updated    cart ready
```

---

## 🛠️ Tech Stack

| Tool | Version | Mục đích |
|---|---|---|
| **Java** | 11 | Ngôn ngữ |
| **REST Assured** | 5.4.0 | HTTP client cho API tests |
| **TestNG** | 7.10.2 | Test framework, parallel execution |
| **Allure** | 2.28.0 | Test reporting |
| **Jackson** | 2.17.1 | JSON serialization |
| **Log4j2** | 2.23.1 | Logging |
| **Maven Surefire** | 3.3.1 | Build & test runner |

---

## 📋 Test Data Strategy

Test data được sinh tự động bởi `ApiTestDataGenerator`:

```java
// Email unique với timestamp — traceable khi debug
String email = ApiTestDataGenerator.generateCustomerEmail("register");
// → auto_api_register_1786942625@test.com

// Trace ID để link test với execution
String traceId = ApiTestDataGenerator.traceId("TC_CART_001");
// → TC_CART_001_1786942625
```

**Nguyên tắc:**
- Email, username phải unique mỗi lần chạy (timestamp-based)
- Data traceable — biết test nào tạo data nào
- Không hardcode email/ID trong test

---

## 📁 Output & Reports

```
target/
├── allure-results/           # Raw Allure JSON results
├── surefire-reports/         # TestNG XML reports
└── screenshots/              # Screenshots khi test fail (UI tests)
```

---

## 🔧 Cấu Hình Nâng Cao

### Parallel Execution

```xml
<!-- testng-api.xml -->
<suite name="Medusa API Test Suite" parallel="classes" thread-count="2">
```

### Timeouts

```properties
# config/test.properties
api.timeout.ms=10000        # HTTP request timeout
explicit.wait=15            # Selenium wait (UI tests)
```

### Response Time SLA

Tất cả API tests đều kiểm tra SLA:
```java
ApiConstants.MAX_RESPONSE_TIME_MS = 5000L  // 5 giây
```

---

## 🤝 Conventions

- **Naming**: `TC_API_{DOMAIN}_{TYPE}_{NNN}_{description}`
- **Groups**: `api`, `storefront`/`admin`, `auth`/`cart`/`orders`, `positive`/`negative`
- **Assertions**: Mỗi assert có error message rõ ràng bằng tiếng Việt/Anh
- **Logging**: Mỗi TC log kết quả ở cuối: `log.info("TC_XXX PASS | ...")`

---

*Automation Framework cho Medusa v2 E-Commerce — Java / REST Assured / TestNG / Allure*
