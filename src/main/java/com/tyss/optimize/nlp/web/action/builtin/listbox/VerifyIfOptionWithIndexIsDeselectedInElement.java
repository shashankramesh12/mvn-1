package com.tyss.optimize.nlp.web.action.builtin.listbox;

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
@Component(value = "VerifyIfOptionWithIndexIsDeselectedInElement")
public class VerifyIfOptionWithIndexIsDeselectedInElement implements Nlp {

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
            Integer index = (Integer) attributes.get("index");
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
            log.info("Verifying ListBox if option with index " + index + " is deselected in " + elementName + elementType);
            String modifiedPassMessage = passMessage.replace("*index*", String.valueOf(index))
                    .replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*index*", String.valueOf(index))
                    .replace("*elementName*", elementName).replace("*elementType*", elementType);
            Select select = new Select(element);
            List<WebElement> allSelectOptions = select.getAllSelectedOptions();
            List<String> textOfAllSelectedOptions = new ArrayList<>();
            for (WebElement options : allSelectOptions) {
                textOfAllSelectedOptions.add(options.getText());
            }
            List<WebElement> allOptions = select.getOptions();
            List<String> textAllOptions = new ArrayList<>();
            for (WebElement options : allOptions) {
                textAllOptions.add(options.getText());
            }

            String option = "";
            String selectedOption = "";
            boolean flag = false;
            if (index < textAllOptions.size()) {
                for (int i = 0; i < textAllOptions.size(); i++) {
                    option = textAllOptions.get(index);
                    for (int j = 0; j < textOfAllSelectedOptions.size(); j++) {
                        selectedOption = textOfAllSelectedOptions.get(j);
                        if ((!selectedOption.equals(option)) && index == i) {
                            flag = true;
                        }
                    }
                }

                if (flag) {
                    log.info("Option whose index " + index + " is deselected in " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                } else {
                    log.error("Option with index" + index + " is selected in " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            } else {
                log.error("Invalid index");
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfOptionWithIndexIsDeselectedInelement ", exception);
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
        sb.append("Select select = new Select(element);\n");
        sb.append("List<WebElement> allSelectOptions = select.getAllSelectedOptions();\n");
        sb.append("List<String> textOfAllSelectedOptions = new ArrayList<>();\n");
        sb.append("for (WebElement options : allSelectOptions) {\n");
        sb.append("	textOfAllSelectedOptions.add(options.getText());\n");
        sb.append("}\n");
        sb.append("List<WebElement> allOptions = select.getOptions();\n");
        sb.append("List<String> textAllOptions = new ArrayList<>();\n");
        sb.append("for (WebElement options : allOptions) {\n");
        sb.append("	textAllOptions.add(options.getText());\n");
        sb.append("}\n");
        sb.append("String option = \"\";\n");
        sb.append("String selectedOption = \"\";\n");
        sb.append("boolean flag = false;\n");
        sb.append("if (index < textAllOptions.size()) {\n");
        sb.append("	for (int i = 0; i < textAllOptions.size(); i++) {\n");
        sb.append("		option = textAllOptions.get(index);\n");
        sb.append("		for (int j = 0; j < textOfAllSelectedOptions.size(); j++) {\n");
        sb.append("			selectedOption = textOfAllSelectedOptions.get(j);\n");
        sb.append("			if ((!selectedOption.equals(option)) && index == i) {\n");
        sb.append("				flag = true;\n");
        sb.append("			}\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (flag) {\n");
        sb.append("		System.out.println(\"Option whose Index \" + index + \" is deselected\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"Option with Index \" + index + \" is selected\");\n");
        sb.append("	}\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Invalid Index\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");
        params.add("index::1");

        return params;
    }
}
