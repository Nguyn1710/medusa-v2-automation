<p align="center">
  <img src="https://img.shields.io/badge/Java-11-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 11"/>
  <img src="https://img.shields.io/badge/Selenium-4.23-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium"/>
  <img src="https://img.shields.io/badge/REST%20Assured-5.4-4CAF50?style=for-the-badge" alt="REST Assured"/>
  <img src="https://img.shields.io/badge/TestNG-7.10-DC382D?style=for-the-badge" alt="TestNG"/>
  <img src="https://img.shields.io/badge/Allure-2.28-FF6600?style=for-the-badge" alt="Allure"/>
  <img src="https://img.shields.io/badge/Newman-Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white" alt="Newman"/>
</p>

# 🧪 Medusa v2 — E-Commerce Test Automation Framework

[![CI — Automation Tests](https://github.com/Nguyn1710/medusa-v2-automation/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Nguyn1710/medusa-v2-automation/actions/workflows/ci.yml)
[![Allure Report](https://img.shields.io/badge/📊_Allure_Report-Live-green?style=flat-square)](https://nguyn1710.github.io/medusa-v2-automation/)
[![README — Tiếng Việt](https://img.shields.io/badge/README-Tiếng_Việt-red?style=flat-square)](./README_VI.md)

> **End-to-end test automation framework** for [Medusa v2](https://medusajs.com/) — an open-source headless e-commerce platform.  
> Covers **Admin Dashboard (UI)**, **Storefront (UI)**, and **REST API** testing with full CI/CD integration.

---

## 📌 Project Overview

This project demonstrates a **production-grade automation testing framework** built from scratch for a real e-commerce platform. It covers the full testing pyramid — from API contract validation to end-to-end UI flows.

### What This Project Covers

| Testing Layer | Tool | Description |
|:---:|:---:|---|
| 🔌 **API Testing** | REST Assured | Admin & Storefront REST API validation |
| 🖥️ **UI Testing — Admin** | Selenium WebDriver | Admin Dashboard — Login, Products, Orders |
| 🛒 **UI Testing — Storefront** | Selenium WebDriver | Customer-facing — Browse, Cart, Checkout |
| 📮 **API Collection** | Newman (Postman) | Postman collection for API smoke tests |
| 📊 **Reporting** | Allure Report | Consolidated HTML report with screenshots |
| ⚙️ **CI/CD** | GitHub Actions | Automated pipeline → Allure on GitHub Pages |

### Test Coverage at a Glance

```
┌──────────────────────────────────────────────────────┐
│                 🧪 TOTAL: 163 Test Cases              │
├──────────────────────────────────────────────────────┤
│                                                      │
│  🔌 API Tests (REST Assured)         66 tests        │
│  ├─ Storefront API (Auth/Cart/Order) 30 tests        │
│  └─ Admin API (Auth/CRUD)            36 tests        │
│                                                      │
│  🖥️  Admin UI Tests (Selenium)        42 tests        │
│  ├─ Login & Auth                     18 tests        │
│  ├─ Session Management                7 tests        │
│  ├─ Reset Password                    4 tests        │
│  ├─ Products Management               4 tests        │
│  └─ Orders Management                 9 tests        │
│                                                      │
│  🛒 Storefront UI Tests (Selenium)   55 tests        │
│  ├─ Home Page                         6 tests        │
│  ├─ Store Browse                      5 tests        │
│  ├─ Product Detail                    8 tests        │
│  ├─ Cart                              5 tests        │
│  ├─ Checkout                         11 tests        │
│  ├─ Search                            5 tests        │
│  ├─ Account Management                6 tests        │
│  ├─ Security                          4 tests        │
│  └─ Store Selector                    5 tests        │
│                                                      │
└──────────────────────────────────────────────────────┘
```

---

## 🏗️ Architecture

### Project Structure

```
medusa-v2-automation/
│
├── src/main/java/com/medusa/automation/
│   ├── api/                         # 🔌 API Client Layer
│   │   ├── base/
│   │   │   ├── BaseApiClient.java   #    Base: auth helpers, request logging
│   │   │   └── ApiConstants.java    #    HTTP codes, endpoint paths, SLA
│   │   ├── admin/
│   │   │   ├── AdminAuthApi.java    #    Admin authentication
│   │   │   ├── AdminOrderApi.java   #    Order CRUD operations
│   │   │   ├── AdminProductApi.java #    Product CRUD operations
│   │   │   └── AdminCustomerApi.java#    Customer management
│   │   ├── storefront/
│   │   │   ├── StorefrontAuthApi.java   # Customer auth (register/login)
│   │   │   ├── StorefrontCartApi.java   # Cart & checkout flow
│   │   │   └── StorefrontOrderApi.java  # Order history
│   │   └── utils/
│   │       └── ApiTestDataGenerator.java# Unique test data generator
│   │
│   ├── config/
│   │   └── ConfigReader.java        # ⚙️ Centralized config management
│   │
│   ├── drivers/
│   │   └── DriverFactory.java       # 🌐 Browser factory (Chrome, headless)
│   │
│   ├── pages/                       # 📄 Page Object Model (POM)
│   │   ├── BasePage.java            #    Base: waits, common actions
│   │   ├── LoginPage.java           #    Admin login page
│   │   ├── DashboardPage.java       #    Admin dashboard
│   │   ├── ResetPasswordPage.java   #    Password reset flow
│   │   ├── AdminOrderListPage.java  #    Order list & filters
│   │   ├── AdminDraftOrderPage.java #    Draft order creation
│   │   ├── AdminProductListPage.java#    Product list & search
│   │   ├── AdminProductCreateDrawer.java # Product creation drawer
│   │   ├── StoreFrontHomePage.java   #    Storefront landing page
│   │   ├── StoreFrontStorePage.java  #    Store/region selector
│   │   ├── StoreFrontProductPage.java#   Product detail page
│   │   ├── StoreFrontCartPage.java  #    Shopping cart
│   │   ├── StoreFrontCheckoutPage.java # Checkout flow
│   │   ├── StoreFrontAccountPage.java#   Customer account
│   │   └── StoreFrontSearchPage.java#    Search functionality
│   │
│   └── utils/
│       ├── ScreenshotUtil.java      # 📸 Auto-screenshot on failure
│       └── TestDataGenerator.java   # 🎲 Unique test data
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
│   └── test.properties.example      # ⚙️ Config template (no credentials)
│
├── postman_collection.json          # 📮 Postman API collection (Newman)
├── testng.xml                       # Admin UI test suite
├── testng-storefront.xml            # Storefront UI test suite
├── testng-api.xml                   # API test suite (parallel execution)
├── pom.xml                          # Maven build configuration
└── .github/workflows/ci.yml        # ⚙️ CI/CD Pipeline
```

### Design Patterns

```
┌─────────────────────────────────────────────────────────────────┐
│                      Test Execution Layer                       │
│  LoginTest · StoreFrontCartTest · AdminOrderApiTest · ...       │
├─────────────────────────────────────────────────────────────────┤
│              Page Object / API Client Layer                     │
│  LoginPage · StoreFrontCartPage · AdminOrderApi · ...           │
├─────────────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                          │
│  BasePage · BaseApiClient · DriverFactory · ConfigReader        │
├─────────────────────────────────────────────────────────────────┤
│                    Utility & Config Layer                       │
│  ScreenshotUtil · TestDataGenerator · test.properties           │
└─────────────────────────────────────────────────────────────────┘
```

| Pattern | Where | Purpose |
|---|---|---|
| **Page Object Model (POM)** | `pages/` | Separates UI locators from test logic |
| **Factory Pattern** | `DriverFactory` | Browser instance creation |
| **Singleton Config** | `ConfigReader` | Centralized configuration |
| **Builder Pattern** | API Clients | Fluent request construction |
| **Data Generator** | `TestDataGenerator` | Unique, traceable test data |

---

## 🚀 Getting Started

### Prerequisites

- **Java 11+** (JDK)
- **Maven 3.8+**
- **Google Chrome** (for UI tests)
- **Node.js 22+** (for Newman/Postman tests)
- Access to a running Medusa v2 backend

### 1. Clone the Repository

```bash
git clone https://github.com/Nguyn1710/medusa-v2-automation.git
cd medusa-v2-automation
```

### 2. Configure Environment

```bash
# Copy the example config
cp config/test.properties.example config/test.properties

# Edit with your actual values
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

### 3. Run Tests

```bash
# ── Run ALL test suites ──
mvn test                                    # Default: Admin UI suite
mvn test -DsuiteFile=testng-api.xml         # API tests only
mvn test -DsuiteFile=testng-storefront.xml  # Storefront UI tests

# ── Run by Group ──
mvn test -Dgroups=smoke                     # Smoke tests only
mvn test -Dgroups=regression                # Full regression

# ── Run a Single Test Class ──
mvn test -Dtest=LoginTest
mvn test -Dtest=StorefrontCartApiTest

# ── Generate Allure Report ──
mvn allure:serve                            # Opens in browser
```

---

## 📊 Test Types & Coverage

### Test Categories

| Type | Icon | Description | Example |
|---|:---:|---|---|
| **Happy Path** | ✅ | Valid input → expected result | Login with correct credentials → Dashboard |
| **Negative** | ❌ | Invalid input → proper error handling | Login with wrong password → Error message |
| **Boundary** | 🔲 | Edge case values | Cart with quantity 0, max quantity |
| **Security** | 🔒 | Auth bypass, XSS, injection attempts | Access admin page without login → Redirect |
| **Performance SLA** | ⚡ | Response time validation | API response < 5000ms |
| **Pagination** | 📄 | List endpoint pagination | `limit=5` returns ≤ 5 items |

### API Test Details

| Module | Test Class | Tests | Endpoints |
|---|---|:---:|---|
| **Storefront Auth** | `StorefrontAuthApiTest` | 11 | `POST /auth/customer/emailpass/*` |
| **Storefront Cart** | `StorefrontCartApiTest` | 10 | `POST/GET /store/carts/*` |
| **Storefront Orders** | `StorefrontOrderApiTest` | 9 | `GET /store/orders/*` |
| **Admin Auth** | `AdminAuthApiTest` | 6 | `POST /auth/user/emailpass` |
| **Admin Customers** | `AdminCustomerApiTest` | 11 | `GET /admin/customers/*` |
| **Admin Orders** | `AdminOrderApiTest` | 10 | `GET /admin/orders/*` |
| **Admin Products** | `AdminProductApiTest` | 9 | `GET /admin/products/*` |

### UI Test Details

| Module | Test Class | Tests | Key Scenarios |
|---|---|:---:|---|
| **Login** | `LoginTest` | 18 | Valid/invalid login, field validation, remember me |
| **Session** | `SessionManagementTest` | 7 | Token expiry, logout, concurrent sessions |
| **Reset Password** | `ResetPasswordTest` | 4 | Email validation, reset flow |
| **Admin Products** | `AdminProductTest` | 4 | Create, list, search products |
| **Admin Orders** | `AdminOrderTest` | 9 | Order list, filters, draft orders |
| **SF Home** | `StoreFrontHomeTest` | 6 | Hero section, navigation, featured products |
| **SF Store** | `StoreFrontStoreTest` | 5 | Region selector, store switcher |
| **SF Products** | `StoreFrontProductTest` | 8 | Product detail, variants, images |
| **SF Cart** | `StoreFrontCartTest` | 5 | Add/remove items, quantity update |
| **SF Checkout** | `StoreFrontCheckoutTest` | 11 | Address, shipping, payment flow |
| **SF Account** | `StoreFrontAccountTest` | 6 | Profile, order history |
| **SF Search** | `StoreFrontSearchTest` | 5 | Search, filters, no results |
| **SF Security** | `StoreFrontSecurityTest` | 4 | Auth guards, protected routes |

---

## ⚙️ CI/CD Pipeline

The project uses **GitHub Actions** for continuous integration. Every push to `main` triggers the full test suite.

### Pipeline Flow

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

### Pipeline Features

- ✅ **Auto-trigger** on push & pull request to `main`
- ✅ **Manual dispatch** with suite selection (`all` / `api` / `ui` / `storefront`)
- ✅ **Allure Report** auto-deployed to GitHub Pages
- ✅ **Artifacts preserved** — screenshots, surefire reports (14 days)
- ✅ **Secrets management** — credentials injected via GitHub Secrets
- ✅ **Headless Chrome** in CI environment

---

## 🛠️ Tech Stack

| Category | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Java | 11 | Core language |
| **UI Automation** | Selenium WebDriver | 4.23.0 | Browser automation |
| **Driver Management** | WebDriverManager | 5.9.2 | Auto ChromeDriver setup |
| **API Testing** | REST Assured | 5.4.0 | HTTP API validation |
| **API Collection** | Newman (Postman) | Latest | Postman collection runner |
| **Test Framework** | TestNG | 7.10.2 | Test execution & grouping |
| **Reporting** | Allure Report | 2.28.0 | Rich HTML reports |
| **JSON** | Jackson | 2.17.1 | JSON serialization |
| **Logging** | Log4j2 | 2.23.1 | Structured logging |
| **Build** | Maven | 3.8+ | Dependency management |
| **CI/CD** | GitHub Actions | — | Continuous integration |

---

## 📋 Test Data Strategy

All test data is **dynamically generated** to ensure test isolation and traceability:

```java
// Unique email with timestamp — traceable to specific test run
String email = ApiTestDataGenerator.generateCustomerEmail("register");
// → auto_api_register_1786942625@test.com

// Trace ID links test data back to test case
String traceId = ApiTestDataGenerator.traceId("TC_CART_001");
// → TC_CART_001_1786942625
```

**Principles:**
- ✅ Every test run uses **unique data** (timestamp-based)
- ✅ Data is **traceable** — can identify which test created which data
- ✅ **No hardcoded** emails, passwords, or IDs in test code
- ✅ Tests are **independent** — no shared state between tests

---

## 📁 Test Suites

| Suite File | Scope | Parallel | Groups |
|---|---|---|---|
| `testng.xml` | Admin UI — Login, Products, Orders | Sequential | `smoke`, `regression`, `knownBug` |
| `testng-storefront.xml` | Storefront UI — Home to Checkout | Sequential | `smoke`, `regression` |
| `testng-api.xml` | REST API — All endpoints | Parallel (2 threads) | `api`, `admin`, `storefront` |
| `postman_collection.json` | Postman API — Smoke tests | Sequential | — |

---

## 📊 Reporting

### Allure Report

The consolidated Allure Report includes results from **all test suites** (REST Assured + Selenium + Newman):

- 📈 **Dashboard** — Pass/fail overview, duration trends
- 📋 **Test Cases** — Grouped by suite, with steps and assertions
- 📸 **Screenshots** — Auto-captured on UI test failure
- ⏱️ **Timeline** — Parallel execution visualization
- 📎 **Attachments** — Request/response logs for API tests

> 🔗 **Live Report**: [https://nguyn1710.github.io/medusa-v2-automation/](https://nguyn1710.github.io/medusa-v2-automation/)

---

## 🤝 Key Practices

| Practice | Implementation |
|---|---|
| **Page Object Model** | All UI locators isolated in Page classes |
| **Smart Waits** | `WebDriverWait` + `ExpectedConditions` — no `Thread.sleep` |
| **Test Independence** | Each test can run standalone — no order dependency |
| **Meaningful Assertions** | Every assertion includes descriptive error messages |
| **Test Grouping** | `@Test(groups = {"smoke", "regression"})` for selective execution |
| **Config Externalization** | All URLs, credentials in `test.properties` — not in code |
| **CI-Ready** | Headless mode auto-enabled in CI via environment variable |

---

## 📸 Screenshots & CI Evidence

### ✅ GitHub Actions — CI Pipeline (PASSED)

![GitHub Actions CI Pipeline Run](docs/screenshots/github_actions_run.jpg)

> Every push to `main` triggers the full test suite automatically. All steps — API Tests, Admin UI, Storefront UI, Newman — run sequentially and deploy the Allure Report to GitHub Pages.

---

### 📊 Allure Report — Dashboard Overview

![Allure Report Dashboard](docs/screenshots/allure_dashboard.jpg)

> Consolidated report from **163 test cases** across REST Assured, Selenium, and Newman — displayed as a single unified Allure Report.

---

### 🔍 Allure Report — Test Suite Detail

![Allure Report Suites Detail](docs/screenshots/allure_suites.jpg)

> Drill-down view showing individual test cases with step-by-step execution trace, status badges, duration, and request/response attachments for API tests.

---

## 📄 License

This project is for **educational and portfolio purposes**.

---

<p align="center">
  <b>Built with ❤️ for QA Automation Learning</b><br/>
  <sub>Java · Selenium · REST Assured · TestNG · Allure · GitHub Actions</sub>
</p>
