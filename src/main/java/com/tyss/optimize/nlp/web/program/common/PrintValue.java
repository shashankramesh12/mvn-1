package com.tyss.optimize.nlp.web.program.common;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "PrintValue")
public class PrintValue implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String input = (String) attributes.get("input");
            String key = (String) attributes.get("key");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("printing value : " + input);
            modifiedPassMessage = passMessage.replace("*input*", key).replace("*returnValue*", input);
            modifiedFailMessage = failMessage.replace("*input*", key).replace("*returnValue*", input);
            log.info("Successfully printed value");
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in printing value ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
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
