package com.tyss.optimize.nlp.web.action.builtin.listbox.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetWrappedElement")
public class GetWrappedElement implements Nlp {

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
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
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
            log.info("Getting all the options by wrapping them as single element from " + elementName + " " + elementType);
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            WebElement wrappedElement = null;
            Select select = new Select(element);
            wrappedElement = select.getWrappedElement();
            log.info("Captured wrapped option from " + elementName + " " + elementType);
            nlpResponseModel.getAttributes().put("wrappedElement", wrappedElement);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetWrappedElement ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("Select select = new Select(element);\n");
        sb.append("WebElement wrappedElement = select.getWrappedElement();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");

        return params;
    }
}
