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
@Component(value = "GetAllKeys")
public class GetAllKeys implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String[] values = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            failModifiedMessage = failMessage.replace("*filePath*", filePath);
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream ip = storageManager.getObject(storageInfo, filePath);

            Properties prop = new Properties();
            prop.load(ip);
            Set<Object> keySet = prop.keySet();
            int keyCount = keySet.size();
            values = new String[keyCount];
            int i = 0;
            for (Object ob : keySet) {
                String key = (String) ob;
                values[i] = key;
                i++;
            }
            passModifiedMessage = passMessage.replace("*filePath*", filePath)
                    .replace("*returnValue*", String.valueOf(values));
            nlpResponseModel.setMessage(passModifiedMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("values", values);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetAllKeys ", exception);
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

        sb.append("String[] values = null;\n");
        sb.append("try {\n");
        sb.append(" FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append(" Properties prop = new Properties();\n");
        sb.append(" prop.load(ip);\n");
        sb.append(" Set<Object> keySet = prop.keySet();\n");
        sb.append(" int keyCount = keySet.size();\n");
        sb.append(" values = new String[keyCount];\n");
        sb.append(" int i = 0;\n");
        sb.append(" for (Object ob : keySet) {\n");
        sb.append(" 	String key = (String) ob;\n");
        sb.append(" 	values[i] = key;\n");
        sb.append(" 	i++;\n");
        sb.append(" }\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.properties\"");

        return params;
    }
}
