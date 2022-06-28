package com.tyss.optimize.nlp.web.program.alert;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyAlertPopUupMessageContainsString")
public class VerifyAlertPopUupMessageContainsString implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String details = "";
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String string = (String) attributes.get("string");
            String passMessage = (String) nlpRequestModel.getPassMessage();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            modifiedFailMessage = failMessage.replace("*string*", string);
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verify Alert popup message contains " + string);
            String actualText = driver.switchTo().alert().getText();
            details = "Expected Text:" + string + "\n" + "Actual    Text:" + actualText;
            log.info(details);
            if (actualText.contains(string)) {
                log.info("Alert popup message contains " + string);
                nlpResponseModel.setMessage(passMessage.replace("*string*", string));
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Alert popup message does not contain " + string);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyAlertPopUupMessageContainsString ", exception);
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

        sb.append("String actualText = driver.switchTo().alert().getText();\n");
        sb.append("if (actualText.contains(string)) {\n");
        sb.append("	System.out.println(\"Alert popup message contains \" + string);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Alert popup message does not contain \" + string);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("string::\"xyz\"");

        return params;
    }

}
