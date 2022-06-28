package com.tyss.optimize.nlp.web.action.builtin.mouse;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.natives.Click;
import com.tyss.optimize.nlp.web.action.natives.GetAttribute;
import com.tyss.optimize.nlp.web.action.natives.GetTagName;
import com.tyss.optimize.nlp.web.action.natives.IsSelected;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "SelectTheCheckBox")
public class SelectTheCheckBox implements Nlp {

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

            NlpRequestModel requestAttribute = new NlpRequestModel();
            requestAttribute.getAttributes().put("containsChildNlp", true);
            requestAttribute.getAttributes().put("elementName", elementName);
            requestAttribute.getAttributes().put("elementType", elementType);
            requestAttribute.getAttributes().put("element", element);
            requestAttribute.getAttributes().put("attributeName", "type");
            requestAttribute.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestAttribute.setPassMessage("Attribute *attributeName* value of *elementName* *elementType* is *returnValue*");
            requestAttribute.setFailMessage("Failed to capture attribute *attributeName* value of *elementName* *elementType*");

            NlpRequestModel requestSelected = new NlpRequestModel();
            requestSelected.getAttributes().put("containsChildNlp", true);
            requestSelected.getAttributes().put("elementName", elementName);
            requestSelected.getAttributes().put("elementType", elementType);
            requestSelected.getAttributes().put("element", element);
            requestSelected.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSelected.setPassMessage("*elementName* *elementType** is selected");
            requestSelected.setFailMessage("*elementName* *elementType* is not selected");

            NlpRequestModel requestClick = new NlpRequestModel();
            requestClick.getAttributes().put("containsChildNlp", true);
            requestClick.getAttributes().put("elementName", elementName);
            requestClick.getAttributes().put("elementType", elementType);
            requestClick.getAttributes().put("element", element);
            requestClick.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestClick.setPassMessage("Clicked on *elementName* *elementType* in *elementPage* page");
            requestClick.setFailMessage("Failed to click on *elementName* *elementType* in *elementPage* page");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            String tag = (String) ((NlpResponseModel) new GetTagName().execute(requestTagName)).getAttributes().get("tagName");
            String type = (String) ((NlpResponseModel) new GetAttribute().execute(requestAttribute)).getAttributes().get("value");

            log.info("Selecting " + elementName + " " + elementType);

            if (!(tag.equalsIgnoreCase("input") && type.equalsIgnoreCase("checkbox"))) {
                log.error("InvalidElementTypeException: Element should be checkbox");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else {
                Boolean selected = (Boolean) ((NlpResponseModel) new IsSelected().execute(requestSelected))
                        .getAttributes().get("selected");
                if (selected) {
                    log.info("Check Box is already selected");
                } else {
                    log.info("Check Box is not already selected; selecting it");
                    new Click().execute(requestClick);
                }
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                log.info("Selected " + elementName + " " + elementType);
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in SelectTheCheckBox ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
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
        sb.append("String tagName = element.getTagName();\n");
        sb.append("String valueType = element.getAttribute(\"type\");\n");
        sb.append("if (!(tagName.equalsIgnoreCase(\"input\") && valueType.equalsIgnoreCase(\"checkbox\"))) {\n");
        sb.append("	System.out.println(\"InvalidElementTypeException: Element should be checkbox\");\n");
        sb.append("	} else {\n");
        sb.append("	boolean selected = element.isSelected();\n");
        sb.append("	if (selected) {\n");
        sb.append("		System.out.println(\"Check Box is already selected\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"Check Box is not already selected; selecting it\");\n");
        sb.append("		element.click();\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.xpath(\"//h5[text()='Elements']\")");

        return params;
    }
}
