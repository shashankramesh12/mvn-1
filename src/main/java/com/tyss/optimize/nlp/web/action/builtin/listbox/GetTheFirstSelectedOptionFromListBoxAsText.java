package com.tyss.optimize.nlp.web.action.builtin.listbox;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.builtin.listbox.natives.GetFirstSelectedOption;
import com.tyss.optimize.nlp.web.action.natives.GetText;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetTheFirstSelectedOptionFromListBoxAsText")
public class GetTheFirstSelectedOptionFromListBoxAsText implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        String text = "";
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
        	String [] elementSplit=elementName.split(":");
        	String modElementName=elementSplit[1];
       		String modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestListbox = new NlpRequestModel();
            requestListbox.getAttributes().put("containsChildNlp", true);
            requestListbox.getAttributes().put("elementName", elementName);
            requestListbox.getAttributes().put("elementType", elementType);
            requestListbox.getAttributes().put("element", element);
            requestListbox.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestListbox.setPassMessage("*elementName* *elementType* is Listbox");
            requestListbox.setFailMessage(" *elementName* *elementType* is not Listbox");

            NlpRequestModel requestFirst = new NlpRequestModel();
            requestFirst.getAttributes().put("containsChildNlp", true);
            requestFirst.getAttributes().put("elementName", elementName);
            requestFirst.getAttributes().put("elementType", elementType);
            requestFirst.getAttributes().put("element", element);
            requestFirst.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestFirst.setPassMessage("Successfully captured first selected option of *elementName* *elementType*");
            requestFirst.setFailMessage("Failed to get first selected option from *elementName* *elementType*");

            NlpRequestModel requestText = new NlpRequestModel();
            requestText.getAttributes().put("containsChildNlp", true);
            requestText.getAttributes().put("elementName", elementName);
            requestText.getAttributes().put("elementType", elementType);
            requestText.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestText.setPassMessage("The text of *elementName* *elementType* is *returnValue*");
            requestText.setFailMessage("Failed to capture text from *elementName* *elementType*");

            log.info("Capturing first selected option from " + elementName + " " + elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            Boolean status = (Boolean) ((NlpResponseModel) new IsItListBox().execute(requestListbox))
                    .getAttributes().get("isItListBox");
            if (status) {
                WebElement option = (WebElement) ((NlpResponseModel) new GetFirstSelectedOption().execute(requestFirst))
                        .getAttributes().get("firstElement");
                requestText.getAttributes().put("element", option);
                text = (String) ((NlpResponseModel) new GetText().execute(requestText))
                        .getAttributes().get("text");
                String modifiedPassMessage = passMessage.replace("*elementName*", modElementName)
                        .replace("*elementType*", modElementType).replace("*returnValue", String.valueOf(text));
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                log.info("Captured first selected option from " + elementName + " " + elementType);
            } else {
                log.error("This action cannot be performed on NON-ListBox element");
                log.error("Failed to get first selected option from " + elementName + " " + elementType);
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
            log.error("NLP_EXCEPTION in GetTheFirstSelectedOptionFromListBoxAsText ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("text", text);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("String text = \"\";\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String tagName = element.getTagName();\n");
        sb.append("if (tagName.equals(\"select\")) {\n");
        sb.append("	System.out.println(\"The element is a ListBox\");\n");
        sb.append(" Select select = new Select(element);\n");
        sb.append(" WebElement firstElement = select.getFirstSelectedOption();\n");
        sb.append(" text = firstElement.getText();\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"This action cannot be performed on NON-ListBox element\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");

        return params;
    }
}
