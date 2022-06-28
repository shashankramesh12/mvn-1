package com.tyss.optimize.nlp.web.generic;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Component(value = "VerifyIfList1IsEqualToList2")
public class VerifyIfList1IsEqualToList2 implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null, failMessage = null;
        String modifiedPassMessage = null;
        Long startTime = System.currentTimeMillis();
        Boolean isListsEqual = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }

            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            List<String> list1 = (List<String>) attributes.get("list1");
            List<String> list2 = (List<String>) attributes.get("list2");
            List<String> listResult = list1.stream().filter(ele -> list2.stream().anyMatch(ele2 -> ele2.equals(ele))).collect(Collectors.toList());
            if(listResult.size() == list1.size()){
                isListsEqual = true;
                log.info("The list " +  list1 + " is equal to list " + list2);
                modifiedPassMessage = passMessage.replace("*list1*", list1.toString());
                modifiedPassMessage = modifiedPassMessage.replace("*list2*", list2.toString());
                nlpResponseModel.getAttributes().put("returnValue", isListsEqual);
                nlpResponseModel.setStatus(CommonConstants.pass);
                nlpResponseModel.setMessage(modifiedPassMessage);
            }else {
                log.error("The list1 " + list1 + " is not equal to list2 " + list2);
                modifiedFailMessage = passMessage.replace("*list1*", list1.toString());
                modifiedFailMessage = modifiedPassMessage.replace("*list2*", list2.toString());
                nlpResponseModel.getAttributes().put("returnValue", isListsEqual);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setMessage(modifiedFailMessage);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfList1IsEqualToList2 ", exception);
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

        sb.append("List<String> listResult = list1.stream().filter(ele -> list2.stream().anyMatch(ele2 -> ele2.equals(ele))).collect(Collectors.toList());\n");
        sb.append("if(listResult.size() == list1.size()){\n");
        sb.append("	System.out.println(\"The list \" +  list1 + \" is equal to list \" + list2);\n");
        sb.append("}else {\n");
        sb.append("	System.out.println(\"Failed to verify list \" + list1 + \" is equal to list \" + list2);\n");
        sb.append("}\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("list1::\"randomObject\"");
        params.add("list2::\"randomObject\"");

        return params;
    }
}
