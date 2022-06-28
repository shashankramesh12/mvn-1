package com.tyss.optimize.nlp.ios.action.builtin.keyboard;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.ios.action.natives.Clear;
import com.tyss.optimize.nlp.ios.action.natives.SendKeys;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_ClearThenEnterInput")
public class ClearThenEnterInput implements Nlp {
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
            String input = (String) attributes.get("input");
		    String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String  modElementType=elementType.concat(" in "+elementSplit[0] + " screen ");
        
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*input*", input).replace("*elementName*", elementName).replace("*elementType*", elementType);
            String modifiedPassMessage = passMessage.replace("*input*", input).replace("*elementName*", elementName).replace("*elementType*", elementType);
            NlpRequestModel requestClear = new NlpRequestModel();
            requestClear.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestClear.getAttributes().put("elementName", elementName);
            requestClear.getAttributes().put("elementType", elementType);
            requestClear.getAttributes().put("element", element);
            requestClear.getAttributes().put("containsChildNlp", true);
            requestClear.setPassMessage("Cleared text from *elementName* *elementType*");
            requestClear.setFailMessage("Failed to clear text from *elementName* *elementType*");

            NlpRequestModel requestKeys = new NlpRequestModel();
            requestKeys.getAttributes().put("containsChildNlp", true);
            requestKeys.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestKeys.getAttributes().put("elementName", elementName);
            requestKeys.getAttributes().put("elementType", elementType);
            requestKeys.getAttributes().put("element", element);
            requestKeys.getAttributes().put("input", input);
            requestKeys.setPassMessage("Entered *input* into *elementName* *elementType*");
            requestKeys.setFailMessage("Failed to enter *input* into *elementName* *elementType*");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Clear text and enter input " + input + " into " + elementName + " " + elementType);
            new Clear().execute(requestClear);
            new SendKeys().execute(requestKeys);
            log.info("Cleared text and entered " + input + " into " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in ClearThenEnterInput", exception);
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

        sb.append("WebElement element = iosDriver.findElement(ELEMENT);\n");
        sb.append("element.clear();\n");
        sb.append("element.sendKeys(INPUT);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("INPUT::\"ABCD\"");

        return params;
    }

}
