package com.tyss.optimize.nlp.web.program.pdf;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.StorageInfo;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.util.storage.StorageConfigFactoryBuilder;
import com.tyss.optimize.nlp.util.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyPDF")
public class VerifyPDF implements Nlp {

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
            String data = (String) attributes.get("data");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("PDF verify if file contains " + data);
            passModifiedMessage = passMessage.replace("*data*", data);
            failModifiedMessage = failMessage.replace("*data*", data);
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream obj = storageManager.getObject(storageInfo, filePath);

            PDDocument objDoc = PDDocument.load(obj);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String pdfContent = pdfStripper.getText(objDoc);
            if (pdfContent.contains(data)) {
                log.info("PDF file contains " + data);
                nlpResponseModel.setMessage(passModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("PDF file does not contain " + data);
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyPDF ", exception);
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
        sb.append(" FileInputStream obj = new FileInputStream(filePath);\n");
        sb.append(" PDDocument objDoc = PDDocument.load(obj);\n");
        sb.append(" PDFTextStripper pdfStripper = new PDFTextStripper();\n");
        sb.append(" String pdfContent = pdfStripper.getText(objDoc);\n");
        sb.append(" if (pdfContent.contains(data)) {\n");
        sb.append(" 	System.out.println(\"PDF file contains \" + data);\n");
        sb.append(" } else {\n");
        sb.append(" 	System.out.println(\"PDF file does not contain \" + data);\n");
        sb.append(" }\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.pdf\"");
        params.add("data::\"xyz\"");

        return params;
    }
}
