package com.tyss.optimize.nlp.web.program.excel;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.StorageInfo;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.util.storage.StorageConfigFactoryBuilder;
import com.tyss.optimize.nlp.util.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "CreateWorkbook")
public class CreateWorkbook implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String workBookName = null, sheetName = null, folderPath = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            workBookName = (String) attributes.get("workBookName");
            sheetName = (String) attributes.get("sheetName");
            folderPath = (String) attributes.get("folderPath");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }

            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            Workbook wb = new XSSFWorkbook();
            String workBookPath = folderPath + "/" + workBookName + ".xlsx";
            wb.createSheet(sheetName);
            storageManager.saveTheUpdatedObject(storageInfo, wb, workBookPath);
            log.info("Successfully created new workbook " + workBookName + " with sheet " + sheetName + " in folder " + folderPath);
            nlpResponseModel.setMessage(passMessage.replace("*workBookName*", workBookName).replace("*sheetName*", sheetName).replace("*folderPath*", folderPath));
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CreateWorkbook ", exception);
            failMessage = failMessage.replace("*workBookName*", workBookName).replace("*sheetName*", sheetName).replace("*folderPath*", folderPath);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("try {\n");
        sb.append("	Workbook wb = new XSSFWorkbook();\n");
        sb.append("	String workBookPath = folderPath + \"/\" + workBookName + \".xlsx\";\n");
        sb.append("	OutputStream fileOut = new FileOutputStream(workBookPath);\n");
        sb.append("	wb.createSheet(sheetName);\n");
        sb.append("	wb.write(fileOut);\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("folderPath::\"/home/tyss\"");
        params.add("workBookName::\"abc\"");
        params.add("sheetName::\"xyz\"");

        return params;
    }
}
