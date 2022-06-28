package com.tyss.optimize.nlp.web.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetAttribute")
public class GetAttribute implements Nlp {

    @Override
    public NlpResponseModel  execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        String value = "";
        Long startTime = System.currentTimeMillis();
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String attributeName = (String) attributes.get("attributeName");
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            WebElement element = (WebElement) attributes.get("element");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*attributeName*", attributeName).replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            log.info("Getting " + attributeName + " attribute value of " + elementName + " " + elementType);
            value = element.getAttribute(attributeName);
            String modifiedPassMessage = passMessage.replace("*attributeName*", attributeName).replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*returnValue*", value);
            log.info(attributeName + " attribute value of " + elementName + " " + elementType + " is " + value);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("value", value);
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetAttribute ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String value = element.getAttribute(attributeName);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("attributeName::\"randomName\"");

        return params;
    }

}
