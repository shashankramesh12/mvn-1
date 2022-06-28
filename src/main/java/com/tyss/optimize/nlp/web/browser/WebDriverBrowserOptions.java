package com.tyss.optimize.nlp.web.browser;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Data;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

import java.util.HashMap;
import java.util.Map;

@Data
public class WebDriverBrowserOptions {

    public static WebDriver getBrowserOption(String driverType, String driverPath, String task) {
        switch (driverType) {
            case "chrome":
                Map<String, Object> chromePrefs = new HashMap<>();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("use-fake-ui-for-media-stream");
                new WebDriverBrowserOptions().doActionAsPerTask(task, chromePrefs);
                chromeOptions.setExperimentalOption("prefs", chromePrefs);
                System.setProperty(driverType, driverPath);
                return new ChromeDriver(chromeOptions);

            case "firefox":
                Map<String, Object> firefoxPrefs = new HashMap<>();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                WebDriverManager.firefoxdriver().setup();
                firefoxOptions.addArguments("use-fake-ui-for-media-stream");
                new WebDriverBrowserOptions().doActionAsPerTask(task, firefoxPrefs);
                System.setProperty(driverType, driverPath);
                return new FirefoxDriver(firefoxOptions);

            case "msedge":
                Map<String, Object> edgePrefs = new HashMap<>();
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("use-fake-ui-for-media-stream");
                new WebDriverBrowserOptions().doActionAsPerTask(task, edgePrefs);
                edgeOptions.setExperimentalOption("prefs", edgePrefs);
                System.setProperty(driverType, driverPath);
                return new EdgeDriver(edgeOptions);

            case "iexplorer":
                Map<String, Object> iePrefs = new HashMap<>();
                InternetExplorerOptions ieOptions = new InternetExplorerOptions();

                new WebDriverBrowserOptions().doActionAsPerTask(task, iePrefs);

                System.setProperty(driverType, driverPath);
                return new InternetExplorerDriver(ieOptions);

            default:
                return null;
        }
    }

    private void doActionAsPerTask(String task, Map<String, Object> prefs) {
        switch (task) {
            case "enable-camera":
                prefs.put("profile.default_content_setting_values.media_stream_camera", 1);
            case "enable-microphone":
                prefs.put("profile.default_content_setting_values.media_stream_mic", 1);
            case "enable-geolocation":
                prefs.put("profile.default_content_setting_values.geolocation", 1);
            case "enable-notifications":
                prefs.put("profile.default_content_setting_values.notifications", 1);
            case "disable-camera":
                prefs.put("profile.default_content_setting_values.media_stream_camera", 2);
            case "disable-microphone":
                prefs.put("profile.default_content_setting_values.media_stream_mic", 2);
            case "disable-geolocation":
                prefs.put("profile.default_content_setting_values.geolocation", 2);
            case "disable-notifications":
                prefs.put("profile.default_content_setting_values.notifications", 2);
            default:
                System.out.println("Inappropriate task!!");
        }
    }
}
