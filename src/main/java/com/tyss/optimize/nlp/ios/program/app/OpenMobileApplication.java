package com.tyss.optimize.nlp.ios.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component(value = "IOS_OpenMobileApplication")
public class OpenMobileApplication implements Nlp {
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
            String hubURL = (String) attributes.get("hubURL");
            Integer iTimeOut = (Integer) attributes.get("implicitTimeOut");
            DesiredCapabilities cap = (DesiredCapabilities) attributes.get("desiredCapabilities");
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            if(Objects.nonNull(attributes.get("systemCapability"))){
                Map<String, Object> systemCapability = (Map<String, Object>) attributes.get("systemCapability");
                systemCapability.entrySet().stream().forEach(e -> {
                    cap.setCapability(e.getKey(), e.getValue());
                });
            }
            log.info("Opening the application: Please ensure that required capabilities are set ");

            iosDriver = new IOSDriver(new URL(hubURL), cap);
            iosDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(iTimeOut));
            log.info("Application opened");
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in OpenMobileApplication ", exception);
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

        sb.append("try{\n");
        sb.append("  iosDriver = new IOSDriver(new URL(hubURL), cap);\n");
        sb.append("  iosDriver.manage().timeouts().implicitlyWait(implicitTimeOut, TimeUnit.SECONDS);\n");
        sb.append("}catch(MalformedURLException mfe){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("hubURL::\"http://localhost:4723/wd/hub\"");
        params.add("implicitTimeOut::20");

        return params;
    }

}
