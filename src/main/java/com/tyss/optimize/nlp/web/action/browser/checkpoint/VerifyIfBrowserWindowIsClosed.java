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
@Component(value = "VerifyIfBrowserWindowIsClosed")
public class VerifyIfBrowserWindowIsClosed implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String exceptionName = "";
        String passMessage = null;
        String failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying if Browser is closed");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            driver.getWindowHandle();
        } catch (Exception exception) {
            exceptionName = exception.getClass().getCanonicalName();
        }
        if (exceptionName.equals("org.openqa.selenium.NoSuchSessionException")) {
            log.info("Browser window is closed");
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } else {
            log.error("Browser window is not closed");
            nlpResponseModel.setMessage(failMessage);
            nlpResponseModel.setStatus(CommonConstants.fail);
            nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("String exceptionName = \"\";\n");
        sb.append("try {\n");
        sb.append("	driver.getWindowHandle();\n");
        sb.append("} catch (Exception exception) {\n");
        sb.append("	exceptionName = exception.getClass().getCanonicalName();\n");
        sb.append("}\n");
        sb.append("if (exceptionName.equals(\"org.openqa.selenium.NoSuchSessionException\")) {\n");
        sb.append("	System.out.println(\"Browser window is closed\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Browser window is not closed\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
