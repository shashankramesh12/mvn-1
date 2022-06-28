package com.tyss.optimize.nlp.mobile.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.nlp.util.*;

@Slf4j
@Component(value = "MOB_ClickAtSpecifiedLocation")
public class ClickAtSpecifiedLocation implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            AndroidDriver androidDriver = (AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            Integer xCoordinate = (Integer) attributes.get("xCoordinate");
            Integer yCoordinate = (Integer) attributes.get("yCoordinate");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*xCoordinate*", String.valueOf(xCoordinate)).replace("*yCoordinate*", String.valueOf(yCoordinate));
            String modifiedPassMessage = passMessage.replace("*xCoordinate*", String.valueOf(xCoordinate)).replace("*yCoordinate*", String.valueOf(yCoordinate));
            log.info("Clicking at specified location:- x:" + xCoordinate + " y:" + yCoordinate + " of device screen");
            log.info("Clicked at " + xCoordinate + " and " + yCoordinate + " coordinate of device screen");
            TouchAction action = new TouchAction(androidDriver);
            action.tap(PointOption.point(xCoordinate, yCoordinate)).perform();
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in ClickAtSpecifiedLocation ", exception);
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

        sb.append("TouchAction action = new TouchAction(androidDriver);\n");
        sb.append("action.tap(PointOption.point(xCoordinate, yCoordinate)).perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("xCoordinate::20");
        params.add("yCoordinate::30");

        return params;
    }

}
