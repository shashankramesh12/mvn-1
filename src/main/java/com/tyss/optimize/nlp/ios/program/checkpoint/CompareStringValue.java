package com.tyss.optimize.nlp.ios.program.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_CompareStringValue")
public class CompareStringValue implements Nlp {
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
            log.info("Verify if " + actualString + " matches " + expectedString);
            Boolean match = false;
            log.info("Expected Value:" + expectedString);
            log.info("Actual Value:" + actualString);

            if (caseSensitive) {
                match = expectedString.equals(actualString);
                log.info("Case Sensitive comparision, result is:" + match);
            } else {
                match = expectedString.equalsIgnoreCase(actualString);
                log.info("Case Insensitive comparision, result is:" + match);
            }

            if (match) {
                log.info(actualString + " is matched with " + expectedString);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(actualString + " did not match " + expectedString);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CompareStringValue ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
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
        sb.append("	match = expectedString.equals(actualString);\n");
        sb.append("} else {\n");
        sb.append("	match = expectedString.equalsIgnoreCase(actualString);\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(actualString + \" is matched with \" + expectedString);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(actualString + \" did not match \" + expectedString);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("caseSensitive::\"true\"");
        params.add("expectedString::\"randomValue\"");
        params.add("actualString::\"randomValue\"");

        return params;
    }

}
