package com.tyss.optimize.nlp.web.action.builtin.listbox;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.builtin.listbox.natives.GetOptions;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetListBoxSize")
public class GetListBoxSize implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        int count = 0;
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
            requestOptions.getAttributes().put("containsChildNlp", true);
            requestOptions.getAttributes().put("elementName", elementName);
            requestOptions.getAttributes().put("elementType", elementType);
            requestOptions.getAttributes().put("element", element);
            requestOptions.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestOptions.setPassMessage("Captured all options from *elementName* *elementType*");
            requestOptions.setFailMessage("Failed to get all options from *elementName* *elementType*");
            log.info("Fetching size of " + elementName + " " + elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName)
                    .replace("*elementType*", modElementType);
            Boolean status = (Boolean) ((NlpResponseModel) new IsItListBox().execute(requestListbox))
                    .getAttributes().get("isItListBox");
            if (status) {
                List<WebElement> allOptions = (List<WebElement>) ((NlpResponseModel) new GetOptions().execute(requestOptions))
                        .getAttributes().get("elementList");
                count = allOptions.size();
                log.info("Number of options present in " + elementName + " " + elementType + " is " + count);
                String modifiedPassMessage = passMessage.replace("*elementName*", modElementName)
                        .replace("*elementType*", modElementType).replace("*returnValue*", String.valueOf(count));
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                log.info("Successfully fetched size of " + elementName + " " + elementType);
            } else {
                log.error("This action cannot be performed on NON-ListBox element");
                log.error("Failed to get size of " + elementName + " " + elementType);
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
            log.error("NLP_EXCEPTION in GetListBoxSize ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("count", count);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("int count = 0;\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String tagName = element.getTagName();\n");
        sb.append("if (tagName.equals(\"select\")) {\n");
        sb.append("	System.out.println(\"The element is a ListBox\");\n");
        sb.append(" Select select = new Select(element);\n");
        sb.append(" List<WebElement> elementList = select.getOptions();\n");
        sb.append(" count = elementList.size();\n");
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
