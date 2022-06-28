package com.tyss.optimize.nlp.web.console;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component(value = "GetValueFromLocalStorage")
public class GetValueFromLocalStorage implements Nlp {

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
            String key = attributes.get("key").toString();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            String passMessage = nlpRequestModel.getPassMessage();
            modifiedFailMessage = nlpRequestModel.getFailMessage();
            modifiedFailMessage = modifiedFailMessage.replace("*key*", key);
            passMessage = passMessage.replace("*key*", key);
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            WebStorage webStorage = (WebStorage) new Augmenter().augment(driver);
            LocalStorage localStorage = webStorage.getLocalStorage();
            String value = localStorage.getItem(key);
            if(Objects.isNull(value)) {
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setMessage(modifiedFailMessage);
            } else {
                passMessage = passMessage.replace("*returnValue*", value);
                nlpResponseModel.setStatus(CommonConstants.pass);
                nlpResponseModel.setMessage(passMessage);
            }
            nlpResponseModel.getAttributes().put("value",value);
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetValueFromLocalStorage ", exception);
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

        sb.append("WebStorage webStorage = (WebStorage) new Augmenter().augment(driver);\n");
        sb.append("LocalStorage localStorage = webStorage.getLocalStorage();\n");
        sb.append("String value = localStorage.getItem(key);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("key::\"xyz\"");

        return params;
    }

}
