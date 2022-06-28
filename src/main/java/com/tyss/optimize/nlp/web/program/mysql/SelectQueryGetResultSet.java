package com.tyss.optimize.nlp.web.program.mysql;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "SelectQueryGetResultSet")
public class SelectQueryGetResultSet implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        ResultSet resultSet = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            Connection connection = (Connection) attributes.get("connection");
            String query = (String) attributes.get("query");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            passModifiedMessage = passMessage.replace("*query*", query);
            failModifiedMessage = failMessage.replace("*query*", query);
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numberOfColumns = resultSetMetaData.getColumnCount();
            JSONObject response = new JSONObject();
            JSONArray json = new JSONArray();
            while (resultSet.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numberOfColumns; i++) {
                    String column_name = resultSetMetaData.getColumnName(i);
                    obj.put(column_name, resultSet.getObject(column_name));
                }
                json.add(obj);
            }
            response.put("Response", json);
            log.info("ResultSet in JSON : " + response);
            nlpResponseModel.setMessage(passModifiedMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("resultSet", resultSet);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in SelectQueryGetResultSet ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failModifiedMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Object conObject = connection;\n");
        sb.append("Connection connectionObject = (Connection) conObject;\n");
        sb.append("JSONObject response = new JSONObject();\n");
        sb.append("try {\n");
        sb.append("	Statement statement = connectionObject.createStatement();\n");
        sb.append("	ResultSet resultSet = statement.executeQuery(query);\n");
        sb.append("	ResultSetMetaData resultSetMetaData = resultSet.getMetaData();\n");
        sb.append("	int numberOfColumns = resultSetMetaData.getColumnCount();\n");
        sb.append("	JSONArray json = new JSONArray();\n");
        sb.append("	while (resultSet.next()) {\n");
        sb.append("		JSONObject obj = new JSONObject();\n");
        sb.append("		for (int i = 1; i <= numberOfColumns; i++) {\n");
        sb.append("			String column_name = resultSetMetaData.getColumnName(i);\n");
        sb.append("			obj.put(column_name, resultSet.getObject(column_name));\n");
        sb.append("		}\n");
        sb.append("		json.add(obj);\n");
        sb.append("	}\n");
        sb.append("	response.put(\"Response\", json);\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("connection::\"conObject\"");
        params.add("query::\"randomQuery\"");

        return params;
    }
}
