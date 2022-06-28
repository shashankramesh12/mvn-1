package com.tyss.optimize.nlp.web.generic;

import com.google.common.collect.Ordering;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component(value = "VerifyIfListIstInAscendingOrder")
public class VerifyIfListIstInAscendingOrder implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null, failMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        List<String> list = new ArrayList<>();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }

            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            list = (List<String>) attributes.get("list");
            boolean sorted = Ordering.natural().isOrdered(list);
            if(sorted){
                log.info("The list " + list + " is in ascending order");
                modifiedPassMessage = passMessage.replace("*returnValue*", String.valueOf(sorted));
                nlpResponseModel.setStatus(CommonConstants.pass);
                nlpResponseModel.setMessage(modifiedPassMessage);
            }else {
                log.error("The List  " + list + " is not in ascending order");
                modifiedFailMessage = failMessage.replace("*list*", String.valueOf(list));
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setMessage(modifiedFailMessage);
            }

        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfListIstInAscendingOrder ", exception);
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

    @Override
    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean sorted = Ordering.natural().isOrdered(list);\n");
        sb.append("if(sorted){\n");
        sb.append("	System.out.println(\"The List \" + list + \" is in ascending order\");\n");
        sb.append("}else {\n");
        sb.append("	System.out.println(\"Failed to verify if List \" + list + \" is in ascending order\");\n");
        sb.append("}\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("list::\"randomObject\"");

        return params;
    }
}
