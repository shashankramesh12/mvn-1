package com.tyss.optimize.nlp.mobile.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.mobile.action.get.GetBatteryPercentage;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_VerifyBatteryLevel")
public class VerifyBatteryLevel implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IDriver driver = nlpRequestModel.getDriver();
            Integer expectedBatteryPercentage = (Integer) attributes.get("expectedBatteryPercentage");
            String passMessage = (String) nlpRequestModel.getPassMessage();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*expectedBatteryPercentage*", expectedBatteryPercentage.toString());
            NlpRequestModel requestBattery = new NlpRequestModel();
            requestBattery.getAttributes().put("containsChildNlp", true);
            requestBattery.setDriver(driver);
            requestBattery.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestBattery.setPassMessage("Battery level of device is *returnValue*");
            requestBattery.setFailMessage("Failed to fetch battery level of device");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Verifying device battery percentage is more than " + expectedBatteryPercentage);
            int batteryPercentage = (Integer) ((NlpResponseModel) new GetBatteryPercentage().execute(requestBattery)).getAttributes().get("batteryPer");
            log.info("Getting battery percentage:");
            if (batteryPercentage > expectedBatteryPercentage) {
                log.info("Device battery percentage is more than " + expectedBatteryPercentage);
                nlpResponseModel.setMessage(passMessage.replace("*expectedBatteryPercentage*", expectedBatteryPercentage.toString()));
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.info("Device battery percentage is less than " + expectedBatteryPercentage);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyBatteryLevel ", exception);
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

        sb.append("double batteryLevel = androidDriver.getBatteryInfo().getLevel();\n");
        sb.append("Integer batteryPercentage = batteryLevel * 100;\n");
        sb.append("if (batteryPercentage > expectedBatteryPercentage) {\n");
        sb.append("	System.out.println(\"Device battery percentage is more than \" + expectedBatteryPercentage);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Device battery percentage is less than \" + expectedBatteryPercentage);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedBatteryPercentage::20");

        return params;
    }

}
