package com.medusa.automation.utils;

import com.medusa.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil — chụp screenshot khi test fail, lưu vào target/screenshots/
 */
public class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ScreenshotUtil() {}

    /**
     * Chụp screenshot và lưu vào thư mục screenshots
     *
     * @param driver WebDriver instance
     * @param testName Tên test (dùng cho tên file)
     * @return đường dẫn file screenshot, hoặc null nếu lỗi
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        if (!ConfigReader.getInstance().isScreenshotOnFail()) {
            return null;
        }

        try {
            String screenshotDir = ConfigReader.getInstance().getScreenshotDir();
            Path dirPath = Paths.get(screenshotDir);
            Files.createDirectories(dirPath);

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
            String safeName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = safeName + "_" + timestamp + ".png";
            Path filePath = dirPath.resolve(fileName);

            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(filePath, screenshot);

            log.info("Screenshot saved: {}", filePath.toAbsolutePath());
            return filePath.toAbsolutePath().toString();
        } catch (IOException e) {
            log.error("Không thể lưu screenshot cho test '{}': {}", testName, e.getMessage());
            return null;
        }
    }
}
