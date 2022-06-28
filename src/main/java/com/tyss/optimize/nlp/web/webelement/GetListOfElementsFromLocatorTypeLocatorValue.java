package com.tyss.optimize.nlp.web.webelement;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component(value = "GetListOfElementsFromLocatorTypeLocatorValue")
public class GetListOfElementsFromLocatorTypeLocatorValue implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null, failMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        List<WebElement> elements = new ArrayList<>();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            WebDriver driver = null;
            if (Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            String locatorType = (String) attributes.get("locatorType");
            String locatorValue = (String) attributes.get("locatorValue");
            By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);
            elements = driver.findElements(b);
            if(Objects.nonNull(elements)){
                log.info("Fetched " + elements.size() + " elements from locator type " + locatorType + " locator value " + locatorValue);
                modifiedPassMessage = passMessage.replace("*returnValue*", String.valueOf(elements));
                nlpResponseModel.setStatus(CommonConstants.pass);
                nlpResponseModel.setMessage(modifiedPassMessage);
            }else {
                log.error("Failed to fetch list of elements from locatorType " + locatorType + " locatorValue " + locatorValue);
                modifiedFailMessage = failMessage.replace("*locatorType*", locatorType);
                modifiedFailMessage = modifiedFailMessage.replace("*locatorValue*", locatorValue);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setMessage(modifiedFailMessage);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetListOfElementsFromLocatorTypeLocatorValue ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    @Override
    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);\n");
        sb.append("List<WebElement> elements = driver.findElements(b);\n");
        sb.append("if(Objects.nonNull(elements)){\n");
        sb.append("	System.out.println(\"Fetched \" + elements.size() + \" elements from locator Type \" + locatorType + \" locator Value \" + locatorValue);\n");
        sb.append("}else {\n");
        sb.append("	System.out.println(\"Failed to fetch list of elements from locator Type \" + locatorType + \" locator Value \" + locatorValue);\n");
        sb.append("}\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("locatorType::\"id\"");
        params.add("locatorValue::\"xyz\"");

        return params;
    }
}
