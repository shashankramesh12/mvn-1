package com.tyss.optimize.nlp.web.action.builtin.keyboard;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.natives.Clear;
import com.tyss.optimize.nlp.web.action.natives.SendKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "ClearThenEnterInput")
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
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String input = (String) attributes.get("input");
			String [] elementSplit=elementName.split(":");
			elementName=elementSplit[1];
			elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestText = new NlpRequestModel();
            requestText.getAttributes().put("containsChildNlp", true);
            modifiedFailMessage = failMessage.replace("*input*", input).replace("*elementName*", elementName).replace("*elementType*", elementType);
            String modifiedPassMessage = passMessage.replace("*input*", input).replace("*elementName*", elementName).replace("*elementType*", elementType);
            log.info("Clear text and enter input " + input + " into " + elementName + " " + elementType);
            new Clear().execute(nlpRequestModel);
            new SendKeys().execute(nlpRequestModel);
            log.info("Cleared text and entered input " + input + " into " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in ClearThenEnterInput ", exception);
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
        sb.append("element.clear();\n");
        sb.append("element.sendKeys(INPUT);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("INPUT::\"xyz\"");

        return params;
    }
}
