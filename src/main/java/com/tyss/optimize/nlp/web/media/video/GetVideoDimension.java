package com.tyss.optimize.nlp.web.media.video;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component(value = "GetVideoDimension")
public class GetVideoDimension implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {

        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Long startTime = System.currentTimeMillis();
        try{

            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            String passMessage = nlpRequestModel.getPassMessage();
            modifiedFailMessage = nlpRequestModel.getFailMessage();
            String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            passMessage = passMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedFailMessage = modifiedFailMessage.replace("*elementName*", elementName).replace("*elementType*", elementType);
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            WebElement element = (WebElement) attributes.get("element");
            Object height = ((JavascriptExecutor) driver).executeScript("return arguments[0].videoHeight;", element);
            Object width = ((JavascriptExecutor) driver).executeScript("return arguments[0].videoWidth;", element);
            passMessage = passMessage.replace("*returnValue*", width+":"+height);
            nlpResponseModel.getAttributes().put("dimension", width+":"+height);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(passMessage);
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetVideoDimension ", exception);
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



        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }
}
