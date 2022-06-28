package com.tyss.optimize.nlp.web.program.browser;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyBrowserWindowXLocation")
public class VerifyBrowserWindowXLocation implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver webDriver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            Integer expectedLocation = (Integer) attributes.get("expectedLocation");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying Browser XLocation is" + expectedLocation);
            String modifiedPassMessage = passMessage.replace("*expectedLocation*", String.valueOf(expectedLocation));
            modifiedFailMessage = failMessage.replace("*expectedLocation*", String.valueOf(expectedLocation));
            int xLocation = webDriver.manage().window().getPosition().getX();
            if (xLocation == expectedLocation) {
                log.info("Browser window XLocation is verified and it is " + expectedLocation + " pixels");
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Browser window XLocation is verified and it is not " + expectedLocation + " pixels");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyBrowserWindowXLocation ", exception);
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

        sb.append("int xLocation = webDriver.manage().window().getPosition().getX();\n");
        sb.append("if (xLocation == expectedLocation) {\n");
        sb.append("	System.out.println(\"Browser window XLocation is verified and it is \" + expectedLocation + \" pixels\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Browser window XLocation is verified and it is not \" + expectedLocation + \" pixels\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedLocation::40");

        return params;
    }
}
