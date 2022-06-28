package com.tyss.optimize.nlp.web.program.db.jdbc;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetJDBCConnectivity")
public class GetJDBCConnectivity implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        Connection connection = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String dbURL = (String) attributes.get("dbURL");
            String username = (String) attributes.get("username");
            String password = (String) attributes.get("password");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.debug("Deleting JDBC record");
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(dbURL, username, password);
            if (!connection.isValid(10)) {
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else {
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
            nlpResponseModel.getAttributes().put("connection", connection);
        } catch (Exception e) {
            log.error("NLP_EXCEPTION in GetJDBCConnectivity ", e);
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

        sb.append("Connection connection = null;\n");
        sb.append("try {\n");
        sb.append("	Class.forName(\"com.mysql.jdbc.Driver\");\n");
        sb.append("	connection = DriverManager.getConnection(dbURL, username, password);\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("dbURL::\"jdbc:mysql://localhost:3306/mysqlDB\"");
        params.add("username::\"root\"");
        params.add("password::\"root\"");

        return params;
    }
}
