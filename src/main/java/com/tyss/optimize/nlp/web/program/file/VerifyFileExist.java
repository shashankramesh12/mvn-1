package com.tyss.optimize.nlp.web.program.file;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyFileExist")
public class VerifyFileExist implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String folderPath = (String) attributes.get("folderPath");
            String fileName = (String) attributes.get("fileName");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            passModifiedMessage = passMessage.replace("*fileName*", fileName).replace("*folderPath*", folderPath);
            failModifiedMessage = failMessage.replace("*fileName*", fileName).replace("*folderPath*", folderPath);
            log.info("Verify if " + fileName + " exists in folder " + folderPath);
            boolean isExist = new File(folderPath + "/" + fileName).isFile();
            if (isExist == true) {
                log.info("File " + fileName + " exists in folder " + folderPath);
                nlpResponseModel.setMessage(passModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("File " + fileName + " does not exists in folder " + folderPath);
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyFileExist ", exception);
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

        sb.append("try {\n");
        sb.append(" boolean isExist = new File(folderPath + \"/\" + fileName).isFile();\n");
        sb.append(" if (isExist == true) {\n");
        sb.append(" 	System.out.println(\"File \" + fileName + \" exists in folder \" + folderPath);\n");
        sb.append(" } else {\n");
        sb.append(" 	System.out.println(\"File \" + fileName + \" does not exists in folder \" + folderPath);\n");
        sb.append(" }\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("folderPath::\"/home/tyss/\"");
        params.add("fileName::\"script.txt\"");

        return params;
    }
}
