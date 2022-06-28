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
@Component(value = "VerifyAllOptionsAreSelected")
public class VerifyAllOptionsAreSelected implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String modifiedFailMessage = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
        	String [] elementSplit=elementName.split(":");
       		String  modElementName=elementSplit[1];
       		String  modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
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

            log.info("Verifying if all options are selected in" + elementName + " " + elementType);
            String modifiedPassMessage = passMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            List<WebElement> optionList = (List<WebElement>) ((NlpResponseModel) new GetOptions().execute(requestOptions)).getAttributes().get("elementList");
            List<WebElement> selectedOptionList = (List<WebElement>) ((NlpResponseModel) new GetAllSelectedOptions().execute(requestAllSelected)).getAttributes().get("elementList");
            boolean flag = true;
            if (optionList.size() == selectedOptionList.size()) {
                for (int i = 0; i < optionList.size(); i++) {
                    if (!optionList.get(i).equals(selectedOptionList.get(i))) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    log.info("All options are selected in " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                } else {
                    log.error("All options are not selected in " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            } else {
                log.error("All options are not selected in " + elementName + " " + elementType);
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
            log.error("NLP_EXCEPTION in VerifyAllOptionsAreSelected ", exception);
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
        sb.append("List<WebElement> optionList = new ArrayList<>();\n");
        sb.append("List<WebElement> selectedOptionList = new ArrayList<>();\n");
        sb.append("Select select = new Select(element);\n");
        sb.append("optionList = select.getOptions();\n");
        sb.append("selectedOptionList = select.getAllSelectedOptions();\n");
        sb.append("boolean flag = true;\n");
        sb.append("if (optionList.size() == selectedOptionList.size()) {\n");
        sb.append("	for (int i = 0; i < optionList.size(); i++) {\n");
        sb.append("		if (!optionList.get(i).equals(selectedOptionList.get(i))) {\n");
        sb.append("			flag = false;\n");
        sb.append("			break;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (flag) {\n");
        sb.append("		System.out.println(\"All options are selected\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"All options are not selected\");\n");
        sb.append("	}\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"All options are not selected\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"cars\")");

        return params;
    }
}
