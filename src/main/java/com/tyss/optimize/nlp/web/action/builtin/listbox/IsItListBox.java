package com.tyss.optimize.nlp.web.action.builtin.listbox;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.natives.GetTagName;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IsItListBox")
public class IsItListBox implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        boolean isItListBox = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
			String [] elementSplit=elementName.split(":");
        	String modElementName=elementSplit[1];
      		String  modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestTagName = new NlpRequestModel();
            requestTagName.getAttributes().put("containsChildNlp", true);
            requestTagName.getAttributes().put("elementName", elementName);
            requestTagName.getAttributes().put("elementType", elementType);
            requestTagName.getAttributes().put("element", element);
            requestTagName.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestTagName.setPassMessage("The tag name of *elementName* *elementType* is *returnValue*");
            requestTagName.setFailMessage("Failed to capture tag name of *elementName* *elementType*");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("checking if the element " + elementName + " " + elementType + " is ListBox or not");
            String modifiedPassMessage = passMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            String tagName = (String) ((NlpResponseModel) new GetTagName().execute(requestTagName))
                    .getAttributes().get("tagName");
            if (tagName.equals("select")) {
                isItListBox = true;
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(elementName + " " + elementType + " is NOT a ListBox");
                isItListBox = false;
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in IsItListBox ", exception);
            isItListBox = false;
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("isItListBox", isItListBox);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String tagName = element.getTagName();\n");
        sb.append("if (tagName.equals(\"select\")) {\n");
        sb.append("	System.out.println(\"The element is a ListBox\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The element is not a ListBox\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");

        return params;
    }
}
