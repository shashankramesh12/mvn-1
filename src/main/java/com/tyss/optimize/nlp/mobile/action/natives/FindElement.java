package com.tyss.optimize.nlp.mobile.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_FindElement")
public class FindElement implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        WebElement element = null;
        WebElement webElement = null;
        By b = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            String locatorType = (String) attributes.get("locatorType");
            String locatorValue = (String) attributes.get("locatorValue");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            elementType=elementType.concat(" in "+elementSplit[0] + " screen ");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedPassMessage = passMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            log.info("Finding the child element of " + elementName + " " + elementType);
            b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);
            log.info("Successfully found child element of " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            webElement = element.findElement(b);
            nlpResponseModel.getAttributes().put("element", webElement);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in FindElement ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("try {\n");
        sb.append(" WebElement element = androidDriver.findElement(ELEMENT);\n");
        sb.append(" By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);\n");
        sb.append(" WebElement webElement = element.findElement(b);\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"in.mohalla.sharechat:id/et_phone\")");
        params.add("locatorType::\"id\"");
        params.add("locatorValue::\"randomId\"");

        return params;
    }

}
