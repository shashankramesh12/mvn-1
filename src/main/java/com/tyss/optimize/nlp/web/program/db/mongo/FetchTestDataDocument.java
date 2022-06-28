package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.ant.PropertyHelper;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component(value = "FetchTestDataDocument")
public class FetchTestDataDocument implements Nlp {

    private static MongoClient singletonMongo;
    private static final String springProfileActive = "spring.profiles.active";

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        GridFSDBFile imageForOutput = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String projectName = (String) attributes.get("projectName");
            String testDataName = (String) attributes.get("testDataName");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            DB db = getDBObject();
            GridFS gfsPhoto = new GridFS(db, projectName);
            DBCursor cursor = gfsPhoto.getFileList();
            while (cursor.hasNext()) {
            }
            imageForOutput = gfsPhoto.findOne(testDataName);
            InputStream inputStream = imageForOutput.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            log.debug("Fetched TestData:" + result.toString(StandardCharsets.UTF_8.name()));
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("imageForOutput", imageForOutput);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in FetchTestDataDocument ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    private DB getDBObject() {
        Properties prop = new Properties();
        String fileName = "application.properties";
        String profile = null;
        if (null != System.getenv(springProfileActive)) {
            profile = System.getenv(springProfileActive);
        } else if (null != System.getProperty(springProfileActive)) {
            profile = System.getProperty(springProfileActive);
        }
        if (profile != null) {
            fileName = "application-" + profile + ".properties";
        }
        try (InputStream input = PropertyHelper.class.getClassLoader().getResourceAsStream(fileName)) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException e) {
            log.error(e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
        String dbName = prop.getProperty("spring.data.mongodb.database");
        MongoCredential credential = MongoCredential.createCredential(prop.getProperty("spring.data.mongodb.username"),
                dbName, prop.getProperty("spring.data.mongodb.password").toCharArray());
        if (singletonMongo == null) {
            String hostname = prop.getProperty("spring.data.mongodb.host");
            singletonMongo = new MongoClient(new ServerAddress(hostname), Arrays.asList(credential));
        }
        DB db = singletonMongo.getDB(dbName);
        return db;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("GridFSDBFile imageForOutput = null;\n");
        sb.append("Properties prop = new Properties();\n");
        sb.append("try {\n");
        sb.append("    prop.load(new FileInputStream(\"config.properties\"));\n");
        sb.append("} catch (IOException e) {\n");
        sb.append("System.out.println(\"IOException: Can't load properties file\");\n");
        sb.append("}\n");
        sb.append("try {\n");
        sb.append("	String dbName = prop.getProperty(\"spring.data.mongodb.database\");\n");
        sb.append("	MongoCredential credential = MongoCredential.createCredential(prop.getProperty(\"spring.data.mongodb.username\"),\n");
        sb.append("			dbName, prop.getProperty(\"spring.data.mongodb.password\").toCharArray());\n");
        sb.append("	String hostname = prop.getProperty(\"spring.data.mongodb.host\");\n");
        sb.append("	MongoClient mongoClient = new MongoClient(new ServerAddress(hostname), Arrays.asList(credential));\n");
        sb.append("	DB db = mongoClient.getDB(dbName);\n");
        sb.append("	GridFS gfsPhoto = new GridFS(db, projectName);\n");
        sb.append("	DBCursor cursor = gfsPhoto.getFileList();\n");
        sb.append("	while (cursor.hasNext()) {\n");
        sb.append("	}\n");
        sb.append("	imageForOutput = gfsPhoto.findOne(testDataName);\n");
        sb.append("	InputStream inputStream = imageForOutput.getInputStream();\n");
        sb.append("	ByteArrayOutputStream result = new ByteArrayOutputStream();\n");
        sb.append("	byte[] buffer = new byte[1024];\n");
        sb.append("	int length;\n");
        sb.append("	while ((length = inputStream.read(buffer)) != -1) {\n");
        sb.append("		result.write(buffer, 0, length);\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("projectName::\"xyz\"");
        params.add("testDataName::\"abc\"");

        return params;
    }
}
