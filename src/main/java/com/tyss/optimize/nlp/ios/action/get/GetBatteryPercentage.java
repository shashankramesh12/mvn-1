package com.tyss.optimize.nlp.ios.action.get;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.NlpRequestModel;
import io.appium.java_client.ios.IOSBatteryInfo;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.tyss.optimize.nlp.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_GetBatteryPercentage")
public class GetBatteryPercentage implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Integer batteryPer = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IOSDriver iosDriver = (IOSDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Getting the battery level of device");
            IOSBatteryInfo battery = null;
            battery = iosDriver.getBatteryInfo();
            double batteryLevel = battery.getLevel();
            log.info("Getting the battery level of device:" + batteryLevel);
            batteryPer = (int) (batteryLevel * 100);
            log.info("Successfully fetched battery level of device");
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*", String.valueOf(batteryPer)));
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetBatteryPercentage ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("batteryPer", batteryPer);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("double batteryLevel = iosDriver.getBatteryInfo().getLevel();\n");
        sb.append("Integer batteryPer = batteryLevel * 100;\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}



