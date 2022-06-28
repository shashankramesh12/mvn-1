package com.tyss.optimize.nlp.web.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component(value = "VerifyWidthOfElement")
public class VerifyWidthOfElement implements Nlp {

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
            Integer expectedWidth = (Integer) attributes.get("expectedWidth");
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
            log.info("Verifying width of " + elementName + " " + elementType + "is " + expectedWidth);
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*expectedWidth*", String.valueOf(expectedWidth));
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*expectedWidth*", String.valueOf(expectedWidth));

            int width = element.getSize().getWidth();
            if (width == expectedWidth) {
                log.info("The width of " + elementName + " " + elementType + " is " + expectedWidth);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The width of " + elementName + " " + elementType + " is not " + expectedWidth);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyWidthOfElement ", exception);
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
        sb.append("int width = element.getSize().getWidth();\n");
        sb.append("if (width == expectedWidth) {\n");
        sb.append("	System.out.println(\"The width is \" + expectedWidth);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The width is not \" + expectedWidth);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("expectedWidth::50");

        return params;
    }
}
