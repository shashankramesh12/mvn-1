package com.tyss.optimize.nlp.web.program.db.jdbc;

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
@Component(value = "GetJDBCRecord")
public class GetJDBCRecord implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        JSONObject response = new JSONObject();
        JSONArray json = new JSONArray();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            Connection conn = (Connection) attributes.get("conn");
            String operation = (String) attributes.get("operation");
            String findColumns = (String) attributes.get("findColumns");
            String tableName = (String) attributes.get("tableName");
            String query = (String) attributes.get("query");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Fetching record from JDBC database");
            String sql = operation + " " + findColumns + " FROM " + tableName + " " + query;
            log.debug("Statement:" + sql);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = rsmd.getColumnName(i);
                    obj.put(column_name, rs.getObject(column_name));
                }
                json.add(obj);
            }
            response.put("Response", json);
            nlpResponseModel.getAttributes().put("response", response);
            conn.close();
            log.info("Successfully fetched record from JDBC database");
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception e) {
            log.error("NLP_EXCEPTION in GetJDBCRecord ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Object connObject = conn;\n");
        sb.append("Connection connectionObject = (Connection) connObject;\n");
        sb.append("JSONObject response = new JSONObject();\n");
        sb.append("JSONArray json = new JSONArray();\n");
        sb.append("try {\n");
        sb.append("	String sql = operation + \" \" + findColumns + \" FROM \" + tableName + \" \" + query;\n");
        sb.append("	Statement stmt = connectionObject.createStatement();\n");
        sb.append("	ResultSet rs = stmt.executeQuery(sql);\n");
        sb.append("	ResultSetMetaData rsmd = rs.getMetaData();\n");
        sb.append("	while (rs.next()) {\n");
        sb.append("		int numColumns = rsmd.getColumnCount();\n");
        sb.append("		JSONObject obj = new JSONObject();\n");
        sb.append("		for (int i = 1; i <= numColumns; i++) {\n");
        sb.append("			String column_name = rsmd.getColumnName(i);\n");
        sb.append("			obj.put(column_name, rs.getObject(column_name));\n");
        sb.append("		}\n");
        sb.append("		json.add(obj);\n");
        sb.append("	}\n");
        sb.append("	response.put(\"Response\", json);\n");
        sb.append("	connectionObject.close();\n");
        sb.append("} catch (Exception e) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("conn::\"conObject\"");
        params.add("operation::\"xyz\"");
        params.add("tableName::\"abc\"");
        params.add("findColumns::\"xyz,abc\"");
        params.add("query::\"randomQuery\"");

        return params;
    }
}
