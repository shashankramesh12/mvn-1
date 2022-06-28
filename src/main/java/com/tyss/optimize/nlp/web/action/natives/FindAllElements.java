package com.tyss.optimize.nlp.web.action.natives;

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

@Slf4j
@Component(value = "FindAllElements")
public class FindAllElements implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        List<WebElement> list = null;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String locatorType = (String) attributes.get("locatorType");
            WebElement locatorValue = (WebElement) attributes.get("locatorValue");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Finding all elements by locator " + locatorType);
            String modifiedPassMessage = passMessage.replace("*locatorType*", locatorType);
            modifiedFailMessage = failMessage.replace("*locatorType*", locatorType);
            By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);
            list = driver.findElements(b);
            log.info("Successfully captured all elements by locator " + locatorType);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in FindAllElements ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("list", list);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("try {\n");
        sb.append(" By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);\n");
        sb.append(" List<WebElement> list = driver.findElements(b);\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("locatorType::\"id\"");
        params.add("locatorValue::\"randomId\"");

        return params;
    }

}
