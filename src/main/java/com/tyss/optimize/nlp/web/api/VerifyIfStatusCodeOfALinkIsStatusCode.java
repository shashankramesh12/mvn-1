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
@Component(value = "VerifyIfStatusCodeOfALinkIsStatusCode")
public class VerifyIfStatusCodeOfALinkIsStatusCode implements Nlp {

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
        Integer statusCode = null;
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
            statusCode = (Integer) attributes.get("statusCode");
            String link = apiUrl;
            WebDriver driver =null;
            Boolean verifyStatusFlag = false;
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
            if(response.get().getStatus().equals(statusCode)) {
                verifyStatusFlag = true;
            }
            log.info("The status code of link " + apiUrl + " is " + statusCode);
            modifiedPassMessage = passMessage.replace("*returnValue*", verifyStatusFlag.toString());
            nlpResponseModel.getAttributes().put("returnValue", verifyStatusFlag);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(modifiedPassMessage);
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in VerifyIfStatusCodeOfALinkIsStatusCode ", exception);
            modifiedFailMessage = failMessage.replace("*link*", apiUrl);
            modifiedFailMessage = modifiedFailMessage.replace("*statusCode*", statusCode.toString());
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
