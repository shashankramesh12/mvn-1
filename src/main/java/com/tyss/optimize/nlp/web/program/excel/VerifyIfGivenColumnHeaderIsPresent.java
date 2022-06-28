package com.tyss.optimize.nlp.web.program.excel;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.StorageInfo;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.util.storage.StorageConfigFactoryBuilder;
import com.tyss.optimize.nlp.util.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyIfGivenColumnHeaderIsPresent")
public class VerifyIfGivenColumnHeaderIsPresent implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        String filePath = (String) attributes.get("filePath");
        String sheetName = (String) attributes.get("sheetName");
        String columnHeader = (String) attributes.get("columnHeader");
        containsChildNlp = (Boolean) attributes.get("containsChildNlp");
        passMessage = (String) nlpRequestModel.getPassMessage();
        failMessage = (String) nlpRequestModel.getFailMessage();
        modifiedFailMessage = failMessage.replace("*columnHeader*", columnHeader).replace("*sheetName*", sheetName).replace("*filePath*", filePath);
        StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
        if (attributes.get("ifCheckPointIsFailed") != null) {
            String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
            ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
        }
        try {
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream ip = storageManager.getObject(storageInfo, filePath);

            Workbook wb = WorkbookFactory.create(ip);
            Sheet sh = wb.getSheet(sheetName);
            Row row = sh.getRow(0);
            int columnCount = row.getPhysicalNumberOfCells();
            String columnValue = "";
            Boolean flag = false;
            for (int i = 0; i < columnCount; i++) {
                Cell columnCell = sh.getRow(0).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                columnValue = columnCell.toString();

                if (columnValue.equals(columnHeader)) {
                    flag = true;
                    break;
                }
            }
            if (flag == true) {
                log.info("Successfully verified column " + columnHeader + " is present in sheet " + sheetName + " and workbook path " + filePath);
                nlpResponseModel.setMessage(passMessage.replace("*columnHeader*", columnHeader).replace("*sheetName*", sheetName).replace("*filePath*", filePath));
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Failed to verify if given column " + columnHeader + " is present in sheet " + sheetName + " and workbook file path " + filePath);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfGivenColumnHeaderIsPresent ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("try {\n");
        sb.append("	FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append("	Workbook wb = WorkbookFactory.create(ip);\n");
        sb.append("	Sheet sh = wb.getSheet(sheetName);\n");
        sb.append("	Row row = sh.getRow(0);\n");
        sb.append("	int columnCount = row.getPhysicalNumberOfCells();\n");
        sb.append("	String columnValue = \"\";\n");
        sb.append("	boolean flag = false;\n");
        sb.append("	for (int i = 0; i < columnCount; i++) {\n");
        sb.append("		Cell columnCell = sh.getRow(0).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("		columnValue = columnCell.toString();\n");
        sb.append("		if (columnValue.equals(columnHeader)) {\n");
        sb.append("			flag = true;\n");
        sb.append("			break;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (flag == true) {\n");
        sb.append("		System.out.println(\"Successfully verified column \" + columnHeader + \" is present in sheet \" + sheetName + \" and workbook path \" + filePath);\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"Failed to verify if given column \" + columnHeader + \" is present in sheet \" + sheetName + \" and workbook file path \" + filePath);\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.xlsx\"");
        params.add("sheetName::\"xyz\"");
        params.add("columnHeader::\"abc\"");

        return params;
    }
}
