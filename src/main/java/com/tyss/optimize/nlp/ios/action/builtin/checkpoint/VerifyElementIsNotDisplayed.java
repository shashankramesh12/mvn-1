package com.tyss.optimize.nlp.ios.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_VerifyElementIsNotDisplayed")
public class VerifyElementIsNotDisplayed implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
		    String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            elementType=elementType.concat(" in "+elementSplit[0] + " screen ");
        
            IOSDriver iosDriver = (IOSDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            log.info("Verifying if element " + elementName + " " + elementType + " is not displayed");
            WebDriverWait wait = new WebDriverWait(iosDriver, explicitTimeOut);
            wait.until(ExpectedConditions.invisibilityOf(element));
            log.info(elementName + " " + elementType + " is not displayed");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyElementIsNotDisplayed ", exception);
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

        sb.append("WebElement element = iosDriver.findElement(ELEMENT);\n");
        sb.append("WebDriverWait wait = new WebDriverWait(iosDriver, explicitTimeOut);\n");
        sb.append("wait.until(ExpectedConditions.invisibilityOf(element));\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("explicitTimeOut::20");

        return params;
    }

}
