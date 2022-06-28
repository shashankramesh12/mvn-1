package com.tyss.optimize.nlp.system.convertions;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "ConvertMinutesToSeconds")
public class ConvertMinutesToSeconds implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String modifiedFailMessage = null;
        String modifiedPassMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        Double minutes = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            minutes = (Double.valueOf(String.valueOf(attributes.get("minutes"))));
            Double seconds = minutes * 60;
            String passMessage = nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedPassMessage = passMessage.replace("*minutes*", String.valueOf(minutes));
            modifiedPassMessage = modifiedPassMessage.replace("*returnValue*", String.valueOf(seconds));
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("seconds", seconds);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in ConvertMinutesToSeconds ", exception);
            String failMessage = nlpRequestModel.getFailMessage();
            modifiedFailMessage = failMessage.replace("*minutes*", String.valueOf(minutes));
            nlpResponseModel.setMessage(modifiedFailMessage);
            nlpResponseModel.setStatus(CommonConstants.fail);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    @Override
    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("try {\n");
        sb.append("	Double minutes = Double.valueOf(String.valueOf(Minutes));\n");
        sb.append("	Double seconds = minutes * 60;\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("Minutes::360");

        return params;
    }
}
