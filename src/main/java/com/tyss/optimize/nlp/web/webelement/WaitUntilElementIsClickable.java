package com.tyss.optimize.nlp.web.webelement;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component(value = "WaitUntilElementIsClickable")
public class WaitUntilElementIsClickable implements Nlp {

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
            String elementName = attributes.get("elementName").toString();
            String elementType = attributes.get("elementType").toString();
            Object element = attributes.get("element");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            new WebDriverWait(driver, explicitTimeOut).until(ExpectedConditions.elementToBeClickable(By.name(elementName)));
            JavascriptExecutor ex = (JavascriptExecutor)driver;
            ex.executeScript("arguments[0].click()", element);
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Waited for" + elementName + elementType + "to be clickable ");
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName);
            modifiedPassMessage = modifiedPassMessage.replace("*elementType*", String.valueOf(elementType));
            modifiedFailMessage = failMessage.replace("*elementName*", elementName);
            modifiedFailMessage.replace("*elementType*", String.valueOf(elementType));
            log.info(elementName + elementType + "are clickable ");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in WaitUntilElementIsClickable ", exception);
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

    @Override
    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("WebDriverWait wait = new WebDriverWait(driver, explicitTimeOut);\n");
        sb.append("wait.until(ExpectedConditions.elementToBeClickable(By.name(elementName)));\n");
        sb.append("JavascriptExecutor ex = (JavascriptExecutor)driver;\n");
        sb.append("ex.executeScript(\"arguments[0].click()\", element);\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("explicitTimeOut::200");
        params.add("elementName::\"abc\"");

        return params;
    }
}
