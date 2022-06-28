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
@Component(value = "GetLastModifiedFile")
public class GetLastModifiedFile implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        File lastModifiedFile = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            failModifiedMessage = failMessage.replace("*folderPath*", filePath);
            log.info("File get the path of last modified file from " + filePath);
            File directory = new File(filePath);
            File[] listOfFiles = directory.listFiles();
            if (listOfFiles.length == 0 || listOfFiles == null) {
                log.error("The specified directory does not contain any file");
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
            lastModifiedFile = listOfFiles[0];
            for (int i = 1; i < listOfFiles.length; i++) {
                if (lastModifiedFile.lastModified() < listOfFiles[i].lastModified()) {
                    lastModifiedFile = listOfFiles[i];
                    log.info("File get the path of last modified file from " + filePath + " is " + lastModifiedFile);
                }
            }
            String passModifiedMessage = passMessage.replace("*folderPath*", filePath)
                    .replace("*returnValue*", String.valueOf(lastModifiedFile));
            nlpResponseModel.setMessage(passModifiedMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("lastModifiedFile", lastModifiedFile);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetLastModifiedFile ", exception);
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

        sb.append("File lastModifiedFile = null;\n");
        sb.append("try {\n");
        sb.append(" File directory = new File(filePath);\n");
        sb.append(" File[] listOfFiles = directory.listFiles();\n");
        sb.append(" if (listOfFiles.length == 0 || listOfFiles == null) {\n");
        sb.append(" 	System.out.println(\"The specified directory does not contain any file\");\n");
        sb.append(" }\n");
        sb.append(" lastModifiedFile = listOfFiles[0];\n");
        sb.append(" for (int i = 1; i < listOfFiles.length; i++) {\n");
        sb.append(" 	if (lastModifiedFile.lastModified() < listOfFiles[i].lastModified()) {\n");
        sb.append(" 		lastModifiedFile = listOfFiles[i];\n");
        sb.append(" 	}\n");
        sb.append(" }\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/\"");

        return params;
    }
}
