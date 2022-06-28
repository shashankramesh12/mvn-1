package com.tyss.optimize.nlp.web.program.mysql;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyIfDataPresentInRefColumn")
public class VerifyIfDataPresentInRefColumn implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        boolean flag = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            Connection connection = (Connection) attributes.get("connection");
            String query = (String) attributes.get("query");
            String expectedData = (String) attributes.get("expectedData");
            String columnName = (String) attributes.get("columnName");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            passModifiedMessage = passMessage.replace("*expectedData*", expectedData)
                    .replace("*columnName*", columnName);
            failModifiedMessage = failMessage.replace("*expectedData*", expectedData)
                    .replace("*columnName*", columnName);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            Integer columnCount = resultSetMetaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = resultSetMetaData.getColumnName(i + 1);
            }
            int refColumnIndex = 0;
            for (int i = 0; i < columnCount; i++) {
                if (columnNames[i].equals(columnName)) {
                    refColumnIndex = i + 1;
                }
            }
            while (resultSet.next()) {
                if (resultSet.getString(refColumnIndex).equals(expectedData)) {
                    flag = true;
                } else {
                    continue;
                }
            }
            if (flag == true) {
                log.info(expectedData + " is present in column " + columnName);
                nlpResponseModel.setMessage(passModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(expectedData + " is not present in column " + columnName);
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfDataPresentInRefColumn ", exception);
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
        sb.append("boolean flag = false;\n");
        sb.append("try {\n");
        sb.append("	Statement statement = connectionObject.createStatement();\n");
        sb.append("	ResultSet resultSet = statement.executeQuery(query);\n");
        sb.append("	ResultSetMetaData resultSetMetaData = resultSet.getMetaData();\n");
        sb.append("	Integer columnCount = resultSetMetaData.getColumnCount();\n");
        sb.append("	String[] columnNames = new String[columnCount];\n");
        sb.append("	for (int i = 0; i < columnCount; i++) {\n");
        sb.append("		columnNames[i] = resultSetMetaData.getColumnName(i + 1);\n");
        sb.append("	}\n");
        sb.append("	int refColumnIndex = 0;\n");
        sb.append("	for (int i = 0; i < columnCount; i++) {\n");
        sb.append("		if (columnNames[i].equals(columnName)) {\n");
        sb.append("			refColumnIndex = i + 1;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	while (resultSet.next()) {\n");
        sb.append("		if (resultSet.getString(refColumnIndex).equals(expectedData)) {\n");
        sb.append("			flag = true;\n");
        sb.append("		} else {\n");
        sb.append("			continue;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("}catch (Exception exception){}\n");
        sb.append("if (flag == true) {\n");
        sb.append("	System.out.println(expectedData + \" is present in column \" + columnName);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(expectedData + \" is not present in column \" + columnName);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("connection::\"conObject\"");
        params.add("query::\"randomQuery\"");
        params.add("expectedData::\"xyz\"");
        params.add("columnName::\"abc\"");

        return params;
    }
}
