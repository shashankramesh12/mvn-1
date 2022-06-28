package com.tyss.optimize.nlp.mobile.action.get;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.mobile.action.natives.GetDeviceLocation;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.html5.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_GetAltitude")
public class GetAltitude implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Double deviceLocation = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IDriver driver = nlpRequestModel.getDriver();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestDevice = new NlpRequestModel();
            requestDevice.setDriver(driver);
            requestDevice.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestDevice.getAttributes().put("containsChildNlp", true);
            requestDevice.setPassMessage("Geo location of device is *returnValue*");
            requestDevice.setFailMessage("Failed to get Geo location of device");
            log.info("Getting Geo location altitude");
            deviceLocation = ((Location) ((NlpResponseModel) new GetDeviceLocation().execute(requestDevice)).getAttributes().get("location")).getAltitude();
            log.info("Successfully fetched Geo location altitude");
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*", deviceLocation.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("deviceLocation", deviceLocation);
        } catch (NlpException e) {
            log.error("NLP_EXCEPTION in GetAltitude ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetAltitude ", e);
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

        sb.append("Double deviceLocation = androidDriver.location().getAltitude();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
