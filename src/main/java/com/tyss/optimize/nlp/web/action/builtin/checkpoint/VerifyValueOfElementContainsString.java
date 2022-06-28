package com.tyss.optimize.nlp.web.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component(value = "VerifyValueOfElementContainsString")
public class VerifyValueOfElementContainsString implements Nlp {

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
            String expectedValue = (String) attributes.get("expectedValue");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying value of " + elementName + " " + elementType + " contains " + expectedValue);
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*expectedValue*", expectedValue);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*expectedValue*", expectedValue);
            String details = "";
            Boolean match = false;
            String actualValue = element.getAttribute("value");
            details = "Expected Value:" + expectedValue + "\n" + "Actual    Value:" + actualValue;
            log.info(details);
            if (caseSensitive) {
                match = actualValue.contains(expectedValue);
                log.info("Case Sensitive comparison, result is:" + match);
            } else {

                match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualValue, expectedValue);
                log.info("Case Insensitive comparison, result is:" + match);
            }

            if (match) {
                log.info("The value of " + elementName + " " + elementType + " contains " + expectedValue);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The value of " + elementName + " " + elementType + " does not contain " + expectedValue);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyValueOfElementContainsString ", exception);
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

        sb.append("boolean match = false;\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String actualValue = element.getAttribute(\"value\");\n");
        sb.append("if (caseSensitive) {\n");
        sb.append("	match = actualValue.contains(expectedValue);\n");
        sb.append("} else {\n");
        sb.append("	match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualValue, expectedValue);\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(\"The value contains \" + expectedValue);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The value does not contain \" + expectedValue);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("expectedValue::\"xyz\"");
        params.add("caseSensitive::true");

        return params;
    }
}
