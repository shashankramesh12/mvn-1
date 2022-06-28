package com.tyss.optimize.nlp.web.program.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "PrintJSONResponse")
public class PrintJSONResponse implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        JSONObject jObject = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            JSONObject jsonResponse = (JSONObject) attributes.get("jsonResponse");
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Printing JSON response");
            String responseJSON = (String) jsonResponse.get("Response");
            log.info("PrintJSONResponse:Response: " + responseJSON);
            log.info("PrintJSONResponse:StatusCode: " + (int) jsonResponse.get("StatusCode"));
            log.info("PrintJSONResponse:ResponseObject: " + ((Response) jsonResponse.get("ResponseObject")).prettyPrint());
            log.info("Successfully printed JSON response");
            jObject = (JSONObject) new JSONParser().parse(responseJSON);
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (ParseException e) {
            log.error("NLP_EXCEPTION in PrintJSONResponse ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("responseJSON", jObject);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Object responseObject = jsonResponse;\n");
        sb.append("JSONObject responseJSONObject = (JSONObject) responseObject;\n");
        sb.append("String responseJSON = (String) responseJSONObject.get(\"Response\");\n");
        sb.append("System.out.println(\"PrintJSONResponse:Response: \" + responseJSON);\n");
        sb.append("System.out.println(\"PrintJSONResponse:StatusCode: \" + (int) responseJSONObject.get(\"StatusCode\"));\n");
        sb.append("System.out.println(\"PrintJSONResponse:ResponseObject: \" + ((Response) responseJSONObject.get(\"ResponseObject\")).prettyPrint());\n");
        sb.append("JSONObject jObject = (JSONObject) new JSONParser().parse(responseJSON);\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("jsonResponse::\"randomObject\"");

        return params;
    }
}
