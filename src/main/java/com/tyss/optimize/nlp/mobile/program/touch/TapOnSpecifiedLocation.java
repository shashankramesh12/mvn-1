package com.tyss.optimize.nlp.mobile.program.touch;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_TapOnSpecifiedLocation")
public class TapOnSpecifiedLocation implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
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
            log.info("Tapping on specified location; x:" + xCoordinate + " y:" + yCoordinate);

            TouchAction action = new TouchAction(androidDriver);
            action.tap(PointOption.point(xCoordinate, yCoordinate)).perform();
            log.info("Tapped on " + xCoordinate + " and " + yCoordinate + " coordinate");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in TapOnSpecifiedLocation ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
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

        sb.append("TouchAction action = new TouchAction(androidDriver);\n");
        sb.append("action.tap(PointOption.point(xCoordinate, yCoordinate)).perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("xCoordinate::238");
        params.add("yCoordinate::1158");

        return params;
    }

}
