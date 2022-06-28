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
@Component(value = "VerifyIfImageIsBroken")
public class VerifyIfImageIsBroken implements Nlp {

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
            String imageUrl = attributes.get("imageUrl").toString();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            passMessage = passMessage.replace("*imageUrl*",imageUrl);
            modifiedFailMessage = modifiedFailMessage.replace("*imageUrl*",imageUrl);
            WebElement imageElement = driver.findElement(By.xpath("//img[@src='"+imageUrl+"']"));
            if(Objects.isNull(imageElement)){
                imageElement = driver.findElement(By.cssSelector("//img[@src='"+imageUrl+"']"));
            }
            Boolean imageBroken = false;
            if (imageElement.getAttribute("naturalWidth").equals("0")) {
                imageBroken = true;
            } else {
                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(imageElement.getAttribute("src"));
                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() != 200) {
                    imageBroken = true;
                }
            }
            nlpResponseModel.getAttributes().put("brokenImages", imageBroken);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(passMessage);
        } catch (IOException exception) {
            log.error("IOException in VerifyIfImageIsBroken ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in VerifyIfImageIsBroken ", exception);
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
