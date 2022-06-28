package com.tyss.optimize.nlp.mobile.action.builtin.touch;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
//import io.appium.java_client.android.AndroidElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import static io.appium.java_client.touch.LongPressOptions.longPressOptions;
import static io.appium.java_client.touch.offset.ElementOption.element;
import static java.time.Duration.ofSeconds;

import org.springframework.stereotype.Component;
import com.tyss.optimize.nlp.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_LongPress")
public class LongPress implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            AndroidDriver androidDriver = (AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            elementType=elementType.concat(" in "+elementSplit[0] + " screen ");
		
            String passMessage = (String) nlpRequestModel.getPassMessage();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            log.info("Long Press on " + elementName + " " + elementType);
            TouchAction action = new TouchAction(androidDriver);
            //AndroidElement androidElement = (AndroidElement) element;
            action.longPress(longPressOptions().withElement(element(element)).withDuration(ofSeconds(2))).release().perform();
            action.perform();
            log.info("Pressed on " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in LongPress ", exception);
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

        sb.append("WebElement profile = androidDriver.findElement(ELEMENT);\n");
        sb.append("TouchAction action = new TouchAction(androidDriver);\n");
        sb.append("action.longPress(longPressOptions().withElement(element(profile)).withDuration(ofSeconds(2))).release().perform();\n");
        sb.append("action.perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"in.mohalla.sharechat:id/iv_profile\")");

        return params;
    }

}
