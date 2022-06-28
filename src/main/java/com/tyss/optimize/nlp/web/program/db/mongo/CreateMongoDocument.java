package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "CreateMongoDocument")
public class CreateMongoDocument implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        DBCollection collection = null;
        JsonHelper jsonHelper = new JsonHelper();
        DBObject insertDBObj = null;
        boolean flag = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            DB mongoDB = (DB) attributes.get("mongoDB");
            String collectionName = (String) attributes.get("collectionName");
            JSONObject createJSON = (JSONObject) attributes.get("createJSON");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            if (createJSON != null && !createJSON.isEmpty() && jsonHelper.isJSONValid(createJSON.toJSONString())) {
                insertDBObj = (DBObject) JSON.parse(createJSON.toString());
                collection = mongoDB.getCollection(collectionName);
                collection.insert(insertDBObj);
                log.info("Successfully inserted document into database collection");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                flag = true;
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CreateMongoDocument ", exception);
            flag = false;
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());

        }
        nlpResponseModel.getAttributes().put("flag", flag);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean flag = false, isJSONValid = false;\n");
        sb.append("Object createObject = createJSON;\n");
        sb.append("JSONObject createJSONObject = (JSONObject) createObject;\n");
        sb.append("Object mongoObject = mongoDB;\n");
        sb.append("DB mongoDBObject = (DB) mongoObject;\n");
        sb.append("try {\n");
        sb.append("	final ObjectMapper mapper = new ObjectMapper();\n");
        sb.append("		mapper.readTree(createJSONObject.toJSONString());\n");
        sb.append("		isJSONValid = true;\n");
        sb.append("} catch (IOException e) {\n");
        sb.append("		System.out.println(\"Failed to validate JSON Object\");\n");
        sb.append("		isJSONValid = false;\n");
        sb.append("}\n");
        sb.append("try {\n");
        sb.append("		if (createJSONObject != null && !createJSONObject.isEmpty() && isJSONValid) {\n");
        sb.append("			DBObject insertDBObj = (DBObject) JSON.parse(createJSONObject.toString());\n");
        sb.append("			DBCollection collection = mongoDBObject.getCollection(collectionName);\n");
        sb.append("			collection.insert(insertDBObj);\n");
        sb.append("			System.out.println(\"Successfully inserted document into database collection\");\n");
        sb.append("			flag = true;\n");
        sb.append("		}\n");
        sb.append("	} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("mongoDB::\"dbObject\"");
        params.add("collectionName::\"xyz\"");
        params.add("createJSON::\"{\"field\":\"value\"}\"");

        return params;
    }
}
