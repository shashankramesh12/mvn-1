package com.tyss.optimize.nlp.mobile.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.html5.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value =  "MOB_GetDeviceLocation")
public class GetDeviceLocation implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Location location = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            AndroidDriver androidDriver=(AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage=(String) nlpRequestModel.getPassMessage();
            failMessage=(String) nlpRequestModel.getFailMessage();
            if(attributes.get("ifCheckPointIsFailed")!=null)
            {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Getting Geo location of Device");
            location = androidDriver.location();
            log.info("Successfully fetched Geo location of Device");
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*",location.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("location",location);
        } catch(Exception exception) {
            log.error("NLP_EXCEPTION in GetDeviceLocation ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime=System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime-startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Location location = androidDriver.location();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
