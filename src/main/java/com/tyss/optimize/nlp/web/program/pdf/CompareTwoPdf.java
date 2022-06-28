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
@Component(value = "CompareTwoPdf")
public class CompareTwoPdf implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, passModifiedMessage = null, failModifiedMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath1 = (String) attributes.get("filePath1");
            String filePath2 = (String) attributes.get("filePath2");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("PDF verify if " + filePath1 + " data matches " + filePath2 + " data");
            passModifiedMessage = passMessage.replace("*filePath1*", filePath1)
                    .replace("*filePath2*", filePath2);
            failModifiedMessage = failMessage.replace("*filePath1*", filePath1)
                    .replace("*filePath2*", filePath2);
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream obj1 = storageManager.getObject(storageInfo, filePath1);
            PDDocument objDoc1 = PDDocument.load(obj1);
            PDFTextStripper pdfStripper1 = new PDFTextStripper();
            String pdfContent1 = pdfStripper1.getText(objDoc1);
            InputStream obj2 = storageManager.getObject(storageInfo, filePath2);
            PDDocument objDoc2 = PDDocument.load(obj2);
            PDFTextStripper pdfStripper2 = new PDFTextStripper();
            String pdfContent2 = pdfStripper2.getText(objDoc2);
            if (pdfContent1.equals(pdfContent2)) {
                log.info("PDF " + filePath1 + " matches " + filePath2);
                nlpResponseModel.setMessage(passModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(filePath1 + " did not match " + filePath2);
                nlpResponseModel.setMessage(failModifiedMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CompareTwoPdf ", exception);
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
        sb.append(" FileInputStream obj1 = new FileInputStream(filePath1);\n");
        sb.append(" FileInputStream obj2 = new FileInputStream(filePath2);\n");
        sb.append(" PDDocument objDoc1 = PDDocument.load(obj1);\n");
        sb.append(" PDFTextStripper pdfStripper1 = new PDFTextStripper();\n");
        sb.append(" String pdfContent1 = pdfStripper1.getText(objDoc1);\n");
        sb.append(" PDDocument objDoc2 = PDDocument.load(obj2);\n");
        sb.append(" PDFTextStripper pdfStripper2 = new PDFTextStripper();\n");
        sb.append(" String pdfContent2 = pdfStripper2.getText(objDoc2);\n");
        sb.append(" if (pdfContent1.equals(pdfContent2)) {\n");
        sb.append(" 	System.out.println(\"PDF \" + filePath1 + \" matches \" + filePath2);\n");
        sb.append(" } else {\n");
        sb.append(" 	System.out.println(filePath1 + \" did not match \" + filePath2);\n");
        sb.append(" }\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath1::\"/home/tyss/File1.pdf\"");
        params.add("filePath2::\"/home/tyss/File2.pdf\"");

        return params;
    }
}
