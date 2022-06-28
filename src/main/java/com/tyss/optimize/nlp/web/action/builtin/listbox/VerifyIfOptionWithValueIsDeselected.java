package com.tyss.optimize.nlp.web.action.builtin.listbox;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.builtin.listbox.natives.GetAllSelectedOptions;
import com.tyss.optimize.nlp.web.action.builtin.listbox.natives.GetOptions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyIfOptionWithValueIsDeselected")
public class VerifyIfOptionWithValueIsDeselected implements Nlp {

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
            String value = (String) attributes.get("value");
        	String [] elementSplit=elementName.split(":");
        	String modElementName=elementSplit[1];
        	String modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
            WebElement element = (WebElement) attributes.get("element");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestOptions = new NlpRequestModel();
            requestOptions.getAttributes().put("containsChildNlp", true);
            requestOptions.getAttributes().put("elementName", elementName);
            requestOptions.getAttributes().put("elementType", elementType);
            requestOptions.getAttributes().put("element", element);
            requestOptions.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestOptions.setPassMessage("Captured all options from *elementName* *elementType*");
            requestOptions.setFailMessage("Failed to get all options from *elementName* *elementType*");

            NlpRequestModel requestAllSelected = new NlpRequestModel();
            requestAllSelected.getAttributes().put("containsChildNlp", true);
            requestAllSelected.getAttributes().put("elementName", elementName);
            requestAllSelected.getAttributes().put("elementType", elementType);
            requestAllSelected.getAttributes().put("element", element);
            requestAllSelected.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestAllSelected.setPassMessage("Captured all selected options from *elementName* *elementType*");
            requestAllSelected.setFailMessage("Failed to get all selected options from *elementName* *elementType*");

            log.info("Verifying ListBox if option with value " + value + " is selected in " + elementName + " " + elementType);
            String modifiedPassMessage = passMessage.replace("*value*", value).replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            modifiedFailMessage = failMessage.replace("*value*", value).replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            List<WebElement> selectedOption = (List<WebElement>) ((NlpResponseModel) new GetAllSelectedOptions().execute(requestAllSelected)).getAttributes().get("elementList");
            String actualValue = "";
            Boolean flag = false;
            Boolean match = false;
            List<WebElement> options = (List<WebElement>) ((NlpResponseModel) new GetOptions().execute(requestOptions)).getAttributes().get("elementList");
            for (WebElement we : options) {
                actualValue = we.getAttribute("value");
                if (actualValue.equals(value)) {
                    match = true;
                }
            }

            if (match) {
                options.removeAll(selectedOption);

                for (WebElement we : options) {
                    actualValue = we.getAttribute("value");
                    if (actualValue.equals(value)) {
                        flag = true;
                    }
                }
                if (flag) {
                    log.info("Option with value " + value + " is deselected in " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                } else {
                    log.error("Option with value " + value + " is not deselected in " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            } else {
                log.error("Option with value " + value + " is not present in " + elementName + " " + elementType);
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
            log.error("NLP_EXCEPTION in VerifyifOptionWithValueIsDeselected ", exception);
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
        sb.append("List<WebElement> selectedOption = new ArrayList<>();\n");
        sb.append("Select select = new Select(element);\n");
        sb.append("selectedOption = select.getAllSelectedOptions();\n");
        sb.append("String actualValue = \"\";\n");
        sb.append("boolean flag = false;\n");
        sb.append("boolean match = false;\n");
        sb.append("List<WebElement> options = select.getOptions();\n");
        sb.append("for (WebElement we : options) {\n");
        sb.append("	actualValue = we.getAttribute(value);\n");
        sb.append("	if (actualValue.equals(value)) {\n");
        sb.append("		match = true;\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	options.removeAll(selectedOption);\n");
        sb.append("	for (WebElement we : options) {\n");
        sb.append("		actualValue = we.getAttribute(value);\n");
        sb.append("		if (actualValue.equals(value)) {\n");
        sb.append("			flag = true;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (flag) {\n");
        sb.append("		System.out.println(\"Option with Value \" + value + \" is deselected\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"Option with Value \" + value + \" is not deselected\");\n");
        sb.append("	}\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Option with Value \" + value + \" is not present\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");
        params.add("value::\"xyz\"");

        return params;
    }
}
