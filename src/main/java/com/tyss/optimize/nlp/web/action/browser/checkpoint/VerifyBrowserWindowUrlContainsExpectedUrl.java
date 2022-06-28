package com.tyss.optimize.nlp.web.action.browser.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyBrowserWindowUrlContainsExpectedUrl")
public class VerifyBrowserWindowUrlContainsExpectedUrl implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String expectedUrl = (String) attributes.get("expectedUrl");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying browser window url contains " + expectedUrl);
            String modifiedPassMessage = passMessage.replace("*expectedUrl*", expectedUrl);
            modifiedFailMessage = failMessage.replace("*expectedUrl*", expectedUrl);
            String actualUrl = driver.getCurrentUrl();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (actualUrl.contains(expectedUrl)) {
                log.info("Browser window url contains " + expectedUrl);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Browser window url contains " + expectedUrl);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyBrowserWindowUrlContainsExpectedUrl ", exception);
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

        sb.append("String actualUrl = driver.getCurrentUrl();\n");
        sb.append("if (actualUrl.contains(expectedUrl)) {\n");
        sb.append("	System.out.println(\"Browser window url contains \" + expectedUrl);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Browser window url does not contains \" + expectedUrl);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedUrl::\"https://www.google.com/\"");

        return params;
    }

}
