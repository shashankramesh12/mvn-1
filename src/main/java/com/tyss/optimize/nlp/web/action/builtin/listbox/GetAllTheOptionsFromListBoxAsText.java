package com.tyss.optimize.nlp.web.action.builtin.listbox;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.builtin.listbox.natives.GetOptions;
import com.tyss.optimize.nlp.web.webelement.GetTextFromListOfWebElements;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetAllTheOptionsFromListBoxAsText")
public class GetAllTheOptionsFromListBoxAsText implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        List<String> allText = new ArrayList<>();
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

            NlpRequestModel requestOptions = new NlpRequestModel();
            requestOptions.getAttributes().put("elementName", elementName);
            requestOptions.getAttributes().put("elementType", elementType);
            requestOptions.getAttributes().put("element", element);
            requestOptions.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestOptions.setPassMessage("Captured all options from *elementName* *elementType*");
            requestOptions.setFailMessage("Failed to get all options from *elementName* *elementType*");

            NlpRequestModel requestText = new NlpRequestModel();
            requestText.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestText.setPassMessage("Successfully captured text from each webelements");
            requestText.setFailMessage("Failed to capture text from each webelements");

            log.info("Capturing all options from " + elementName + " " + elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            Boolean status = (Boolean) ((NlpResponseModel) new IsItListBox().execute(requestListbox))
                    .getAttributes().get("isItListBox");

            if (status) {
                List<WebElement> allOptions = (List<WebElement>) ((NlpResponseModel) new GetOptions().execute(requestOptions))
                        .getAttributes().get("elementList");
                requestText.getAttributes().put("allElements", allOptions);
                allText = (List<String>) ((NlpResponseModel) new GetTextFromListOfWebElements().execute(requestText))
                        .getAttributes().get("allText");
                String modifiedPassMessage = passMessage.replace("*elementName*", modElementName)
                        .replace("*elementType*", modElementType).replace("*returnValue*", String.valueOf(allText));
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                log.info("Captured all options from " + elementName + " " + elementType);
            } else {
                log.error("Failed to get all options from " + elementName + " " + elementType);
                log.error("This action cannot be performed on NON-ListBox element");
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
            log.error("NLP_EXCEPTION in GetAllTheOptionsFromListBoxAsText ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        StringBuilder strBuilder = new StringBuilder();
        int count = 0;
        for (String options : allText) {
            strBuilder.append(options);
            count++;
            if (count <= allText.size() - 1)
                strBuilder.append(",");

        }
        String allPlainText = strBuilder.toString();
        nlpResponseModel.getAttributes().put("allPlainText", allPlainText);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean isItListBox = false;\n");
        sb.append("List<String> allText = new ArrayList<>();\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String tagName = element.getTagName();\n");
        sb.append("if (tagName.equals(\"select\")) {\n");
        sb.append("	isItListBox = true;\n");
        sb.append("} else {\n");
        sb.append("	isItListBox = false;\n");
        sb.append("}\n");
        sb.append("if(isItListBox){\n");
        sb.append("	List<WebElement> elementList = new ArrayList<>();\n");
        sb.append("	Select select = new Select(element);\n");
        sb.append("	elementList = select.getOptions();\n");
        sb.append("	for (WebElement element : elementList) {\n");
        sb.append("		String text = element.getText();\n");
        sb.append("		System.out.println(\"Adding the text:\" + text + \" to ArrayList\");\n");
        sb.append("		allText.add(text);\n");
        sb.append("		System.out.println(\"Successfully captured text from each webElements\");\n");
        sb.append("	}\n");
        sb.append("	StringBuilder strBuilder = new StringBuilder();\n");
        sb.append("	int count = 0;\n");
        sb.append("	for (String options : allText) {\n");
        sb.append("		strBuilder.append(options);\n");
        sb.append("		count++;\n");
        sb.append("		if (count <= allText.size() - 1)\n");
        sb.append("			strBuilder.append(\",\");\n");
        sb.append("	}\n");
        sb.append("	String allPlainText = strBuilder.toString();\n");
        sb.append("}else{\n");
        sb.append("System.out.println(\"This action cannot be performed on NON-ListBox element\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");

        return params;
    }
}
