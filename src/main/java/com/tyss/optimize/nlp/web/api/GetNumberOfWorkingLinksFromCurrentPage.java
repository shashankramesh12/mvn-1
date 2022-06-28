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
@Component(value = "GetNumberOfWorkingLinksFromCurrentPage")
public class GetNumberOfWorkingLinksFromCurrentPage implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        List<String> links = null;
        try{
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            modifiedFailMessage = nlpRequestModel.getFailMessage();
            links = (List<String>) attributes.get("links");
            Integer statusCode = (Integer) attributes.get("statusCode");
            List<String> unmatchedStatusCodeList = new ArrayList<>();
            WebDriver driver =null;
            if(Objects.nonNull(links)){
                List<AtomicReference<Response>> responseList = new ArrayList<>();
                for (String link : links) {
                    AtomicReference<Response> response = new AtomicReference<>();
                    new WebDriverDevTools().getDevTools(driver).addListener(Network.responseReceived(), responseReceived -> {
                        if(responseReceived.getResponse().getUrl().equalsIgnoreCase(link)) {
                            response.set(responseReceived.getResponse());
                            responseList.add(response);
                            if(!response.get().getStatus().equals(statusCode)){
                                unmatchedStatusCodeList.add(link);
                            }
                        }
                    });}

                log.info("Status codes of all the links is " + statusCode);
                modifiedPassMessage = passMessage.replace("*returnValue*", String.valueOf(unmatchedStatusCodeList));
                nlpResponseModel.getAttributes().put("returnValue",unmatchedStatusCodeList);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
            else {
                log.error("Failed to verify the status codes of all the links is " + statusCode);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
            }
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetNumberOfWorkingLinksFromCurrentPage ", exception);
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
