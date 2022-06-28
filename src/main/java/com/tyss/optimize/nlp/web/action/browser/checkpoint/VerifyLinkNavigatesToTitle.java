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
@Component(value = "VerifyLinkNavigatesToTitle")
public class VerifyLinkNavigatesToTitle implements Nlp {

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
            String title = (String) attributes.get("title");
            String url = (String) attributes.get("url");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying if " + url + " navigates to " + title + " page");
            String modifiedPassMessage = passMessage.replace("*url*", url).replace("*title*", title);
            modifiedFailMessage = failMessage.replace("*url*", url).replace("*title*", title);
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            driver.navigate().to(url);
            String actualTitle = driver.getTitle();
            if (actualTitle.equals(title)) {
                log.info(url + " navigates to " + title + " page");
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(url + " does not navigate to " + title + " page");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyLinkNavigatesToTitle ", exception);
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

        sb.append("driver.navigate().to(url);\n");
        sb.append("String actualTitle = driver.getTitle();\n");
        sb.append("if (actualTitle.equals(title)) {\n");
        sb.append("	System.out.println(url + \" navigates to \" + title + \" page\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(url + \" does not navigate to \" + title + \" page\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("url::\"https://www.google.com/\"");
        params.add("title::\"xyz\"");

        return params;
    }

}
