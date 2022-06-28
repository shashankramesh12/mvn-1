package com.tyss.optimize.nlp.ios.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_ClickAtSpecifiedLocation")
public class ClickAtSpecifiedLocation implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IOSDriver iosDriver = (IOSDriver) nlpRequestModel.getDriver().getSpecificIDriver();
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

            Actions actions = new Actions(iosDriver);
            //TouchAction actions = new TouchAction(iosDriver);
            //actions.tap(PointOption.point(xCoordinate, yCoordinate)).perform();
            actions.moveByOffset(xCoordinate, yCoordinate).click().build().perform();
            log.info("Clicked at " + xCoordinate + " and " + yCoordinate + " coordinate of device screen");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in ClickAtSpecifiedLocation ", exception);
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

        sb.append("Actions actions = new Actions(iosDriver);\n");
        sb.append("actions.moveByOffset(xCoordinate, yCoordinate).click().build().perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("xCoordinate::20");
        params.add("yCoordinate::30");

        return params;
    }

}
