package com.tyss.optimize.nlp.web.media.image;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component(value = "GetAllBrokenImages")
public class GetAllBrokenImages implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {

        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Long startTime = System.currentTimeMillis();
        try{
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            String passMessage = nlpRequestModel.getPassMessage();
            modifiedFailMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            List<WebElement> imagesList = driver.findElements(By.tagName("img"));
            List<String> brokenImages = new ArrayList();
            imagesList.stream().forEach(imageElement -> {
                if (imageElement.getAttribute("naturalWidth").equals("0")) {
                    brokenImages.add(imageElement.getAttribute("src"));
                } else {
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpGet request = new HttpGet(imageElement.getAttribute("src"));
                    try {
                        HttpResponse response = client.execute(request);
                        if (response.getStatusLine().getStatusCode() != 200){
                            brokenImages.add(imageElement.getAttribute("src"));
                        }
                    } catch (IOException e) {
                        log.error("NLP_EXCEPTION in VerifyAllBrokenImages ", e);
                    }
                }
            });
            nlpResponseModel.getAttributes().put("brokenImages", brokenImages);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(passMessage);
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetAllBrokenImages ", exception);
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
