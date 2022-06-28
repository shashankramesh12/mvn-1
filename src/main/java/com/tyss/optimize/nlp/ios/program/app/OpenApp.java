package com.tyss.optimize.nlp.ios.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(value = "IOS_OpenApp")
public class OpenApp implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        IOSDriver iosDriver = null;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String platform = (String) attributes.get("platform");
            String version = (String) attributes.get("version");
            String deviceName = (String) attributes.get("deviceName");
            String udid = (String) attributes.get("udid");
            String appPackage = (String) attributes.get("appPackage");
            String appActivity = (String) attributes.get("platform");
            String hubURL = (String) attributes.get("hubURL");
            Integer cmdTimeOut = (Integer) attributes.get("cmdTimeOut");
            Long implicitTimeOut = (Long) attributes.get("implicitTimeOut");
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }

            log.info("Setting the desired capabilities and opening the application: Please ensure that required capabilities are set");

            DesiredCapabilities cap = new DesiredCapabilities();
            cap.setCapability("platformName", platform);
            cap.setCapability("platformVersion", version);
            cap.setCapability("deviceName", deviceName);
            cap.setCapability("udid", udid);
            cap.setCapability("appPackage", appPackage);
            cap.setCapability("appActivity", appActivity);
            cap.setCapability("autoGrantPermissions", true);
            cap.setCapability("autoDismissAlerts", true);
            cap.setCapability("newCommandTimeout", cmdTimeOut);
            iosDriver = new IOSDriver(new URL(hubURL), cap);
            iosDriver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenApp", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        IDriver driver = new com.tyss.optimize.data.models.dto.drivers.IOSDriver(iosDriver);
        nlpResponseModel.setDriver(driver);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();



        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
