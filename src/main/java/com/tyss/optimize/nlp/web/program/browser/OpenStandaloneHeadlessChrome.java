package com.tyss.optimize.nlp.web.program.browser;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "OpenStandaloneHeadlessChrome")
public class OpenStandaloneHeadlessChrome implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String browse_name = "chrome";
            WebDriver driver = null;
            String passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Opening standalone headless chrome Browser");
            if ("firefox".equalsIgnoreCase(browse_name)) {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(firefoxOptions);
            } else if ("IE".equalsIgnoreCase(browse_name)) {
                // InternetExplorerDriverManager.iedriver().version(driver_version).setup();
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
            } else {
                // ChromeDriverManager.chromedriver().version(driver_version).setup();
                WebDriverManager.chromedriver().arch64().arch32().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-notifications");
                // disable the info bar
                options.addArguments("--disable-infobars");
                options.addArguments("--headless");
                driver = new ChromeDriver(options);
            }
            log.info("Opened standalone headless chrome Browser");
            nlpResponseModel.setMessage(passMessage);
            IDriver iDriver = new com.tyss.optimize.data.models.dto.drivers.WebDriver(driver);
            nlpResponseModel.setDriver(iDriver);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CloseAllBrowsers ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("String browse_name = \"chrome\";\n");
        sb.append("if (\"firefox\".equalsIgnoreCase(browse_name)) {\n");
        sb.append("	FirefoxOptions firefoxOptions = new FirefoxOptions();\n");
        sb.append("	firefoxOptions.addPreference(\"dom.webnotifications.enabled\", false);\n");
        sb.append("	WebDriverManager.firefoxdriver().setup();\n");
        sb.append("	driver = new FirefoxDriver(firefoxOptions);\n");
        sb.append("} else if (\"IE\".equalsIgnoreCase(browse_name)) {\n");
        sb.append("	WebDriverManager.iedriver().setup();\n");
        sb.append("	driver = new InternetExplorerDriver();\n");
        sb.append("} else {\n");
        sb.append("	WebDriverManager.chromedriver().arch64().arch32().setup();\n");
        sb.append("	ChromeOptions options = new ChromeOptions();\n");
        sb.append("	options.addArguments(\"--disable-notifications\");\n");
        sb.append("	options.addArguments(\"--disable-infobars\");\n");
        sb.append("	options.addArguments(\"--headless\");\n");
        sb.append("	driver = new ChromeDriver(options);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }
}
