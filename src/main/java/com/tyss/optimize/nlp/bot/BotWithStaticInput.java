package com.tyss.optimize.nlp.bot;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component(value = "BotWithStaticInput")
public class BotWithStaticInput implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {

        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String input = attributes.get("input").toString();
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            Integer responseWaitTime = (Integer) attributes.get("responseWaitTime");
            WebElement inputElement = (WebElement) attributes.get("inputElement");
            String locatorValue = attributes.get("locatorValue").toString();
            String locatorName = attributes.get("locatorName").toString();
            WebDriver driver =null;
            String response = null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            if(Objects.nonNull(inputElement)){
                inputElement.sendKeys(input);
                inputElement.sendKeys(Keys.ENTER);
            }
            Thread.sleep(responseWaitTime*1000);
            By b = (By) By.class.getDeclaredMethod(locatorName, String.class).invoke(null, locatorValue);
            List<WebElement> responseElements = driver.findElements(b);
            if(Objects.nonNull(responseElements)){
                WebElement latestElement = responseElements.get(responseElements.size()-1);
                response = latestElement.getText();
            }
            passMessage = passMessage.replace("*question*", input)
                    .replace("*answer*", Objects.nonNull(response) ? response : "");
            failMessage = failMessage.replace("*question*", input)
                    .replace("*answer*", Objects.nonNull(response) ? response : "");
            if(!Objects.nonNull(response)){
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                nlpResponseModel.getAttributes().put("response", response);
            } else {
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.getAttributes().put("response", response);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in BotWithStaticInput:", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
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
