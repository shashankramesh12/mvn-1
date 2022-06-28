package com.tyss.optimize.nlp.web.api;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.v96.network.Network;
import org.openqa.selenium.devtools.v96.network.model.Response;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component(value = "GetStatusCodeOfALink")
public class GetStatusCodeOfALink implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String passMessage = null, failMessage = null;
        String modifiedFailMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        String apiUrl = null;
        try{
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            apiUrl = attributes.get("link").toString();
            String link = apiUrl;
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            AtomicReference<Response> response = new AtomicReference<>();
            new WebDriverDevTools().getDevTools(driver).addListener(Network.responseReceived(), responseReceived -> {
                if(responseReceived.getResponse().getUrl().equalsIgnoreCase(link)) {
                    response.set(responseReceived.getResponse());
                    return;
                }
            });
            if (Objects.nonNull(response.get())){
                log.info("The status code of a link " +  apiUrl + " is " +  response.get().getStatus());
                modifiedPassMessage = passMessage.replace("*returnValue*", response.get().getStatus().toString());
                nlpResponseModel.getAttributes().put("returnValue", response.get().getStatus());
                nlpResponseModel.setStatus(CommonConstants.pass);
                nlpResponseModel.setMessage(modifiedPassMessage);
            } else {
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setMessage(failMessage);
            }


        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetStatusCodeOfALink ", exception);
            modifiedFailMessage = failMessage.replace("*link*", apiUrl);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
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
