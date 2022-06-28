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
@Component(value = "VerifyListBoxOptionsAreSorted")
public class VerifyListBoxOptionsAreSorted implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
			String [] elementSplit=elementName.split(":");
        	elementName=elementSplit[1];
        	elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            Boolean flag = true;
            Select select = new Select(element);
            List<WebElement> allOptions = select.getOptions();
            List<String> allText = new ArrayList<>();
            for (WebElement options : allOptions) {
                allText.add(options.getText());
            }
            List<String> allTextBeforeSort = new ArrayList<String>();
            allTextBeforeSort.addAll(allText);
            log.info("Sorting the ListBox content");
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            Collections.sort(allText);
            //allText.containsAll(allTextBeforeSort);
            for (int i = 0; i < allText.size(); i++) {
                if (!allText.get(i).equals(allTextBeforeSort.get(i))) {
                    flag = false;
                }
            }
            if (flag) {
                log.info("Options in " + elementName + " " + elementType + " are in sorted order");
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Options in " + elementName + " " + elementType + " are not in sorted order");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyListboxOptionsAreSorted ", exception);
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

        sb.append("boolean flag = true;\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("Select select = new Select(element);\n");
        sb.append("List<WebElement> allOptions = select.getOptions();\n");
        sb.append("List<String> allText = new ArrayList<>();\n");
        sb.append("for (WebElement options : allOptions) {\n");
        sb.append("	allText.add(options.getText());\n");
        sb.append("}\n");
        sb.append("List<String> allTextBeforeSort = new ArrayList<String>();\n");
        sb.append("allTextBeforeSort.addAll(allText);\n");
        sb.append("Collections.sort(allText);\n");
        sb.append("for (int i = 0; i < allText.size(); i++) {\n");
        sb.append("	if (!allText.get(i).equals(allTextBeforeSort.get(i))) {\n");
        sb.append("		flag = false;\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (flag) {\n");
        sb.append("	System.out.println(\"Options are in sorted order\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Options are not in sorted order\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");

        return params;
    }
}
