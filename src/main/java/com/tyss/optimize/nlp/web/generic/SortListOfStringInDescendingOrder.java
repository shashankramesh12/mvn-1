package com.tyss.optimize.nlp.web.generic;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component(value = "SortListOfStringInDescendingOrder")
public class SortListOfStringInDescendingOrder implements Nlp {

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
            List<String> sortedList = list.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
            log.info("The sorted list is " + sortedList);
            modifiedPassMessage = passMessage.replace("*returnValue*", sortedList.toString());
            nlpResponseModel.getAttributes().put("returnValue", sortedList);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.setMessage(modifiedPassMessage);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in SortListOfStringInDescendingOrder ", exception);
            modifiedFailMessage = failMessage.replace("*list*", list.toString());
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
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
        StringBuilder sb = new StringBuilder();

        sb.append("List<String> sortedList = list.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("list::\"randomObject\"");

        return params;
    }
}
