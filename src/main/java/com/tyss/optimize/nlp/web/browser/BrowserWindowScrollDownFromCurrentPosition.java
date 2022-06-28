package com.tyss.optimize.nlp.web.browser;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Slf4j
@Component(value = "BrowserWindow:ScrollDownFromCurrentPosition")
public class BrowserWindowScrollDownFromCurrentPosition implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null, failMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        Integer yCoordinate = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            WebDriver driver = null;
            if (Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            yCoordinate = (Integer) attributes.get("yCoordinate");
            Integer xCoordinate = driver.manage().window().getPosition().getX();
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String scrollDown = "window.scrollBy" + "(" + xCoordinate + ", " + yCoordinate + ")";
            js.executeScript(scrollDown);
            log.info("The page is scrolled from current position to (" + xCoordinate + ", " + yCoordinate + ") coordinates in browser window");
            modifiedPassMessage = passMessage.replace("*x*", xCoordinate.toString());
            modifiedPassMessage = modifiedPassMessage.replace("*y*", yCoordinate.toString());
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(modifiedPassMessage);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in BrowserWindow:ScrollDownFromCurrentPosition ", exception);
            modifiedFailMessage = failMessage.replace("*yCoordinate*", yCoordinate.toString());
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel.setStatus(CommonConstants.fail);
            nlpResponseModel.setMessage(modifiedFailMessage);
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    @Override
    public StringBuilder getTestCode() throws NlpException {
        return null;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        return null;
    }
}
