package com.tyss.optimize.nlp.web.action.builtin.mouse;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MouseDoubleClickAtCursorPoint")
public class MouseDoubleClickAtCursorPoint implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Double clicking on the current cursor point");
            log.info("Double clicked on current cursor point");
            Actions actions = new Actions(driver);
            actions.doubleClick();
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in MouseDoubleClickAtCursorPoint ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
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

        sb.append("Actions actions = new Actions(driver);\n");
        sb.append("actions.doubleClick().perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }
}
