package com.tyss.optimize.nlp.ios.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.html5.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_GetDeviceLocation")
public class GetDeviceLocation implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedFailMessage = null, modifiedPassMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Location location = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            //IOSDriver iosDriver = nlpRequestModel.getIosDriver();
            IOSDriver iosDriver = (IOSDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            failMessage = (String) nlpRequestModel.getFailMessage();
            passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Getting Geo location of Device");
            location = iosDriver.location();
            log.info("Successfully fetched Geo location of Device" + location);
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*", String.valueOf(location)));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("location", location);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetDeviceLocation ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Location location = iosDriver.location();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
