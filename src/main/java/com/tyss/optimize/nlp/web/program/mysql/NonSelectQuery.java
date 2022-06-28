package com.tyss.optimize.nlp.web.program.mysql;

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
@Component(value = "NonSelectQuery")
public class NonSelectQuery implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Integer numberOfRowsAffected = 0;
        String result = null;
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
            numberOfRowsAffected = statement.executeUpdate(query);
            result = "Number of affected rows :" + numberOfRowsAffected;
            nlpResponseModel.setMessage(passModifiedMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("result", result);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in NonSelectQuery ", exception);
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
        sb.append("try {\n");
        sb.append(" Statement statement = connectionObject.createStatement();\n");
        sb.append(" Integer numberOfRowsAffected = statement.executeUpdate(query);\n");
        sb.append(" String result = \"Number of affected rows :\" + numberOfRowsAffected;\n");
        sb.append("}catch (SQLException exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("connection::\"conObject\"");
        params.add("query::\"randomQuery\"");

        return params;
    }
}
