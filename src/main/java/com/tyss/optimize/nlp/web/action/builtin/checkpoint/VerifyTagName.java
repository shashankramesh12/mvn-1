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
@Component(value = "VerifyTagName")
public class VerifyTagName implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String modifiedFailMessage = null;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            String tagName = (String) attributes.get("tagName");
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
            log.info("Verify tagName of " + elementName + " " + elementType + " is " + tagName);
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*tagName*", tagName);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*tagName*", tagName);
            String actualTagName = element.getTagName();
            if (actualTagName.equals(tagName)) {
                log.info("The tagName of " + elementName + " " + elementType + " is " + tagName);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The tagName of " + elementName + " " + elementType + " is not " + tagName);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyTagName ", exception);
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

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String actualTagName = element.getTagName();\n");
        sb.append("if (actualTagName.equals(TagName)) {\n");
        sb.append("	System.out.println(\"The tagName is \" + TagName);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The tagName is not \" + TagName);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("TagName::\"xyz\"");

        return params;
    }
}
