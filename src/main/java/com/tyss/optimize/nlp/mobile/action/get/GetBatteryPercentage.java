package com.tyss.optimize.nlp.mobile.action.get;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.android.AndroidBatteryInfo;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_GetBatteryPercentage")
public class GetBatteryPercentage implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        AndroidBatteryInfo battery = null;
        Integer batteryPer = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            AndroidDriver androidDriver = (AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Getting battery level of device");
            battery = androidDriver.getBatteryInfo();
            double batteryLevel = battery.getLevel();
            log.info("Getting battery level of device:" + batteryLevel);
            batteryPer = (int) (batteryLevel * 100);
            log.info("Successfully fetched battery level of device");
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*", batteryPer.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("batteryPer", batteryPer);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetBatteryPercentage ", e);
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

        sb.append("double batteryLevel = androidDriver.getBatteryInfo().getLevel();\n");
        sb.append("Integer batteryPer = batteryLevel * 100;\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
