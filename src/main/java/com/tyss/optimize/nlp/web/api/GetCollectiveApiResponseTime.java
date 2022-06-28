package com.tyss.optimize.nlp.web.api;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.v96.network.Network;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component(value = "GetCollectiveApiResponseTime")
public class GetCollectiveApiResponseTime implements Nlp {

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
            String passMessage = nlpRequestModel.getPassMessage();
            modifiedFailMessage = nlpRequestModel.getFailMessage();
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            AtomicReference<Integer> completeResponseTime = new AtomicReference<>(0);
            AtomicReference<Map<String, Integer>> response = new AtomicReference<>();
            new WebDriverDevTools().getDevTools(driver).addListener(Network.responseReceived(), responseReceived -> {
                if(Objects.nonNull(responseReceived.getResponse())) {
                    Double end = (Double) responseReceived.getResponse().getTiming().get().getSendEnd();
                    Double start = (Double) responseReceived.getResponse().getTiming().get().getSendStart();
                    Integer responseTime = Math.toIntExact(Math.round(end - start));
                    completeResponseTime.set(completeResponseTime.get()+responseTime);
                    response.get().put(responseReceived.getResponse().getUrl(), responseTime);
                }
            });
            passMessage = passMessage.replace("*returnValue*", completeResponseTime.get().toString());
            nlpResponseModel.getAttributes().put("response", response.get());
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(passMessage);
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetCollectiveApiResponseTime ", exception);
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
