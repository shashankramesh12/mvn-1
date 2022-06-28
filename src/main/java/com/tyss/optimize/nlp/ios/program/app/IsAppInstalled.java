package com.tyss.optimize.nlp.ios.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_IsAppInstalled")
public class IsAppInstalled implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IOSDriver iosDriver = (IOSDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String appPackage = (String) attributes.get("appPackage");
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying if application is installed");

            boolean flag = iosDriver.isAppInstalled(appPackage);
            if (flag) {
                log.info("Application is installed");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Application is not installed");
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in IsAppInstalled ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean flag = iosDriver.isAppInstalled(appPackage);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("appPackage::\"randomValue\"");

        return params;
    }

}
