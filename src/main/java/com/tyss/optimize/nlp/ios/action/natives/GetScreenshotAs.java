package com.tyss.optimize.nlp.ios.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_GetScreenshotAs")
public class GetScreenshotAs implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedFailMessage = null, modifiedPassMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        File srcFile = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
		    String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            elementType=elementType.concat(" in "+elementSplit[0] + " screen ");
            failMessage = (String) nlpRequestModel.getFailMessage();
            passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedPassMessage = passMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            log.info("Taking screenshot of " + elementName + " " + elementType);
            srcFile = element.getScreenshotAs(OutputType.FILE);
            log.info("Screenshot of " + elementName + " " + elementType + " is captured");
            nlpResponseModel.setMessage(modifiedPassMessage.replace("*returnValue*", String.valueOf(srcFile)));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("srcFile", srcFile);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetScreenshotAs ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = iosDriver.findElement(ELEMENT);\n");
        sb.append("File srcFile = element.getScreenshotAs(OutputType.FILE);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");

        return params;
    }

}
