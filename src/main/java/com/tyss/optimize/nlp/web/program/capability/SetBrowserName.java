package com.tyss.optimize.nlp.web.program.capability;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component(value = "SetBrowserName")
public class SetBrowserName implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        DesiredCapabilities desiredCapabilities = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            desiredCapabilities = (DesiredCapabilities) attributes.get("desiredCapabilities");
            String browserName = (String) attributes.get("browserName");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Setting Browser Name as: " + browserName);
            String modifiedPassMessage = passMessage.replace("*browserName*", browserName);
            modifiedFailMessage = failMessage.replace("*browserName*", browserName);
            Map<String, Object> environmentCapability = (Map<String, Object>) attributes.get("environmentCapability");
            DesiredCapabilities finalDesiredCapabilities = desiredCapabilities;
            if(Objects.nonNull(environmentCapability)){
                environmentCapability.entrySet().stream().forEach(e -> {
                    finalDesiredCapabilities.setCapability(e.getKey(), e.getValue());
                });
            }
            finalDesiredCapabilities.setCapability("browserstack.local", "false");
            finalDesiredCapabilities.setBrowserName(browserName);
            log.info("desiredCapabilities----"+finalDesiredCapabilities);
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Successfully set browser name as " + browserName);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in SetBrowserName ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.setDesiredCapabilities(desiredCapabilities);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("if(Objects.nonNull(environmentCapability)){\n");
        sb.append("	environmentCapability.entrySet().stream().forEach(e -> {\n");
        sb.append("		desiredCapabilities.setCapability(e.getKey(), e.getValue());\n");
        sb.append("	});\n");
        sb.append("}\n");
        sb.append("desiredCapabilities.setCapability(\"browserstack.local\", \"false\");\n");
        sb.append("desiredCapabilities.setBrowserName(browserName);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("browserName::\"Chrome\"");

        return params;
    }

}
