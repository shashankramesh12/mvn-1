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
@Component(value = "GetAllKeysInSortedOrder")
public class GetAllKeysInSortedOrder implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        List<String> list = new ArrayList<>();
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
            for (Object key : keySet) {
                list.add(key.toString());
            }
            Collections.sort(list);
            passModifiedMessage = passMessage.replace("*filePath*", filePath)
                    .replace("*returnValue*", String.valueOf(list));
            nlpResponseModel.setMessage(passModifiedMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("list", list);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetAllKeysInSortedOrder ", exception);
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

        sb.append("List<String> list = new ArrayList<>();\n");
        sb.append("try {\n");
        sb.append(" FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append(" Properties prop = new Properties();\n");
        sb.append(" prop.load(ip);\n");
        sb.append(" Set<Object> keySet = prop.keySet();\n");
        sb.append(" for (Object key : keySet) {\n");
        sb.append(" 	list.add(key.toString());\n");
        sb.append(" }\n");
        sb.append(" Collections.sort(list);\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.properties\"");

        return params;
    }
}
