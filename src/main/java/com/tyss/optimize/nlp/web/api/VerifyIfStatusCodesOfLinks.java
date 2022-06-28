package com.tyss.optimize.nlp.web.api;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.devtools.v96.network.Network;
import org.openqa.selenium.devtools.v96.network.model.Response;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component(value = "VerifyIfStatusCodesOfLinks")
public class VerifyIfStatusCodesOfLinks implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        try{
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            modifiedFailMessage = nlpRequestModel.getFailMessage();
            WebDriver driver =null;
            List<WebElement> links = null;
            List<String> allLinks = new ArrayList<>();
            List<String> workingLinks = new ArrayList<>();
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
                links = driver.findElements(By.tagName("a"));
                links.stream().map(WebElement::getText).forEach(allLinks::add);
            }
            if(Objects.nonNull(links)){
                List<AtomicReference<Response>> responseList = new ArrayList<>();
                for (String link : allLinks) {
                    AtomicReference<Response> response = new AtomicReference<>();
                    new WebDriverDevTools().getDevTools(driver).addListener(Network.responseReceived(), responseReceived -> {
                        if(responseReceived.getResponse().getUrl().equalsIgnoreCase(link)) {
                            response.set(responseReceived.getResponse());
                            responseList.add(response);
                            if(response.get().getStatus() == 200){
                                workingLinks.add(link);
                            }
                        }
                    });}

                log.info("The working links from current page are " +  workingLinks);
                modifiedPassMessage = passMessage.replace("*returnValue*", workingLinks.toString());
                nlpResponseModel.getAttributes().put("returnValue", workingLinks);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
            else {
                log.error("Failed to fetch all broken links from current page ");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
            }
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetWorkingLinksFromCurrentPage ", exception);
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

    @Override
    public StringBuilder getTestCode() throws NlpException {
        return null;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        return null;
    }
}
