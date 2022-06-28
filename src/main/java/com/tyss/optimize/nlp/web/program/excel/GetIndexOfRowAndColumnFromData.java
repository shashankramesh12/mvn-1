package com.tyss.optimize.nlp.web.program.excel;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.StorageInfo;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.util.storage.StorageConfigFactoryBuilder;
import com.tyss.optimize.nlp.util.storage.StorageManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetIndexOfRowAndColumnFromData")
public class GetIndexOfRowAndColumnFromData implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String rowAndColumnIndex = "";
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            String sheetName = (String) attributes.get("sheetName");
            String data = (String) attributes.get("data");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            modifiedFailMessage = failMessage.replace("*data*", data);
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream ip = storageManager.getObject(storageInfo, filePath);

            Workbook wb = WorkbookFactory.create(ip);
            Sheet sh = wb.getSheet(sheetName);
            int rowCount = sh.getPhysicalNumberOfRows();
            int columnCount = sh.getRow(0).getPhysicalNumberOfCells();
            boolean flag = false;
            for (int i = 1; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    Cell cellData = sh.getRow(i).getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = cellData.toString();
                    if (cellValue.equals(data)) {
                        int rowIndex = i;
                        rowAndColumnIndex = "Row Index:" + rowIndex + " " + "Column Index:" + j;
                        flag = true;
                        break;
                    }
                }
                if (flag == true) {
                    log.info("Index of row and column with data " + data + " is " + rowAndColumnIndex);
                    nlpResponseModel.setMessage(passMessage.replace("*data*", data).replace("*returnValue*", rowAndColumnIndex));
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                }
            }
            if (flag == false) {
                rowAndColumnIndex = "Data not found";
                log.error("Failed to fetch index of row and column where data is " + data);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
            nlpResponseModel.getAttributes().put("rowAndColumnIndex", rowAndColumnIndex);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetIndexOfRowAndColumnFromData ", exception);
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
        sb.append("	String rowAndColumnIndex = \"\";\n");
        sb.append("	FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append("	Workbook wb = WorkbookFactory.create(ip);\n");
        sb.append("	Sheet sh = wb.getSheet(sheetName);\n");
        sb.append("	int rowCount = sh.getPhysicalNumberOfRows();\n");
        sb.append("	int columnCount = sh.getRow(0).getPhysicalNumberOfCells();\n");
        sb.append("	boolean flag = false;\n");
        sb.append("	for (int i = 1; i < rowCount; i++) {\n");
        sb.append("		for (int j = 0; j < columnCount; j++) {\n");
        sb.append("			Cell cellData = sh.getRow(i).getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("			String cellValue = cellData.toString();\n");
        sb.append("			if (cellValue.equals(data)) {\n");
        sb.append("				int rowIndex = i;\n");
        sb.append("				rowAndColumnIndex = \"Row Index:\" + rowIndex + \" \" + \"Column Index:\" + j;\n");
        sb.append("				flag = true;\n");
        sb.append("				break;\n");
        sb.append("			}\n");
        sb.append("		}\n");
        sb.append("		if (flag == true) {\n");
        sb.append("			System.out.println(\"Index of row and column with Data \" + data + \" is \" + rowAndColumnIndex);\n");
        sb.append("			break;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (flag == false) {\n");
        sb.append("		rowAndColumnIndex = \"Data not found\";\n");
        sb.append("		System.out.println(\"Failed to fetch index of row and column where Data is \" + data);\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.xlsx\"");
        params.add("sheetName::\"xyz\"");
        params.add("data::\"abc\"");

        return params;
    }
}
