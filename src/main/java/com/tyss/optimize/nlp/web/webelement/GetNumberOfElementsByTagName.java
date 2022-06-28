package com.tyss.optimize.nlp.web.webelement;

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
@Component(value = "GetNumberOfElementsByTagName")
public class GetNumberOfElementsByTagName implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String modifiedPassMessage, modifiedFailMessage = null;
        int number = 0;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String tagName = (String) attributes.get("tagName");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            ifCheckPointIsFailed = null;
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*tagName*", tagName);
            modifiedPassMessage = passMessage.replace("*tagName*", tagName);
            log.info("Fetching number of elements by tagName " + tagName);

            number = driver.findElements(By.tagName(tagName)).size();
            log.info("Successfully fetched number of elements by tagName " + tagName);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("number", number);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetNumberOfElementsByTagName ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("int number = driver.findElements(By.tagName(TagName)).size();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("TagName::\"xyz\"");

        return params;
    }

}