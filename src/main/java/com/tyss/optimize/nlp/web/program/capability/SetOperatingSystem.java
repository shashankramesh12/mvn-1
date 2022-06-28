package com.tyss.optimize.nlp.web.program.capability;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "SetOperatingSystem")
public class SetOperatingSystem implements Nlp {

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
            desiredCapabilities = nlpRequestModel.getDesiredCapabilities();
            String operatingSystem = (String) attributes.get("operatingSystem");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Setting Platform as:" + operatingSystem);
            String modifiedPassMessage = passMessage.replace("*operatingSystem*", operatingSystem);
            modifiedFailMessage = failMessage.replace("*operatingSystem*", operatingSystem);
            if (EnumUtils.isValidEnum(Platform.class, operatingSystem)) {
                Platform platform = Platform.fromString(operatingSystem);
                desiredCapabilities.setPlatform(platform);
                log.info("Successfully set platform as " + operatingSystem);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Invalid Platform:" + operatingSystem);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in SetOperatingSystem ", e);
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

        sb.append("if (EnumUtils.isValidEnum(Platform.class, operatingSystem)) {\n");
        sb.append("	Platform platform = Platform.fromString(operatingSystem);\n");
        sb.append("	desiredCapabilities.setPlatform(platform);\n");
        sb.append("	System.out.println(\"Successfully set platform as \" + operatingSystem);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Invalid Platform:\" + operatingSystem);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("operatingSystem::\"Windows OS\"");

        return params;
    }

}
