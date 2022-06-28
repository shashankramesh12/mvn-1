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
@Component(value = "VerifyAllOptionsAreDeselected")
public class VerifyAllOptionsAreDeselected implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
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
            log.info("Verifying if all options are deselected in list box");
            Select select = new Select(element);
            List<WebElement> allSelectOptions = select.getAllSelectedOptions();
            List<String> textOfAllSelectedOptions = new ArrayList<>();
            for (WebElement options : allSelectOptions) {
                textOfAllSelectedOptions.add(options.getText());
            }
            List<WebElement> allOptions = select.getOptions();
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            if (textOfAllSelectedOptions.size() == allOptions.size()) {
                log.error("All options are Selected in list box");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else if (textOfAllSelectedOptions.size() < allOptions.size() && (!textOfAllSelectedOptions.isEmpty())) {
                log.error("Few options are Selected in list box");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else if (textOfAllSelectedOptions.isEmpty()) {
                log.info("All options are deselected in " + elementName + " " + elementType);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyAllOptionsAreDeselected ", exception);
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
        sb.append("Select select=new Select(element);\n");
        sb.append("List<WebElement> allSelectOptions=select.getAllSelectedOptions();\n");
        sb.append("List<String> textOfAllSelectedOptions =new ArrayList<>();\n");
        sb.append("for(WebElement options:allSelectOptions) {\n");
        sb.append("	textOfAllSelectedOptions.add(options.getText());\n");
        sb.append("}\n");
        sb.append("List<WebElement> allOptions=select.getOptions();\n");
        sb.append("if (textOfAllSelectedOptions.size() == allOptions.size()) {\n");
        sb.append("	System.out.println(\"All options are Selected in list box\");\n");
        sb.append("} else if (textOfAllSelectedOptions.size() < allOptions.size()&&(!textOfAllSelectedOptions.isEmpty())) {\n");
        sb.append("	System.out.println(\"Few options are Selected in list box\");\n");
        sb.append("} else if (textOfAllSelectedOptions.isEmpty()) {\n");
        sb.append("	System.out.println(\"All options are deselected\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");

        return params;
    }
}
