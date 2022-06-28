package com.tyss.optimize.nlp.mobile.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(value = "MOB_OpenApp")
public class OpenApp implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        AndroidDriver androidDriver = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String platformName = attributes.get("platformName").toString();
            String version = attributes.get("version").toString();
            String udid = attributes.get("udid").toString();
            String deviceName = attributes.get("deviceName").toString();
            String appPackage = attributes.get("appPackage").toString();
            String appActivity = attributes.get("appActivity").toString();
            String hubURL = attributes.get("hubURL").toString();
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
            cap.setCapability("platformName", platformName);
            cap.setCapability("platformVersion", version);
            cap.setCapability("deviceName", deviceName);
            cap.setCapability("udid", udid);
            //cap.setCapability("app", apkPath);
            cap.setCapability("appPackage", appPackage);
            cap.setCapability("appActivity", appActivity);
            // cap.setCapability("noReset", true);
            cap.setCapability("autoGrantPermissions", true);
            // cap.setCapability("autoAcceptAlerts", true);
            //cap.setCapability("autoDismissAlerts", true);
            cap.setCapability("newCommandTimeout", cmdTimeOut);
            androidDriver = new AndroidDriver(new URL(hubURL), cap);
            androidDriver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);
            log.info("Successfully set capabilities and opened application");
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenApp ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        IDriver driver = new com.tyss.optimize.data.models.dto.drivers.AndroidDriver(androidDriver);
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
