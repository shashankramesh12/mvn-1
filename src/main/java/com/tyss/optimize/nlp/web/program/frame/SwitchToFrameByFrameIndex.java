package com.tyss.optimize.nlp.web.program.frame;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "SwitchToFrameByFrameIndex")
public class SwitchToFrameByFrameIndex implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage=null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            Integer index = (Integer) attributes.get("index");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Switching to frame by index:" + index);
            modifiedPassMessage = passMessage.replace("*index*", String.valueOf(index));
            modifiedFailMessage = failMessage.replace("*index*", String.valueOf(index));
            driver.switchTo().frame(index);
            log.info("Switched to frame where index is " + index);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in SwitchToFrameByFrameIndex ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("driver.switchTo().frame(index);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("index::1");

        return params;
    }
}
