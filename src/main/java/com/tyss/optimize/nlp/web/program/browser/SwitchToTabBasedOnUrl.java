package com.tyss.optimize.nlp.web.program.browser;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component(value = "SwitchToTabBasedOnUrl")
public class SwitchToTabBasedOnUrl implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        String currentWindow = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver webDriver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            IDriver driver = nlpRequestModel.getDriver();
            String url = (String) attributes.get("url");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            boolean tabFound = false;
            NlpRequestModel requestCurrent = new NlpRequestModel();
            requestCurrent.getAttributes().put("containsChildNlp", true);
            requestCurrent.setDriver(driver);
            requestCurrent.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestCurrent.setPassMessage("Current Browser window id is *returnValue*");
            requestCurrent.setFailMessage("Failed to capture current Browser window id");

            NlpRequestModel requestAll = new NlpRequestModel();
            requestAll.getAttributes().put("containsChildNlp", true);
            requestAll.setDriver(driver);
            requestAll.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestAll.setPassMessage("Successfully captured all Browser windows id");
            requestAll.setFailMessage("Failed to capture all Browser windows id");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Switching to new tab where url of tab is " + url);
            String modifiedPassMessage = passMessage.replace("*urlOfNewTab*", url);
            modifiedFailMessage = failMessage.replace("*urlOfNewTab*", url);
            currentWindow = (String) ((NlpResponseModel) new GetCurrentWindowHandle().execute(requestCurrent)).getAttributes().get("wh");
            Set<String> allWindowHandles = (Set<String>) ((NlpResponseModel) new GetAllWindowHandles().execute(requestAll)).getAttributes().get("allwh");
            allWindowHandles.remove(currentWindow);
            for (String windowHandle : allWindowHandles) {
                String actualUrl = webDriver.switchTo().window(windowHandle).getCurrentUrl();
                log.info("URL is:" + actualUrl);
                if (actualUrl.equalsIgnoreCase(url)) {
                    log.info("URL is matching; hence switching and stopping the iteration");
                    tabFound = true;
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                } else {
                    log.info("URL of tab is not matching , checking next tab");
                }
            }

            if (!tabFound) {
                log.error("Specified Tab not found");
                webDriver.switchTo().window(currentWindow);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("currentWindow", currentWindow);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean tabFound = false;\n");
        sb.append("String currentWindow = driver.getWindowHandle();\n");
        sb.append("Set<String> allWindowHandles = driver.getWindowHandles();\n");
        sb.append("allWindowHandles.remove(currentWindow);\n");
        sb.append("for (String windowHandle : allWindowHandles) {\n");
        sb.append("	String actualUrl = driver.switchTo().window(windowHandle).getCurrentUrl();\n");
        sb.append("	if (actualUrl.equalsIgnoreCase(url)) {\n");
        sb.append("		tabFound = true;\n");
        sb.append("		System.out.println(\"Switched to child tab where URL contains \" + url);\n");
        sb.append("		break;\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"URL of tab is not matching , checking next tab\");\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (!tabFound) {\n");
        sb.append("	System.out.println(\"Specified Tab not found\");\n");
        sb.append("	driver.switchTo().window(currentWindow);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("url::\"https://www.google.com/\"");

        return params;
    }
}
