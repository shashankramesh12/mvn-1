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
@Component(value = "OpenHeadlessChromeUsingGrid")
public class OpenHeadlessChromeUsingGrid implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String hubURL = (String) nlpRequestModel.getUrl();
            String browse_name = (String) attributes.get("browse_name");
            RemoteWebDriver remoteWebDriver = null;
            String passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("HUB is:" + hubURL);
            log.info("Browser is:" + browse_name);
            URL remoteAddress = new URL(hubURL);
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browse_name);
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-notifications");
            // disable the info bar
            options.addArguments("--disable-infobars");
            options.addArguments("--headless");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            remoteWebDriver = new RemoteWebDriver(remoteAddress, capabilities);
            log.info("Opened headless chrome Browser using grid");
            log.info("Opening Browser: Driver SessionId" + remoteWebDriver.getSessionId());
            nlpResponseModel.setMessage(passMessage);
            IDriver iDriver = new com.tyss.optimize.data.models.dto.drivers.WebDriver(remoteWebDriver);
            nlpResponseModel.setDriver(iDriver);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenHeadlessChromeUsingGrid ", exception);
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

        sb.append("URL remoteAddress = new URL(hubURL);\n");
        sb.append("DesiredCapabilities capabilities = new DesiredCapabilities();\n");
        sb.append("capabilities.setBrowserName(browse_name);\n");
        sb.append("ChromeOptions options = new ChromeOptions();\n");
        sb.append("options.addArguments(\"--disable-notifications\");\n");
        sb.append("options.addArguments(\"--disable-infobars\");\n");
        sb.append("options.addArguments(\"--headless\");\n");
        sb.append("capabilities.setCapability(ChromeOptions.CAPABILITY, options);\n");
        sb.append("RemoteWebDriver remoteWebDriver = new RemoteWebDriver(remoteAddress, capabilities);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("hubURL::\"https://www.google.com/\"");
        params.add("browse_name::\"chrome\"");

        return params;
    }
}
