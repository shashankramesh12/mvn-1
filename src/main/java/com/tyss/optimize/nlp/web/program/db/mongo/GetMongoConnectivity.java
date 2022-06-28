package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoIterable;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetMongoConnectivity")
public class GetMongoConnectivity implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        MongoClient mongoClient = null;
        DB db = null;
        boolean isDBExists = false;
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
            log.info("Getting Mongo database connection");
            List<String> dbURLList = Arrays.asList(dbURL.split("/"));
            String dbName = dbURLList.get(1);
            String dns = dbURLList.get(0);
            List<String> dnsList = Arrays.asList(dns.split(":"));

            if (username == null || password == null) {
                mongoClient = new MongoClient(dnsList.get(0), Integer.parseInt(dnsList.get(1)));
            } else {
                MongoCredential credential = MongoCredential.createCredential(username, dbName, password.toCharArray());
                mongoClient = new MongoClient(new ServerAddress(dnsList.get(0), Integer.parseInt(dnsList.get(1))),
                        Arrays.asList(credential));
            }
            MongoIterable<String> listDatabaseNames = mongoClient.listDatabaseNames();
            for (String databaseName : listDatabaseNames) {
                if (databaseName != null && databaseName.equalsIgnoreCase(dbName)) {
                    isDBExists = true;
                    break;
                }
            }
            if (isDBExists) {
                db = mongoClient.getDB(dbName);
                log.info("Successfully connected Mongo database");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
            nlpResponseModel.getAttributes().put("db", db);
        } catch (Exception exception) {
            log.error("NullPointerException in GetMongoConnectivity ", exception);
            String exceptionSimpleName=exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("DB db = null;\n");
        sb.append("boolean isDBExists = false;\n");
        sb.append("MongoClient mongoClient = null;\n");
        sb.append("try {\n");
        sb.append("List<String> dbURLList = Arrays.asList(dbURL.split(\"/\"));\n");
        sb.append("String dbName = dbURLList.get(1);\n");
        sb.append("String dns = dbURLList.get(0);\n");
        sb.append("List<String> dnsList = Arrays.asList(dns.split(\":\"));\n");
        sb.append("if (username == null || password == null) {\n");
        sb.append("	mongoClient = new MongoClient(dnsList.get(0), Integer.parseInt(dnsList.get(1)));\n");
        sb.append("} else {\n");
        sb.append("	MongoCredential credential = MongoCredential.createCredential(username, dbName, password.toCharArray());\n");
        sb.append("	mongoClient = new MongoClient(new ServerAddress(dnsList.get(0), Integer.parseInt(dnsList.get(1))),\n");
        sb.append("			Arrays.asList(credential));\n");
        sb.append("}\n");
        sb.append("MongoIterable<String> listDatabaseNames = mongoClient.listDatabaseNames();\n");
        sb.append("for (String databaseName : listDatabaseNames) {\n");
        sb.append("	if (databaseName != null && databaseName.equalsIgnoreCase(dbName)) {\n");
        sb.append("		isDBExists = true;\n");
        sb.append("		break;\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (isDBExists) {\n");
        sb.append("	db = mongoClient.getDB(dbName);\n");
        sb.append("	System.out.println(\"Successfully connected Mongo database\");\n");
        sb.append("}\n");
        sb.append("} catch (Exception exception) {}\n");

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
