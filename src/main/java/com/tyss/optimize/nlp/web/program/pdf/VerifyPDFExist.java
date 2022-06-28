package com.tyss.optimize.nlp.web.program.pdf;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.StorageInfo;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.util.storage.StorageConfigFactoryBuilder;
import com.tyss.optimize.nlp.util.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyPDFExist")
public class VerifyPDFExist implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        String folderPath = (String) attributes.get("folderPath");
        String pdfName = (String) attributes.get("pdfName");
        containsChildNlp = (Boolean) attributes.get("containsChildNlp");
        passMessage = nlpRequestModel.getPassMessage();
        failMessage = nlpRequestModel.getFailMessage();
        StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
        if (attributes.get("ifCheckPointIsFailed") != null) {
            String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
            ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
        }
        log.info("PDF " + pdfName + " exist");
        passModifiedMessage = passMessage.replace("*pdfName*", pdfName)
                .replace("*folderPath*", folderPath);
        failModifiedMessage = failMessage.replace("*pdfName*", pdfName);

        try {
            String filePath = folderPath + "/" + pdfName + ".pdf";
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            boolean isExist = storageManager.checkIfObjectExist(storageInfo, filePath);

            if (isExist == true) {
                log.info("PDF " + pdfName + " exist");
                nlpResponseModel.setMessage(passModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(pdfName + " does not exist");
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyPDFExist ", exception);
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
        sb.append(" boolean isExist = new File(folderPath + \"/\" + pdfName + \".pdf\").isFile();\n");
        sb.append(" if (isExist == true) {\n");
        sb.append(" 	System.out.println(\"PDF \" + pdfName + \" exist\");\n");
        sb.append(" } else {\n");
        sb.append(" 	System.out.println(pdfName + \" does not exist\");\n");
        sb.append(" }\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("folderPath::\"/home/tyss\"");
        params.add("pdfName::\"File1\"");

        return params;
    }
}
