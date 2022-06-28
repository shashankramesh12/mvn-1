package com.tyss.optimize.nlp.web.program.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "ComparePartialStringValue")
public class ComparePartialStringValue implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String expectedString = (String) attributes.get("expectedString");
            String actualString = (String) attributes.get("actualString");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*actualString*", actualString).replace("*expectedString*", expectedString);
            String modifiedPassMessage = passMessage.replace("*actualString*", actualString).replace("*expectedString*", expectedString);
            log.info("Verifying if actual value " + actualString + " contains expected value " + expectedString);
            Boolean match = false;
            if (caseSensitive) {
                match = actualString.contains(expectedString);
                log.info("Case sensitive comparison, result is: " + match);
            } else {
                match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualString, expectedString);
                log.info("Case insensitive comparison, result is: " + match);
            }
            if (match) {
                log.info(actualString + " contains " + expectedString);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(actualString + " does not contain " + expectedString);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in ComparePartialStringValue ", exception);
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

        sb.append("boolean match = false;\n");
        sb.append("boolean isSensitive = Boolean.valueOf(caseSensitive);\n");
        sb.append("if (isSensitive) {\n");
        sb.append("	match = actualString.contains(expectedString);\n");
        sb.append("} else {\n");
        sb.append("	match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualString, expectedString);\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(actualString + \" contains \" + expectedString);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(actualString + \" does not contain \" + expectedString);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedString::\"xyz\"");
        params.add("actualString::\"abc\"");
        params.add("caseSensitive::\"true\"");

        return params;
    }
}
