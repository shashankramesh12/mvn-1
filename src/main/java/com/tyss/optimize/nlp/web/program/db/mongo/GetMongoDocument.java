package com.tyss.optimize.nlp.web.program.db.mongo;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tyss.optimize.nlp.web.constants.GenericConstants.RESPONSE_BODY;
import static com.tyss.optimize.nlp.web.constants.GenericConstants.STATUS;

@Slf4j
@Component(value = "GetMongoDocument")
public class GetMongoDocument implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        JsonObject returnObj = null;
        JSONObject jsonRestAPIObject = null;
        JSONObject resObj = new JSONObject();
        JSONArray finalJsonObj = new JSONArray();
        DBCursor cursor = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            DB mongoDB = (DB) attributes.get("mongoDB");
            String collectionName = (String) attributes.get("collectionName");
            JSONObject parameters = (JSONObject) attributes.get("parameters");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            MongoDBUtil dbUtil = new MongoDBUtil();
            DBCollection collection = mongoDB.getCollection(collectionName);
            BasicDBObject whereClause = dbUtil.buildWhereClause(parameters);

            log.info("Getting Mongo document");
            if (!whereClause.isEmpty()) {
                if (!whereClause.isEmpty()) {
                    cursor = collection.find(whereClause);
                } else {
                    cursor = collection.find();
                }
                while (cursor.hasNext()) {
                    returnObj = new JsonParser().parse(JSON.serialize(cursor.next())).getAsJsonObject();
                    try {
                        jsonRestAPIObject = (JSONObject) new JSONParser().parse(returnObj.toString());
                        finalJsonObj.add(jsonRestAPIObject);
                    } catch (ParseException e) {
                        log.error("Failed to Parse JSONObject");
                        throw e;
                    }
                }

                resObj.put(RESPONSE_BODY, finalJsonObj);
                resObj.put(STATUS, HttpStatus.SC_OK);
                log.debug("Successfully fetched Mongo document");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
            nlpResponseModel.getAttributes().put("resObj", resObj);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in DeleteMongoDocument ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public NlpResponseModel getFilterDBObject(NlpRequestModel nlpRequestModel) {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        String passMessage = nlpRequestModel.getPassMessage();
        DB mongoDB = (DB) attributes.get("mongoDB");
        String collectionName = (String) attributes.get("collectionName");
        JSONObject parameters = (JSONObject) attributes.get("parameters");
        MongoDBUtil dbUtil = new MongoDBUtil();
        DBCollection collection = mongoDB.getCollection(collectionName);
        BasicDBObject whereClause = dbUtil.buildWhereClause(parameters);
        JsonObject returnObj = null;
        JSONObject jsonRestAPIObject = new JSONObject();
        DBCursor cursor = null;
        if (!whereClause.isEmpty()) {
            cursor = collection.find(whereClause);
            while (cursor.hasNext()) {
                returnObj = new JsonParser().parse(JSON.serialize(cursor.next())).getAsJsonObject();
                try {
                    jsonRestAPIObject = (JSONObject) new JSONParser().parse(returnObj.toString());
                } catch (ParseException e) {
                    log.error("Failed to Parse JSONObject");
                    return null;
                }
            }
        }
        DBObject dbObject = (DBObject) JSON.parse(jsonRestAPIObject.toString());
        nlpResponseModel.setMessage(passMessage);
        nlpResponseModel.setStatus(CommonConstants.pass);
        nlpResponseModel.getAttributes().put("filterDBObject", dbObject);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("JsonObject returnObj = null;\n");
        sb.append("JSONObject jsonRestAPIObject = null;\n");
        sb.append("JSONObject resObj = new JSONObject();\n");
        sb.append("JSONArray finalJsonObj = new JSONArray();\n");
        sb.append("DBCursor cursor = null;\n");
        sb.append("Object paramObject = parameters;\n");
        sb.append("JSONObject paramJSONObject = (JSONObject) paramObject;\n");
        sb.append("Object mongoObject = mongoDB;\n");
        sb.append("DB mongoDBObject = (DB) mongoObject;\n");
        sb.append("try {\n");
        sb.append("	DBCollection collection = mongoDBObject.getCollection(collectionName);\n");
        sb.append("	BasicDBObject whereQuery = new BasicDBObject();\n");
        sb.append("	for (Object param : paramJSONObject.keySet()) {\n");
        sb.append("		String paramKey = (String) param;\n");
        sb.append("		Object object = paramJSONObject.get(paramKey);\n");
        sb.append("		if (object instanceof Double) {\n");
        sb.append("			whereQuery.append(paramKey, object);\n");
        sb.append("		} else if (object instanceof Integer) {\n");
        sb.append("			whereQuery.append(paramKey, object);\n");
        sb.append("		} else if (object instanceof Long) {\n");
        sb.append("			whereQuery.append(paramKey, object);\n");
        sb.append("		} else if (object instanceof Boolean) {\n");
        sb.append("			whereQuery.append(paramKey, object);\n");
        sb.append("		} else if (object instanceof JSONArray) {\n");
        sb.append("			JSONArray jsonArray = (JSONArray) object;\n");
        sb.append("			whereQuery.append(paramKey, Arrays.asList((BasicDBList) JSON.parse(jsonArray.toJSONString())));\n");
        sb.append("		} else if (object.getClass().isArray()) {\n");
        sb.append("			whereQuery.put(\"$in\", new BasicDBObject(paramKey, Arrays.asList(object)));\n");
        sb.append("		} else if (object instanceof JSONObject) {\n");
        sb.append("			whereQuery.append(paramKey, JSON.parse(((JSONObject) object).toJSONString()));\n");
        sb.append("		} else {\n");
        sb.append("			whereQuery.append(paramKey, object);\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (!whereQuery.isEmpty()) {\n");
        sb.append("		cursor = collection.find(whereQuery);\n");
        sb.append("	} else {\n");
        sb.append("		cursor = collection.find();\n");
        sb.append("	}\n");
        sb.append("	while (cursor.hasNext()) {\n");
        sb.append("		returnObj = new JsonParser().parse(JSON.serialize(cursor.next())).getAsJsonObject();\n");
        sb.append("		try {\n");
        sb.append("			jsonRestAPIObject = (JSONObject) new JSONParser().parse(returnObj.toString());\n");
        sb.append("			finalJsonObj.add(jsonRestAPIObject);\n");
        sb.append("		} catch (ParseException e) {\n");
        sb.append("			System.out.println(\"Failed to Parse JSONObject\");\n");
        sb.append("			throw e;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	resObj.put(\"responseBody\", finalJsonObj);\n");
        sb.append("	resObj.put(\"status\", HttpStatus.SC_OK);\n");
        sb.append("	System.out.println(\"Successfully fetched Mongo document\");\n");
        sb.append("} catch (Throwable e) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("mongoDB::\"dbObject\"");
        params.add("collectionName::\"xyz\"");
        params.add("parameters::\"{\"field\":\"value\"}\"");

        return params;
    }
}
