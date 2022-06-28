package com.tyss.optimize.nlp.web.webelement;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "WaitAttributeOfElementContainsString")
public class WaitAttributeOfElementContainsString implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String modifiedPassMessage, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String attribute = (String) attributes.get("attribute");
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
            String value = (String) attributes.get("value");
            Long time = (Long) attributes.get("time");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
			String [] elementSplit=elementName.split(":");
        	elementName=elementSplit[1];
        	elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*attribute*", attribute).replace("*elementName*", elementName).replace("*elementType*", elementType).replace("*value*", value);
            modifiedPassMessage = passMessage.replace("*attribute*", attribute).replace("*elementName*", elementName).replace("*elementType*", elementType).replace("*value*", value);
            log.info("Waiting till " + attribute + " attribute of " + elementName + " " + elementType + " contains " + value);

            WebDriverWait wait = new WebDriverWait(driver, time);
            wait.until(ExpectedConditions.attributeContains(element, attribute, value));
            log.info("Successfully waited " + attribute + " attribute of " + elementName + " " + elementType + " contains " + value);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in WaitAttributeOfElementContainsString ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if(containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("WebDriverWait wait = new WebDriverWait(driver, time);\n");
        sb.append("wait.until(ExpectedConditions.attributeContains(element, attribute, value));\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("time::200");
        params.add("attribute::\"xyz\"");
        params.add("value::\"abc\"");

        return params;
    }

}