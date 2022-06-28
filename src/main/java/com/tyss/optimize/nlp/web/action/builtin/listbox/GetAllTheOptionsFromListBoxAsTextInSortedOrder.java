package com.tyss.optimize.nlp.web.action.builtin.listbox;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetAllTheOptionsFromListBoxAsTextInSortedOrder")
public class GetAllTheOptionsFromListBoxAsTextInSortedOrder implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        List<String> allText = new ArrayList<>();
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
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
            log.info("Capturing all options from " + elementName + " " + elementType + " in sorted order");
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            Select select = new Select(element);
            List<WebElement> allOptions = select.getAllSelectedOptions();
            for (WebElement options : allOptions) {
                allText.add(options.getText());
            }
            if (caseSensitive) {
                log.info("Sorting the ListBox content (Case Sensitive)");
                Collections.sort(allText);
                log.info("Captured all options from " + elementName + " " + elementType + " in sorted order");
            } else {
                log.info("Sorting the ListBox content (Case In-Sensitive)");
                Collections.sort(allText, String.CASE_INSENSITIVE_ORDER);
                log.info("Captured all options from " + elementName + " " + elementType + " in sorted order");
            }
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            log.debug("ListBox content after sorting");
            for (String text : allText) {
                log.debug(text);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetAllTheOptionsFromListBoxAsTextInSortedOrder ", exception);
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
        nlpResponseModel.getAttributes().put("allText", allText);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("List<String> allText = new ArrayList<>();\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("Select select = new Select(element);\n");
        sb.append("List<WebElement> allOptions = select.getAllSelectedOptions();\n");
        sb.append("for (WebElement options : allOptions) {\n");
        sb.append("	allText.add(options.getText());\n");
        sb.append("}\n");
        sb.append("if (caseSensitive) {\n");
        sb.append("	System.out.println(\"Sorting the ListBox content (Case Sensitive)\");\n");
        sb.append("	Collections.sort(allText);\n");
        sb.append("	System.out.println(\"Captured all options in sorted order\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Sorting the ListBox content (Case In-Sensitive)\");\n");
        sb.append("	Collections.sort(allText, String.CASE_INSENSITIVE_ORDER);\n");
        sb.append("	System.out.println(\"Captured all options in sorted order\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("caseSensitive::true");

        return params;
    }
}
