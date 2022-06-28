package com.tyss.optimize.nlp.mobile.action.get;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value =  "MOB_GetPerformanceData")
public class GetPerformanceData implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String performanceType = null;
        JSONObject perData = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            AndroidDriver androidDriver = (AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String appPackage = (String) attributes.get("appPackage");
            performanceType = (String) attributes.get("performanceType");
            Integer timeout = (Integer) attributes.get("timeout");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            List<List<Object>> data = androidDriver.getPerformanceData(appPackage, performanceType, timeout);
            HashMap<String, Integer> readableData = new HashMap<>();
            for (int i = 0; i < data.get(0).size(); i++) {
                int val;
                if (data.get(1).get(i) == null) {
                    val = 0;
                } else {
                    val = Integer.parseInt((String) data.get(1).get(i));
                }
                    readableData.put((String) data.get(0).get(i), val);
                    perData = new JSONObject(readableData);
                    log.info("Successfully fetched performance data of " + performanceType);
                    nlpResponseModel.setMessage(passMessage.replace("*performanceType*", performanceType).replace("*returnValue*", perData.toString()));
                    nlpResponseModel.setStatus(CommonConstants.pass);
            }
            nlpResponseModel.getAttributes().put("perData",perData);
        }
         catch(Throwable e)
            {
                log.error("NLP_EXCEPTION in GetPerformanceData ", e);
                failMessage = failMessage.replace("*performanceType*",performanceType);
                String exceptionSimpleName = e.getClass().getSimpleName();
                if (containsChildNlp)
                    throw new NlpException(exceptionSimpleName);
                nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
            }
        Long endTime=System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime-startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("JSONObject perData = null;\n");
        sb.append("List<List<Object>> data = androidDriver.getPerformanceData(appPackage, performanceType, timeout);\n");
        sb.append("HashMap<String, Integer> readableData = new HashMap<>();\n");
        sb.append("for (int i = 0; i < data.get(0).size(); i++) {\n");
        sb.append("	int val;\n");
        sb.append("	if (data.get(1).get(i) == null) {\n");
        sb.append("		val = 0;\n");
        sb.append("	} else {\n");
        sb.append("		val = Integer.parseInt((String) data.get(1).get(i));\n");
        sb.append("	}\n");
        sb.append("	readableData.put((String) data.get(0).get(i), val);\n");
        sb.append("	perData = new JSONObject(readableData);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("appPackage::\"randomValue\"");
        params.add("performanceType::\"randomValue\"");
        params.add("timeout::2000");

        return params;
    }

}
