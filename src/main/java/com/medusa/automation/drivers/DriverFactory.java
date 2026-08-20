package com.medusa.automation.drivers;

import com.medusa.automation.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory — tạo và cấu hình WebDriver instance
 * Desktop viewport 1920x1080 theo quy tắc automation
 */
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThread = new ThreadLocal<>();

    private DriverFactory() {}

    public static WebDriver createDriver() {
        ConfigReader config = ConfigReader.getInstance();
        String browser = config.getBrowser().toLowerCase();

        // Auto-force headless khi chạy trên CI (GitHub Actions set CI=true tự động)
        boolean isCI = "true".equalsIgnoreCase(System.getenv("CI"));
        boolean headless = isCI || config.isHeadless();
        if (isCI) {
            log.info("CI environment detected (CI=true) — forcing headless mode");
        }

        WebDriver driver;
        switch (browser) {
            case "firefox":
                driver = createFirefoxDriver(headless);
                break;
            case "chrome":
            default:
                driver = createChromeDriver(headless);
                break;
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        driver.manage().window().setSize(new Dimension(1920, 1080));

        driverThread.set(driver);
        log.info("Browser started: {} | headless={} | viewport=1920x1080", browser, headless);
        return driver;
    }

    private static WebDriver createChromeDriver(boolean headless) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        if (headless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return new FirefoxDriver(options);
    }

    public static WebDriver getDriver() {
        WebDriver driver = driverThread.get();
        if (driver == null) {
            throw new IllegalStateException("Driver chưa được khởi tạo. Gọi createDriver() trước.");
        }
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove();
            log.info("Browser đã đóng.");
        }
    }
}
