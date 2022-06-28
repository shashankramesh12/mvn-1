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
@Component(value = "OpenStandaloneBrowser")
public class OpenStandaloneBrowser implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String browser_name = (String) attributes.get("browser_name");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            WebDriver driver = null;
            modifiedFailMessage = failMessage.replace("*browser_name*", browser_name);
            String modifiedPassMessage = passMessage.replace("*browser_name*", browser_name);
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Opening standalone Browser " + browser_name);
            if ("firefox".equalsIgnoreCase(browser_name)) {
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addPreference("dom.webnotifications.enabled", false);
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(firefoxOptions);
            } else if ("IE".equalsIgnoreCase(browser_name)) {
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
            } else {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--disable-notifications");
                WebDriverManager.chromedriver().arch64().arch32().setup();
                driver = new ChromeDriver(chromeOptions);
            }
            log.info("Opened standalone Browser " + browser_name);
            nlpResponseModel.setMessage(modifiedPassMessage);
            IDriver iDriver = new com.tyss.optimize.data.models.dto.drivers.WebDriver(driver);
            nlpResponseModel.setDriver(iDriver);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenStandaloneBrowser ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
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

        sb.append("if (\"firefox\".equalsIgnoreCase(browser_name)) {\n");
        sb.append("	FirefoxOptions firefoxOptions = new FirefoxOptions();\n");
        sb.append("	firefoxOptions.addPreference(\"dom.webnotifications.enabled\", false);\n");
        sb.append("	WebDriverManager.firefoxdriver().setup();\n");
        sb.append("	driver = new FirefoxDriver(firefoxOptions);\n");
        sb.append("} else if (\"IE\".equalsIgnoreCase(browser_name)) {\n");
        sb.append("	WebDriverManager.iedriver().setup();\n");
        sb.append("	driver = new InternetExplorerDriver();\n");
        sb.append("} else {\n");
        sb.append("	ChromeOptions chromeOptions = new ChromeOptions();\n");
        sb.append("	chromeOptions.addArguments(\"--disable-notifications\");\n");
        sb.append("	WebDriverManager.chromedriver().arch64().arch32().setup();\n");
        sb.append("	driver = new ChromeDriver(chromeOptions);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("browser_name::\"chrome\"");

        return params;
    }
}
