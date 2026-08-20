<p align="center">
  <img src="https://img.shields.io/badge/Java-11-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 11"/>
  <img src="https://img.shields.io/badge/Selenium-4.23-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium"/>
  <img src="https://img.shields.io/badge/REST%20Assured-5.4-4CAF50?style=for-the-badge" alt="REST Assured"/>
  <img src="https://img.shields.io/badge/TestNG-7.10-DC382D?style=for-the-badge" alt="TestNG"/>
  <img src="https://img.shields.io/badge/Allure-2.28-FF6600?style=for-the-badge" alt="Allure"/>
  <img src="https://img.shields.io/badge/Newman-Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white" alt="Newman"/>
</p>

# 🧪 Medusa v2 — Framework Kiểm Thử Tự Động E-Commerce

[![CI — Automation Tests](https://github.com/Nguyn1710/medusa-v2-automation/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Nguyn1710/medusa-v2-automation/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/📊_Allure_Report-Xem_Online-green?style=flat-square)](https://nguyn1710.github.io/medusa-v2-automation/)
[![README — English](https://img.shields.io/badge/README-English-blue?style=flat-square)](./README.md)

> **Framework kiểm thử tự động end-to-end** cho [Medusa v2](https://medusajs.com/) — nền tảng thương mại điện tử mã nguồn mở.  
> Bao phủ **Admin Dashboard (UI)**, **Storefront (UI)** và **REST API** với tích hợp CI/CD đầy đủ.

---

## 📌 Tổng Quan Dự Án

Dự án này xây dựng một **automation testing framework chuẩn production** từ đầu cho một nền tảng thương mại điện tử thực tế. Bao phủ đầy đủ testing pyramid — từ kiểm thử API đến luồng UI end-to-end.

### Phạm Vi Kiểm Thử

| Tầng Kiểm Thử | Công Cụ | Mô Tả |
|:---:|:---:|---|
| 🔌 **Kiểm thử API** | REST Assured | Kiểm thử REST API phía Admin & Storefront |
| 🖥️ **Kiểm thử UI — Admin** | Selenium WebDriver | Dashboard quản trị — Đăng nhập, Sản phẩm, Đơn hàng |
| 🛒 **Kiểm thử UI — Storefront** | Selenium WebDriver | Giao diện khách hàng — Duyệt, Giỏ hàng, Thanh toán |
| 📮 **Bộ sưu tập API** | Newman (Postman) | Bộ sưu tập Postman chạy API smoke test |
| 📊 **Báo cáo** | Allure Report | Báo cáo HTML tổng hợp kèm screenshot |
| ⚙️ **CI/CD** | GitHub Actions | Pipeline tự động → Allure trên GitHub Pages |

### Tổng Quan Độ Phủ

```
┌──────────────────────────────────────────────────────────────┐
│                  🧪 TỔNG CỘNG: 163 Test Cases                │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  🔌 API Tests (REST Assured)              66 tests           │
│  ├─ Storefront API (Auth/Cart/Order)      30 tests           │
│  └─ Admin API (Auth/CRUD)                 36 tests           │
│                                                              │
│  🖥️  Admin UI Tests (Selenium)             42 tests           │
│  ├─ Đăng nhập & Xác thực                 18 tests           │
│  ├─ Quản lý phiên đăng nhập              7 tests            │
│  ├─ Đặt lại mật khẩu                     4 tests            │
│  ├─ Quản lý sản phẩm                     4 tests            │
│  └─ Quản lý đơn hàng                     9 tests            │
│                                                              │
│  🛒 Storefront UI Tests (Selenium)        55 tests           │
│  ├─ Trang chủ                             6 tests            │
│  ├─ Duyệt cửa hàng                       5 tests            │
│  ├─ Chi tiết sản phẩm                    8 tests            │
│  ├─ Giỏ hàng                             5 tests            │
│  ├─ Thanh toán                            11 tests           │
│  ├─ Tìm kiếm                             5 tests            │
│  ├─ Quản lý tài khoản                    6 tests            │
│  ├─ Bảo mật                              4 tests            │
│  └─ Chọn cửa hàng                        5 tests            │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🏗️ Kiến Trúc

### Cấu Trúc Dự Án

```
medusa-v2-automation/
│
├── src/main/java/com/medusa/automation/
│   ├── api/                         # 🔌 Tầng API Client
│   │   ├── base/
│   │   │   ├── BaseApiClient.java   #    Base: xử lý auth, logging
│   │   │   └── ApiConstants.java    #    Mã HTTP, đường dẫn endpoint, SLA
│   │   ├── admin/
│   │   │   ├── AdminAuthApi.java    #    Xác thực Admin
│   │   │   ├── AdminOrderApi.java   #    CRUD đơn hàng
│   │   │   ├── AdminProductApi.java #    CRUD sản phẩm
│   │   │   └── AdminCustomerApi.java#    Quản lý khách hàng
│   │   ├── storefront/
│   │   │   ├── StorefrontAuthApi.java   # Xác thực khách hàng (đăng ký/đăng nhập)
│   │   │   ├── StorefrontCartApi.java   # Giỏ hàng & thanh toán
│   │   │   └── StorefrontOrderApi.java  # Lịch sử đơn hàng
│   │   └── utils/
│   │       └── ApiTestDataGenerator.java# Tạo dữ liệu test duy nhất
│   │
│   ├── config/
│   │   └── ConfigReader.java        # ⚙️ Quản lý cấu hình tập trung
│   │
│   ├── drivers/
│   │   └── DriverFactory.java       # 🌐 Khởi tạo trình duyệt (Chrome, headless)
│   │
│   ├── pages/                       # 📄 Mô hình Page Object (POM)
│   │   ├── BasePage.java            #    Base: waits, hành động chung
│   │   ├── LoginPage.java           #    Trang đăng nhập Admin
│   │   ├── DashboardPage.java       #    Dashboard Admin
│   │   ├── ResetPasswordPage.java   #    Luồng đặt lại mật khẩu
│   │   ├── AdminOrderListPage.java  #    Danh sách & lọc đơn hàng
│   │   ├── AdminDraftOrderPage.java #    Tạo đơn hàng nháp
│   │   ├── AdminProductListPage.java#    Danh sách & tìm kiếm sản phẩm
│   │   ├── AdminProductCreateDrawer.java # Drawer tạo sản phẩm
│   │   ├── StoreFrontHomePage.java   #    Trang chủ Storefront
│   │   ├── StoreFrontStorePage.java  #    Chọn vùng/cửa hàng
│   │   ├── StoreFrontProductPage.java#   Chi tiết sản phẩm
│   │   ├── StoreFrontCartPage.java  #    Giỏ hàng
│   │   ├── StoreFrontCheckoutPage.java # Luồng thanh toán
│   │   ├── StoreFrontAccountPage.java#   Tài khoản khách hàng
│   │   └── StoreFrontSearchPage.java#    Tìm kiếm
│   │
│   └── utils/
│       ├── ScreenshotUtil.java      # 📸 Tự động chụp ảnh khi test fail
│       └── TestDataGenerator.java   # 🎲 Tạo dữ liệu test duy nhất
│
├── src/test/java/com/medusa/automation/tests/
│   ├── LoginTest.java               # 🔑 18 test cases
│   ├── SessionManagementTest.java   # 🔒 7 test cases
│   ├── ResetPasswordTest.java       # 🔄 4 test cases
│   ├── AdminProductTest.java        # 📦 4 test cases
│   ├── AdminOrderTest.java          # 📋 9 test cases
│   ├── StoreFrontHomeTest.java      # 🏠 6 test cases
│   ├── StoreFrontStoreTest.java     # 🏪 5 test cases
│   ├── StoreFrontProductTest.java   # 🛍️ 8 test cases
│   ├── StoreFrontCartTest.java      # 🛒 5 test cases
│   ├── StoreFrontCheckoutTest.java  # 💳 11 test cases
│   ├── StoreFrontAccountTest.java   # 👤 6 test cases
│   ├── StoreFrontSearchTest.java    # 🔍 5 test cases
│   ├── StoreFrontSecurityTest.java  # 🛡️ 4 test cases
│   └── api/
│       ├── admin/
│       │   ├── AdminAuthApiTest.java      # 6 test cases
│       │   ├── AdminCustomerApiTest.java  # 11 test cases
│       │   ├── AdminOrderApiTest.java     # 10 test cases
│       │   └── AdminProductApiTest.java   # 9 test cases
│       └── storefront/
│           ├── StorefrontAuthApiTest.java  # 11 test cases
│           ├── StorefrontCartApiTest.java  # 10 test cases
│           └── StorefrontOrderApiTest.java # 9 test cases
│
├── config/
│   └── test.properties.example      # ⚙️ Mẫu cấu hình (không chứa credentials)
│
├── postman_collection.json          # 📮 Bộ sưu tập Postman API (Newman)
├── testng.xml                       # Suite kiểm thử Admin UI
├── testng-storefront.xml            # Suite kiểm thử Storefront UI
├── testng-api.xml                   # Suite kiểm thử API (chạy song song)
├── pom.xml                          # Cấu hình Maven
└── .github/workflows/ci.yml        # ⚙️ Pipeline CI/CD
```

### Mô Hình Thiết Kế (Design Patterns)

```
┌─────────────────────────────────────────────────────────────────┐
│                    Tầng Thực Thi Test                           │
│  LoginTest · StoreFrontCartTest · AdminOrderApiTest · ...       │
├─────────────────────────────────────────────────────────────────┤
│                Tầng Page Object / API Client                   │
│  LoginPage · StoreFrontCartPage · AdminOrderApi · ...           │
├─────────────────────────────────────────────────────────────────┤
│                     Tầng Hạ Tầng                               │
│  BasePage · BaseApiClient · DriverFactory · ConfigReader        │
├─────────────────────────────────────────────────────────────────┤
│                  Tầng Tiện Ích & Cấu Hình                      │
│  ScreenshotUtil · TestDataGenerator · test.properties           │
└─────────────────────────────────────────────────────────────────┘
```

| Design Pattern | Vị Trí | Mục Đích |
|---|---|---|
| **Page Object Model (POM)** | `pages/` | Tách locator UI khỏi logic test |
| **Factory Pattern** | `DriverFactory` | Khởi tạo instance trình duyệt |
| **Singleton Config** | `ConfigReader` | Quản lý cấu hình tập trung |
| **Builder Pattern** | API Clients | Xây dựng request theo chuỗi (fluent) |
| **Data Generator** | `TestDataGenerator` | Tạo dữ liệu test duy nhất, truy vết được |

---

## 🚀 Hướng Dẫn Cài Đặt & Chạy

### Yêu Cầu Hệ Thống

- **Java 11+** (JDK)
- **Maven 3.8+**
- **Google Chrome** (cho kiểm thử UI)
- **Node.js 22+** (cho Newman/Postman)
- Kết nối đến Medusa v2 backend đang chạy

### 1. Clone Dự Án

```bash
git clone https://github.com/Nguyn1710/medusa-v2-automation.git
cd medusa-v2-automation
```

### 2. Cấu Hình Môi Trường

```bash
# Sao chép file cấu hình mẫu
cp config/test.properties.example config/test.properties

# Chỉnh sửa với giá trị thực tế của bạn
```

```properties
# config/test.properties
base.url=https://your-medusa-admin.up.railway.app
storefront.url=https://your-storefront.vercel.app
admin.email=admin@example.com
admin.password=your_password
storefront.customer.email=customer@example.com
storefront.customer.password=customer_password
api.base.url=https://your-medusa-backend.up.railway.app
api.publishable.key=pk_your_key
browser=chrome
headless=false
```

### 3. Chạy Test

```bash
# ── Chạy TOÀN BỘ test suites ──
mvn test                                    # Mặc định: Admin UI suite
mvn test -DsuiteFile=testng-api.xml         # Chỉ API tests
mvn test -DsuiteFile=testng-storefront.xml  # Chỉ Storefront UI

# ── Chạy theo Nhóm ──
mvn test -Dgroups=smoke                     # Chỉ smoke tests
mvn test -Dgroups=regression                # Regression đầy đủ

# ── Chạy 1 Test Class cụ thể ──
mvn test -Dtest=LoginTest
mvn test -Dtest=StorefrontCartApiTest

# ── Xem Báo Cáo Allure ──
mvn allure:serve                            # Mở trong trình duyệt
```

---

## 📊 Phân Loại & Độ Phủ Test

### Phân Loại Test Case

| Loại | Icon | Mô Tả | Ví Dụ |
|---|:---:|---|---|
| **Happy Path** | ✅ | Input hợp lệ → kết quả đúng | Đăng nhập đúng → vào Dashboard |
| **Negative** | ❌ | Input sai → xử lý lỗi phù hợp | Sai mật khẩu → hiện thông báo lỗi |
| **Boundary** | 🔲 | Giá trị biên | Số lượng giỏ hàng = 0, số lượng tối đa |
| **Security** | 🔒 | Bypass xác thực, XSS, injection | Truy cập admin không đăng nhập → redirect |
| **Performance SLA** | ⚡ | Kiểm tra thời gian phản hồi | API response < 5000ms |
| **Pagination** | 📄 | Phân trang danh sách | `limit=5` trả về ≤ 5 items |

### Chi Tiết Test API

| Module | Test Class | Số TC | Endpoints |
|---|---|:---:|---|
| **Storefront Auth** | `StorefrontAuthApiTest` | 11 | `POST /auth/customer/emailpass/*` |
| **Storefront Cart** | `StorefrontCartApiTest` | 10 | `POST/GET /store/carts/*` |
| **Storefront Orders** | `StorefrontOrderApiTest` | 9 | `GET /store/orders/*` |
| **Admin Auth** | `AdminAuthApiTest` | 6 | `POST /auth/user/emailpass` |
| **Admin Customers** | `AdminCustomerApiTest` | 11 | `GET /admin/customers/*` |
| **Admin Orders** | `AdminOrderApiTest` | 10 | `GET /admin/orders/*` |
| **Admin Products** | `AdminProductApiTest` | 9 | `GET /admin/products/*` |

### Chi Tiết Test UI

| Module | Test Class | Số TC | Kịch Bản Chính |
|---|---|:---:|---|
| **Đăng nhập** | `LoginTest` | 18 | Đăng nhập đúng/sai, validate field, remember me |
| **Phiên đăng nhập** | `SessionManagementTest` | 7 | Hết hạn token, logout, phiên đồng thời |
| **Đặt lại mật khẩu** | `ResetPasswordTest` | 4 | Validate email, luồng đặt lại |
| **Sản phẩm Admin** | `AdminProductTest` | 4 | Tạo, liệt kê, tìm kiếm sản phẩm |
| **Đơn hàng Admin** | `AdminOrderTest` | 9 | Danh sách đơn hàng, bộ lọc, đơn nháp |
| **Trang chủ SF** | `StoreFrontHomeTest` | 6 | Hero section, navigation, sản phẩm nổi bật |
| **Cửa hàng SF** | `StoreFrontStoreTest` | 5 | Chọn vùng, chuyển cửa hàng |
| **Sản phẩm SF** | `StoreFrontProductTest` | 8 | Chi tiết, biến thể, hình ảnh |
| **Giỏ hàng SF** | `StoreFrontCartTest` | 5 | Thêm/xóa, cập nhật số lượng |
| **Thanh toán SF** | `StoreFrontCheckoutTest` | 11 | Địa chỉ, vận chuyển, thanh toán |
| **Tài khoản SF** | `StoreFrontAccountTest` | 6 | Hồ sơ, lịch sử đơn hàng |
| **Tìm kiếm SF** | `StoreFrontSearchTest` | 5 | Tìm kiếm, bộ lọc, không có kết quả |
| **Bảo mật SF** | `StoreFrontSecurityTest` | 4 | Bảo vệ route, kiểm soát truy cập |

---

## ⚙️ Pipeline CI/CD

Dự án sử dụng **GitHub Actions** cho tích hợp liên tục. Mỗi lần push lên nhánh `main` sẽ tự động chạy toàn bộ test suite.

### Luồng Pipeline

```
┌──────────┐    ┌──────────┐    ┌───────────┐    ┌───────────┐
│ Checkout │───▶│ Build    │───▶│ API Tests │───▶│ Admin UI  │
│ + Setup  │    │ Compile  │    │ (REST     │    │ Tests     │
│          │    │          │    │  Assured) │    │ (Selenium)│
└──────────┘    └──────────┘    └───────────┘    └─────┬─────┘
                                                       │
┌──────────────┐    ┌──────────┐    ┌───────────┐      │
│ Deploy Allure│◀───│ Generate │◀───│ Newman    │◀─────┤
│ → GH Pages   │    │ Report   │    │ (Postman) │      │
└──────────────┘    └──────────┘    └───────────┘  ┌───▼───────┐
                                                   │ Storefront│
                                                   │ UI Tests  │
                                                   └───────────┘
```

### Tính Năng Pipeline

- ✅ **Tự động kích hoạt** khi push & pull request vào `main`
- ✅ **Chạy thủ công** với lựa chọn suite (`all` / `api` / `ui` / `storefront`)
- ✅ **Allure Report** tự động deploy lên GitHub Pages
- ✅ **Lưu trữ artifact** — screenshots, surefire reports (14 ngày)
- ✅ **Quản lý secrets** — credentials inject qua GitHub Secrets
- ✅ **Headless Chrome** trong môi trường CI

---

## 🛠️ Công Nghệ Sử Dụng

| Phân Loại | Công Nghệ | Phiên Bản | Mục Đích |
|---|---|---|---|
| **Ngôn ngữ** | Java | 11 | Ngôn ngữ lập trình chính |
| **UI Automation** | Selenium WebDriver | 4.23.0 | Tự động hóa trình duyệt |
| **Quản lý Driver** | WebDriverManager | 5.9.2 | Tự động tải ChromeDriver |
| **Kiểm thử API** | REST Assured | 5.4.0 | Validate HTTP API |
| **Bộ sưu tập API** | Newman (Postman) | Mới nhất | Chạy Postman collection |
| **Test Framework** | TestNG | 7.10.2 | Thực thi & phân nhóm test |
| **Báo cáo** | Allure Report | 2.28.0 | Báo cáo HTML đẹp |
| **Xử lý JSON** | Jackson | 2.17.1 | Serialize/deserialize JSON |
| **Logging** | Log4j2 | 2.23.1 | Ghi log có cấu trúc |
| **Build Tool** | Maven | 3.8+ | Quản lý dependency |
| **CI/CD** | GitHub Actions | — | Tích hợp liên tục |

---

## 📋 Chiến Lược Dữ Liệu Test

Toàn bộ dữ liệu test được **tạo tự động** để đảm bảo tính độc lập và truy vết được:

```java
// Email duy nhất với timestamp — truy vết được theo lần chạy test
String email = ApiTestDataGenerator.generateCustomerEmail("register");
// → auto_api_register_1786942625@test.com

// Trace ID liên kết dữ liệu test với test case
String traceId = ApiTestDataGenerator.traceId("TC_CART_001");
// → TC_CART_001_1786942625
```

**Nguyên tắc:**
- ✅ Mỗi lần chạy sử dụng **dữ liệu duy nhất** (dựa trên timestamp)
- ✅ Dữ liệu **truy vết được** — biết test nào tạo ra dữ liệu nào
- ✅ **Không hardcode** email, password hay ID trong code test
- ✅ Các test **độc lập** — không chia sẻ trạng thái giữa các test

---

## 📁 Các Test Suite

| File Suite | Phạm Vi | Song Song | Nhóm |
|---|---|---|---|
| `testng.xml` | Admin UI — Đăng nhập, Sản phẩm, Đơn hàng | Tuần tự | `smoke`, `regression`, `knownBug` |
| `testng-storefront.xml` | Storefront UI — Trang chủ đến Thanh toán | Tuần tự | `smoke`, `regression` |
| `testng-api.xml` | REST API — Tất cả endpoints | Song song (2 threads) | `api`, `admin`, `storefront` |
| `postman_collection.json` | Postman API — Smoke tests | Tuần tự | — |

---

## 📊 Báo Cáo

### Allure Report

Báo cáo Allure tổng hợp bao gồm kết quả từ **tất cả test suites** (REST Assured + Selenium + Newman):

- 📈 **Dashboard** — Tổng quan pass/fail, xu hướng thời gian
- 📋 **Test Cases** — Phân nhóm theo suite, kèm steps và assertions
- 📸 **Screenshots** — Tự động chụp khi UI test fail
- ⏱️ **Timeline** — Trực quan hóa thực thi song song
- 📎 **Đính kèm** — Request/response logs cho API tests

> 🔗 **Báo cáo trực tuyến**: [https://nguyn1710.github.io/medusa-v2-automation/](https://nguyn1710.github.io/medusa-v2-automation/)

---

## 🤝 Các Phương Pháp Áp Dụng

| Phương Pháp | Cách Triển Khai |
|---|---|
| **Page Object Model** | Tất cả locator UI được tách biệt trong các class Page |
| **Smart Waits** | `WebDriverWait` + `ExpectedConditions` — không dùng `Thread.sleep` |
| **Test Độc Lập** | Mỗi test có thể chạy riêng lẻ — không phụ thuộc thứ tự |
| **Assertion Có Ý Nghĩa** | Mỗi assertion kèm thông báo lỗi mô tả rõ ràng |
| **Phân Nhóm Test** | `@Test(groups = {"smoke", "regression"})` để chạy chọn lọc |
| **Cấu Hình Tách Biệt** | Tất cả URL, credentials nằm trong `test.properties` — không trong code |
| **Sẵn Sàng CI** | Tự động bật headless mode trong CI qua biến môi trường |

---

## 📸 Ảnh Chụp Màn Hình & Bằng Chứng CI

### ✅ GitHub Actions — Pipeline CI (PASSED)

![GitHub Actions CI Pipeline](docs/screenshots/github_actions_run.jpg)

> Mỗi lần push lên `main` sẽ tự động kích hoạt toàn bộ test suite. Tất cả các bước — API Tests, Admin UI, Storefront UI, Newman — chạy tuần tự và deploy Allure Report lên GitHub Pages.

---

### 📊 Allure Report — Dashboard Tổng Quan

![Allure Report Dashboard](docs/screenshots/allure_dashboard.jpg)

> Báo cáo tổng hợp từ **163 test cases** bao gồm REST Assured, Selenium và Newman — hiển thị thành một Allure Report thống nhất.

---

### 🔍 Allure Report — Chi Tiết Test Suite

![Allure Report Suites](docs/screenshots/allure_suites.jpg)

> Giao diện chi tiết hiển thị từng test case với trace thực thi từng bước, status badge, thời gian chạy và đính kèm request/response cho API tests.

---

## 📄 Giấy Phép

Dự án này được tạo cho **mục đích học tập và portfolio cá nhân**.

---

<p align="center">
  <b>Xây dựng với ❤️ cho việc học QA Automation</b><br/>
  <sub>Java · Selenium · REST Assured · TestNG · Allure · GitHub Actions</sub>
</p>

