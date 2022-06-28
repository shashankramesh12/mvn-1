package com.tyss.optimize.nlp.web.program.mouse;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "ScrollPageToSpecificLocation")
public class ScrollPageToSpecificLocation implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            Integer xOffset = (Integer) attributes.get("xOffset");
            Integer yOffset = (Integer) attributes.get("yOffset");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedPassMessage = passMessage.replace("*xOffset*", String.valueOf(xOffset))
                    .replace("*yOffset*", String.valueOf(yOffset));
            modifiedFailMessage = failMessage.replace("*xOffset*", String.valueOf(xOffset))
                    .replace("*yOffset*", String.valueOf(yOffset));
            log.info("Window scrolling page to " + xOffset + " and " + yOffset + " location in browser window");
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(" + xOffset + "," + yOffset + ")");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in ScrollPageToSpecificLocation ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("((JavascriptExecutor) driver).executeScript(\"window.scrollTo(\" + xOffset + \",\" + yOffset + \")\");\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("xOffset::20");
        params.add("yOffset::30");

        return params;
    }
}
