package com.tyss.optimize.nlp.web.program.browser;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "OpenSpecifiedBrowserUsingGrid")
public class OpenSpecifiedBrowserUsingGrid implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String hubURL = (String) attributes.get("hubURL");
            String browser_name = (String) attributes.get("browser_name");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*browser_name*", browser_name);
            String modifiedPassMessage = passMessage.replace("*browser_name*", browser_name);
            log.info("Browser is:" + browser_name);
            log.info("HUB is:" + hubURL);
            log.info("Opening " + browser_name + "Browser using grid");
            RemoteWebDriver remoteWebDriver = null;
            URL remoteAddress = new URL(hubURL);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--disable-notifications");
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser_name);
            capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
            remoteWebDriver = new RemoteWebDriver(remoteAddress, capabilities);

            log.info("Opening Browser: Driver SessionId" + remoteWebDriver.getSessionId());
            log.info("Opened " + browser_name + " Browser using grid");
            nlpResponseModel.setMessage(modifiedPassMessage);
            IDriver iDriver = new com.tyss.optimize.data.models.dto.drivers.WebDriver(remoteWebDriver);
            nlpResponseModel.setDriver(iDriver);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenSpecifiedBrowserUsingGrid ", exception);
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

        sb.append("URL remoteAddress = new URL(hubURL);\n");
        sb.append("ChromeOptions chromeOptions = new ChromeOptions();\n");
        sb.append("chromeOptions.addArguments(\"--disable-notifications\");\n");
        sb.append("DesiredCapabilities capabilities = new DesiredCapabilities();\n");
        sb.append("capabilities.setBrowserName(browser_name);\n");
        sb.append("capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);\n");
        sb.append("RemoteWebDriver remoteWebDriver = new RemoteWebDriver(remoteAddress, capabilities);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("hubURL::\"https://www.google.com/\"");
        params.add("browser_name::\"chrome\"");

        return params;
    }
}


