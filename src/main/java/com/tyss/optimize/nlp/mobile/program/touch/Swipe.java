package com.tyss.optimize.nlp.mobile.program.touch;

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
@Component(value = "MOB_Swipe")
public class Swipe implements Nlp {

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
            Integer fromX = (Integer) attributes.get("fromX");
            Integer fromY = (Integer) attributes.get("fromY");
            Integer toX = (Integer) attributes.get("toX");
            Integer toY = (Integer) attributes.get("toY");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*fromX*", String.valueOf(fromX)).replace("*fromY*", String.valueOf(fromY)).replace("*toX*", String.valueOf(toX)).replace("*toY*", String.valueOf(toY));
            String modifiedPassMessage = passMessage.replace("*fromX*", String.valueOf(fromX)).replace("*fromY*", String.valueOf(fromY)).replace("*toX*", String.valueOf(toX)).replace("*toY*", String.valueOf(toY));
            log.info("Swiping from " + fromX + " x " + fromY + " coordinate to " + toX + " x " + toY + " coordinate");
            log.info("Swipe coordinates detail");
            log.info("From X:" + fromX);
            log.info("From Y:" + fromY);
            log.info("To X:" + toX);
            log.info("To Y:" + toY);
            TouchAction action = new TouchAction(androidDriver);
            action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();
            log.info("Swiped from " + fromX + " x " + fromY + " coordinate to " + toX + " x " + toY + " coordinate");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in Swipe ", exception);
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
        sb.append("action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("fromX::10");
        params.add("fromY::20");
        params.add("toX::30");
        params.add("toY::40");

        return params;
    }

}
