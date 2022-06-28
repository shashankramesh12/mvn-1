package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;

public class MongoDBUtil {

    public BasicDBObject buildWhereClause(JSONObject parameters) {
        BasicDBObject whereQuery = new BasicDBObject();
        for (Object param : parameters.keySet()) {
            String paramKey = (String) param;
            Object object = parameters.get(paramKey);

            if (object instanceof Double) {
                whereQuery.append(paramKey, object);
            } else if (object instanceof Integer) {
                whereQuery.append(paramKey, object);
            } else if (object instanceof Long) {
                whereQuery.append(paramKey, object);
            } else if (object instanceof Boolean) {
                whereQuery.append(paramKey, object);
            } else if (object instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) object;
                whereQuery.append(paramKey, Arrays.asList((BasicDBList) JSON.parse(jsonArray.toJSONString())));
            } else if (object.getClass().isArray()) {
                whereQuery.put("$in", new BasicDBObject(paramKey, Arrays.asList(object)));
            } else if (object instanceof JSONObject) {
                whereQuery.append(paramKey, JSON.parse(((JSONObject) object).toJSONString()));
            } else {
                whereQuery.append(paramKey, object);
            }
        }
        return whereQuery;
    }
}
