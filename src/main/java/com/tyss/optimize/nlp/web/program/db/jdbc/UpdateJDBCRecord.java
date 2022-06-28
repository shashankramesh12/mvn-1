package com.tyss.optimize.nlp.web.program.db.jdbc;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "UpdateJDBCRecord")
public class UpdateJDBCRecord implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Statement stmt = null;
        Connection conn = null;
        boolean flag = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            conn = (Connection) attributes.get("conn");
            String operation = (String) attributes.get("operation");
            String setValues = (String) attributes.get("setValues");
            String tableName = (String) attributes.get("tableName");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Inserting record into JDBC database");
            stmt = conn.createStatement();
            String sql = operation;
            sql += " " + tableName;
            sql += " " + setValues;
            stmt.executeUpdate(sql);
            log.info("Successfully updated JDBC record");
            conn.close();
            flag = true;
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception e) {
            log.error("NLP_EXCEPTION in UpdateJDBCRecord ", e);
            flag = false;
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        } finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (Exception se) {
                log.error("Failed to update JDBC record");
                flag = false;
                String exceptionSimpleName = se.getClass().getSimpleName();
                if (containsChildNlp)
                    throw new NlpException(exceptionSimpleName);
                nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, se.getStackTrace());
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception se) {
                log.error("Failed to update JDBC record");
                flag = false;
                String exceptionSimpleName = se.getClass().getSimpleName();
                if (containsChildNlp)
                    throw new NlpException(exceptionSimpleName);
                nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, se.getStackTrace());
            }
        }
        nlpResponseModel.getAttributes().put("flag", flag);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Object connObject = conn;\n");
        sb.append("Connection connectionObject = (Connection) connObject;\n");
        sb.append("Statement stmt = null;\n");
        sb.append("boolean flag = false;\n");
        sb.append("try {\n");
        sb.append("	stmt = connectionObject.createStatement();\n");
        sb.append("	String sql = operation;\n");
        sb.append("	sql += \" \" + tableName;\n");
        sb.append("	sql += \" \" + setValues;\n");
        sb.append("	stmt.executeUpdate(sql);\n");
        sb.append("	System.out.println(\"Successfully updated JDBC record\");\n");
        sb.append("	connectionObject.close();\n");
        sb.append("	flag = true;\n");
        sb.append("} catch (Exception e) {\n");
        sb.append("	flag = false;\n");
        sb.append("} finally {\n");
        sb.append("	try {\n");
        sb.append("		if (stmt != null)\n");
        sb.append("			connectionObject.close();\n");
        sb.append("	} catch (Exception se) {\n");
        sb.append("		System.out.println(\"Failed to update JDBC record\");\n");
        sb.append("		flag = false;\n");
        sb.append("	}\n");
        sb.append("	try {\n");
        sb.append("		if (connectionObject != null) {\n");
        sb.append("			connectionObject.close();\n");
        sb.append("		}\n");
        sb.append("	} catch (Exception se) {\n");
        sb.append("		System.out.println(\"Failed to update JDBC record\");\n");
        sb.append("		flag = false;\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("conn::\"conObject\"");
        params.add("operation::\"xyz\"");
        params.add("tableName::\"abc\"");
        params.add("setValues::\"randomValues\"");

        return params;
    }
}
