package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "DeleteMongoDocument")
public class DeleteMongoDocument implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        boolean flag = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            DB mongoDB = (DB) attributes.get("mongoDB");
            final String collectionName = (String) attributes.get("collectionName");
            JSONObject parameters = (JSONObject) attributes.get("parameters");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            DBCollection collection = mongoDB.getCollection(collectionName);
            MongoDBUtil dbUtil = new MongoDBUtil();
            BasicDBObject whereClause = dbUtil.buildWhereClause(parameters);
            log.info("Deleting document from Mongo database collection");
            if (!whereClause.isEmpty()) {
                collection.remove(whereClause);
                log.info("Successfully deleted document from Mongo database collection");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
                flag = true;
            }
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in DeleteMongoDocument ", e);
            flag = false;
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        nlpResponseModel.getAttributes().put("flag", flag);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public NlpResponseModel deleteAll(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        String passMessage = nlpRequestModel.getPassMessage();
        DB mongoDB = (DB) attributes.get("mongoDB");
        final String collectionName = (String) attributes.get("collectionName");
        boolean flag = false;
        DBCollection collection = mongoDB.getCollection(collectionName);
        BasicDBObject whereClause = new BasicDBObject();
        collection.remove(whereClause);
        log.debug("Deleted Documents Successfully");
        flag = true;
        nlpResponseModel.setMessage(passMessage);
        nlpResponseModel.setStatus(CommonConstants.pass);
        nlpResponseModel.getAttributes().put("flag", flag);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean flag = false;\n");
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
        sb.append("		collection.remove(whereQuery);\n");
        sb.append("		System.out.println(\"Successfully deleted document from Mongo database collection\");\n");
        sb.append("		flag = true;\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

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
