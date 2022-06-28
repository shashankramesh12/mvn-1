package com.tyss.optimize.nlp.web.webelement;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "WaitTillElementIsClickable")
public class WaitTillElementIsClickable implements Nlp {

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
        String elementName = (String) attributes.get("elementName");
        String elementType = (String) attributes.get("elementType");
        WebElement element = (WebElement) attributes.get("element");
        Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
        containsChildNlp = (Boolean) attributes.get("containsChildNlp");
        String [] elementSplit=elementName.split(":");
        elementName=elementSplit[1];
        elementType=elementType.concat(" in "+elementSplit[0] + " page ");
        String passMessage = nlpRequestModel.getPassMessage();
        String failMessage = nlpRequestModel.getFailMessage();
        ifCheckPointIsFailed = null;
        if (attributes.get("ifCheckPointIsFailed") != null) {
            String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
            ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
        }
        modifiedFailMessage = failMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
        modifiedPassMessage = passMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
        log.info("Waiting till " + elementName + " " + elementType + " is clickable");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(explicitTimeOut));
            wait.until(ExpectedConditions.elementToBeClickable(element));
            log.info("Successfully waited till " + elementName + " " + elementType + " is clickable");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in WaitTillElementIsClickable ", exception);
            String exceptionSimpleName=exception.getClass().getSimpleName();
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
        sb.append("WebDriverWait wait = new WebDriverWait(driver, explicitTimeOut);\n");
        sb.append("wait.until(ExpectedConditions.elementToBeClickable(element));\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("explicitTimeOut::200");

        return params;
    }

}