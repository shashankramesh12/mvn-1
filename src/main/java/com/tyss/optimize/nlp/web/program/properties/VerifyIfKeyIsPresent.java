package com.tyss.optimize.nlp.web.program.properties;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.StorageInfo;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.util.storage.StorageConfigFactoryBuilder;
import com.tyss.optimize.nlp.util.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Slf4j
@Component(value = "VerifyIfKeyIsPresent")
public class VerifyIfKeyIsPresent implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            String key = (String) attributes.get("key");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            passModifiedMessage = passMessage.replace("*key*", key)
                    .replace("*filePath*", filePath);
            failModifiedMessage = failMessage.replace("*key*", key)
                    .replace("*filePath*", filePath);
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream ip = storageManager.getObject(storageInfo, filePath);

            Properties prop = new Properties();
            prop.load(ip);
            Set<Object> keySet = prop.keySet();
            int keyCount = keySet.size();
            String[] values = new String[keyCount];
            int i = 0;
            Boolean flag = false;
            for (Object ob : keySet) {
                String str = (String) ob;
                values[i] = str;
                i++;
            }
            for (int j = 0; j < keyCount; j++) {
                if (values[j].equals(key)) {
                    flag = true;
                }
            }
            if (flag == true) {
                log.info("Successfully verified key " + key + " is present in file path " + filePath);
                nlpResponseModel.setMessage(passModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Failed to verify key " + key + " is present in file path " + filePath);
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfKeyIsPresent ", exception);
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

        sb.append("boolean flag = false;\n");
        sb.append("try {\n");
        sb.append("	FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append("	Properties prop = new Properties();\n");
        sb.append("	prop.load(ip);\n");
        sb.append("	Set<Object> keySet = prop.keySet();\n");
        sb.append("	int keyCount = keySet.size();\n");
        sb.append("	String[] values = new String[keyCount];\n");
        sb.append("	int i = 0;\n");
        sb.append("	for (Object ob : keySet) {\n");
        sb.append("		String str = (String) ob;\n");
        sb.append("		values[i] = str;\n");
        sb.append("		i++;\n");
        sb.append("	}\n");
        sb.append("	for (int j = 0; j < keyCount; j++) {\n");
        sb.append("		if (values[j].equals(key)) {\n");
        sb.append("			flag = true;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("}catch (Exception exception){}\n");
        sb.append("if (flag == true) {\n");
        sb.append("	System.out.println(\"Successfully verified Key \" + key + \" is present in file path \" + filePath);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Failed to verify Key \" + key + \" is present in file path \" + filePath);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.properties\"");
        params.add("key::\"xyz\"");

        return params;
    }
}
