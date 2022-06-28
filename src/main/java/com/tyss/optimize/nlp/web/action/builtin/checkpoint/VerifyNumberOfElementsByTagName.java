package com.tyss.optimize.nlp.web.action.builtin.checkpoint;


import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component(value = "VerifyNumberOfElementsByTagName")
public class VerifyNumberOfElementsByTagName implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String tagName = (String) attributes.get("tagName");
            Integer expectedCount = (Integer) attributes.get("expectedCount");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verify number of elements by tagname " + tagName + " are " + expectedCount);
            String modifiedPassMessage = passMessage.replace("*tagName*", tagName)
                    .replace("*expectedCount*", String.valueOf(expectedCount));
            modifiedFailMessage = failMessage.replace("*tagName*", tagName)
                    .replace("*expectedCount*", String.valueOf(expectedCount));
            int count = driver.findElements(By.tagName(tagName)).size();
            if (count == expectedCount) {
                log.info("The number of elements by tagname " + tagName + " are " + expectedCount);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The number of elements by tagname " + tagName + " are not " + expectedCount);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyNumberOfElementsByTagName ", exception);
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

        sb.append("int count = driver.findElements(By.tagName(TagName)).size();\n");
        sb.append("if (count == expectedCount) {\n");
        sb.append("	System.out.println(\"The number of elements by tagname \" + TagName + \" are \" + expectedCount);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The number of elements by tagname \" + TagName + \" are not \" + expectedCount);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("TagName::\"xyz\"");
        params.add("expectedCount::30");

        return params;
    }
}
