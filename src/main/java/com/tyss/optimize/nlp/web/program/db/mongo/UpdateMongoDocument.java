package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tyss.optimize.nlp.web.constants.GenericConstants.ID;

@Slf4j
@Component(value = "UpdateMongoDocument")
public class UpdateMongoDocument implements Nlp {

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
            String collectionName = (String) attributes.get("collectionName");
            JSONObject parameters = (JSONObject) attributes.get("parameters");
            JSONObject updateJSON = (JSONObject) attributes.get("updateJSON");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            MongoDBUtil dbUtil = new MongoDBUtil();
            DBCollection collection = mongoDB.getCollection(collectionName);
            JsonHelper jsonHelper = new JsonHelper();
            log.info("Updating Mongo document");
            if (updateJSON != null && !updateJSON.isEmpty() && jsonHelper.isJSONValid(updateJSON.toJSONString())) {
                JSONObject retrieveObj = null;
                try {
                    DBObject dbObject = (DBObject) ((NlpResponseModel) new GetMongoDocument().getFilterDBObject(nlpRequestModel))
                            .getAttributes().get("filterDBObject");
                    retrieveObj = (JSONObject) new JSONParser()
                            .parse(dbObject.toString());
                } catch (ParseException e) {
                    log.error("Failed to update Mongo document \n" + e);
                    flag = false;
                    String exceptionSimpleName = e.getClass().getSimpleName();
                    if (containsChildNlp) {
                        throw new NlpException(exceptionSimpleName);
                    }
                    nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
                }
                if (retrieveObj == null || retrieveObj.isEmpty()) {
                    log.error("Failed to update Mongo document");
                    nlpResponseModel.setMessage(failMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                    flag = false;
                }
                try {
                    updateJSON.remove(ID);
                    DBObject update = new BasicDBObject();
                    update.put("$set", dbUtil.buildWhereClause(updateJSON));
                    DBObject filterQry = dbUtil.buildWhereClause(parameters);
                    collection.findAndModify(filterQry, update);
                    log.info("Successfully updated Mongo document");
                    nlpResponseModel.setMessage(passMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    flag = true;
                } catch (Exception e) {
                    log.error("NLP_EXCEPTION in UpdateMongoDocument ", e);
                    flag = false;
                    String exceptionSimpleName = e.getClass().getSimpleName();
                    if (containsChildNlp) {
                        throw new NlpException(exceptionSimpleName);
                    }
                    nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
                }
            } else {
                log.error("Failed to update Mongo document");
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                flag = false;
            }
            nlpResponseModel.getAttributes().put("flag", flag);
        } catch (Exception e) {
            log.error("NLP_EXCEPTION in UpdateMongoDocument ", e);
            flag = false;
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean flag = false, isJSONValid = false;\n");
        sb.append("Object updateObject = updateJSON;\n");
        sb.append("JSONObject updateJSONObject = (JSONObject) updateObject;\n");
        sb.append("Object paramObject = parameters;\n");
        sb.append("JSONObject paramJSONObject = (JSONObject) paramObject;\n");
        sb.append("Object mongoObject = mongoDB;\n");
        sb.append("DB mongoDBObject = (DB) mongoObject;\n");
        sb.append("try {\n");
        sb.append("	final ObjectMapper mapper = new ObjectMapper();\n");
        sb.append("		mapper.readTree(updateJSONObject.toJSONString());\n");
        sb.append("		isJSONValid = true;\n");
        sb.append("} catch (IOException e) {\n");
        sb.append("		System.out.println(\"Failed to validate JSON Object\");\n");
        sb.append("		isJSONValid = false;\n");
        sb.append("}\n");
        sb.append("try {\n");
        sb.append("DBCollection collection = mongoDBObject.getCollection(collectionName);\n");
        sb.append("if (updateJSONObject != null && !updateJSONObject.isEmpty() && isJSONValid) {\n");
        sb.append("	JSONObject retrieveObj = null;\n");
        sb.append("	try {\n");
        sb.append("		BasicDBObject whereClause = new BasicDBObject();\n");
        sb.append("		for (Object param : paramJSONObject.keySet()) {\n");
        sb.append("			String paramKey = (String) param;\n");
        sb.append("			Object object = paramJSONObject.get(paramKey);\n");
        sb.append("			if (object instanceof Double) {\n");
        sb.append("				whereClause.append(paramKey, object);\n");
        sb.append("			} else if (object instanceof Integer) {\n");
        sb.append("				whereClause.append(paramKey, object);\n");
        sb.append("			} else if (object instanceof Long) {\n");
        sb.append("				whereClause.append(paramKey, object);\n");
        sb.append("			} else if (object instanceof Boolean) {\n");
        sb.append("				whereClause.append(paramKey, object);\n");
        sb.append("			} else if (object instanceof JSONArray) {\n");
        sb.append("				JSONArray jsonArray = (JSONArray) object;\n");
        sb.append("				whereClause.append(paramKey, Arrays.asList((BasicDBList) JSON.parse(jsonArray.toJSONString())));\n");
        sb.append("			} else if (object.getClass().isArray()) {\n");
        sb.append("				whereClause.put(\"$in\", new BasicDBObject(paramKey, Arrays.asList(object)));\n");
        sb.append("			} else if (object instanceof JSONObject) {\n");
        sb.append("				whereClause.append(paramKey, JSON.parse(((JSONObject) object).toJSONString()));\n");
        sb.append("			} else {\n");
        sb.append("				whereClause.append(paramKey, object);\n");
        sb.append("			}\n");
        sb.append("		}\n");
        sb.append("     JsonObject returnObj = null;\n");
        sb.append("     JSONObject jsonRestAPIObject = new JSONObject();\n");
        sb.append("     DBCursor cursor = null;\n");
        sb.append("     if (!whereClause.isEmpty()) {\n");
        sb.append("        cursor = collection.find(whereClause);\n");
        sb.append("        while (cursor.hasNext()) {\n");
        sb.append("             returnObj = new JsonParser().parse(JSON.serialize(cursor.next())).getAsJsonObject();\n");
        sb.append("             try {\n");
        sb.append("                 jsonRestAPIObject = (JSONObject) new JSONParser().parse(returnObj.toString());\n");
        sb.append("             } catch (ParseException e) {\n");
        sb.append("                 System.out.println(\"Failed to Parse JSONObject\");\n");
        sb.append("                 flag = false;\n");
        sb.append("             }\n");
        sb.append("         }\n");
        sb.append("     }\n");
        sb.append("     DBObject dbObject = (DBObject) JSON.parse(jsonRestAPIObject.toString());\n");
        sb.append("		retrieveObj = (JSONObject) new JSONParser().parse(dbObject.toString());\n");
        sb.append("	} catch (ParseException e) {\n");
        sb.append("		System.out.println(\"Failed to parse DBObject\");\n");
        sb.append("		flag = false;\n");
        sb.append("	}\n");
        sb.append("	if (retrieveObj == null || retrieveObj.isEmpty()) {\n");
        sb.append("		System.out.println(\"Failed to retrieve document\");\n");
        sb.append("		flag = false;\n");
        sb.append("	}else {\n");
        sb.append("		try {\n");
        sb.append("			updateJSONObject.remove(\"_id\");\n");
        sb.append("			DBObject update = new BasicDBObject();\n");
        sb.append("			BasicDBObject updateQuery = new BasicDBObject();\n");
        sb.append("			for (Object param : updateJSONObject.keySet()) {\n");
        sb.append("				String paramKey = (String) param;\n");
        sb.append("				Object object = updateJSONObject.get(paramKey);\n");
        sb.append("				if (object instanceof Double) {\n");
        sb.append("					updateQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof Integer) {\n");
        sb.append("					updateQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof Long) {\n");
        sb.append("					updateQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof Boolean) {\n");
        sb.append("					updateQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof JSONArray) {\n");
        sb.append("					JSONArray jsonArray = (JSONArray) object;\n");
        sb.append("					updateQuery.append(paramKey, Arrays.asList((BasicDBList) JSON.parse(jsonArray.toJSONString())));\n");
        sb.append("				} else if (object.getClass().isArray()) {\n");
        sb.append("					updateQuery.put(\"$in\", new BasicDBObject(paramKey, Arrays.asList(object)));\n");
        sb.append("				} else if (object instanceof JSONObject) {\n");
        sb.append("					updateQuery.append(paramKey, JSON.parse(((JSONObject) object).toJSONString()));\n");
        sb.append("				} else {\n");
        sb.append("					updateQuery.append(paramKey, object);\n");
        sb.append("				}\n");
        sb.append("			}\n");
        sb.append("			update.put(\"$set\", updateQuery);\n");
        sb.append("			BasicDBObject whereQuery = new BasicDBObject();\n");
        sb.append("			for (Object param : paramJSONObject.keySet()) {\n");
        sb.append("				String paramKey = (String) param;\n");
        sb.append("				Object object = paramJSONObject.get(paramKey);\n");
        sb.append("				if (object instanceof Double) {\n");
        sb.append("					whereQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof Integer) {\n");
        sb.append("					whereQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof Long) {\n");
        sb.append("					whereQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof Boolean) {\n");
        sb.append("					whereQuery.append(paramKey, object);\n");
        sb.append("				} else if (object instanceof JSONArray) {\n");
        sb.append("					JSONArray jsonArray = (JSONArray) object;\n");
        sb.append("					whereQuery.append(paramKey, Arrays.asList((BasicDBList) JSON.parse(jsonArray.toJSONString())));\n");
        sb.append("				} else if (object.getClass().isArray()) {\n");
        sb.append("					whereQuery.put(\"$in\", new BasicDBObject(paramKey, Arrays.asList(object)));\n");
        sb.append("				} else if (object instanceof JSONObject) {\n");
        sb.append("					whereQuery.append(paramKey, JSON.parse(((JSONObject) object).toJSONString()));\n");
        sb.append("				} else {\n");
        sb.append("					whereQuery.append(paramKey, object);\n");
        sb.append("				}\n");
        sb.append("			}\n");
        sb.append("			DBObject filterQry = (DBObject)whereQuery;\n");
        sb.append("			collection.findAndModify(filterQry, update);\n");
        sb.append("			System.out.println(\"Successfully updated Mongo document\");\n");
        sb.append("			flag = true;\n");
        sb.append("		} catch (Exception e) {\n");
        sb.append("			System.out.println(\"Failed to update Mongo document\");\n");
        sb.append("			flag = false;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Failed to parse JSONObject\");\n");
        sb.append("	flag = false;\n");
        sb.append("}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("mongoDB::\"dbObject\"");
        params.add("collectionName::\"xyz\"");
        params.add("parameters::\"{\"field\":\"value\"}\"");
        params.add("updateJSON::\"{\"field\":\"value\"}\"");

        return params;
    }
}
