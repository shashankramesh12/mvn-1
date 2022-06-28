package com.tyss.optimize.nlp.web.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "EnterInputWithRandomString")
public class EnterInputWithRandomString implements Nlp {

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
            String input = (String) attributes.get("input");
            Integer numberOfAlphabets = (Integer) attributes.get("numberOfAlphabets");
        	String [] elementSplit=elementName.split(":");
        	elementName=elementSplit[1];
        	elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Entering input " + input + "with randomdata into " + elementName + " " + elementType);
            String modifiedPassMessage = passMessage.replace("*input*", input).replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*input*", input).replace("*elementName*", elementName).replace("*elementType*", elementType);
            NlpRequestModel requestTagName = new NlpRequestModel();
            requestTagName.getAttributes().put("containsChildNlp", true);
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            attributes.replace("input", RandomStringUtils.randomAlphanumeric(numberOfAlphabets).concat(input));
            nlpRequestModel.setAttributes(attributes);
            new SendKeys().execute(nlpRequestModel);
            log.info("Entered input " + input + "with random data into " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in EnterInputWithRandomString ", exception);
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
        sb.append("String input = RandomStringUtils.randomAlphanumeric(numberOfAlphabets).concat(INPUT);\n");
        sb.append("element.sendKeys(input);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("INPUT::\"Test\"");
        params.add("numberOfAlphabets::\"ABCDE\"");

        return params;
    }

}
