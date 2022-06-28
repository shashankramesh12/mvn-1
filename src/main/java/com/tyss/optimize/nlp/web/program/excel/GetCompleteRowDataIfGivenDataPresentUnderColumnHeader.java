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
@Component(value = "GetCompleteRowDataIfGivenDataPresentUnderColumnHeader")
public class GetCompleteRowDataIfGivenDataPresentUnderColumnHeader implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String[] rowData = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            String sheetName = (String) attributes.get("sheetName");
            String data = (String) attributes.get("data");
            String columnHeader = (String) attributes.get("columnHeader");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            modifiedFailMessage = failMessage.replace("*data*", data).replace("*columnHeader*", columnHeader);
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
            String columnValue = "";
            Integer columnIndex = 0;
            rowData = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                Cell columnCell = sh.getRow(0).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                columnValue = columnCell.toString();
                if (columnValue.equals(columnHeader)) {
                    columnIndex = i;
                    break;
                } else {
                    log.error("Failed to fetch row data with data " + data + " found in column " + columnHeader);
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            }
            for (int i = 1; i < rowCount; i++) {
                Cell cellData = sh.getRow(i).getCell(columnIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                String cellValue = cellData.toString();
                if (cellValue.equals(data)) {
                    for (int j = 0; j < columnCount; j++) {
                        Cell rowValue = sh.getRow(i).getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowData[j] = rowValue.toString();
                    }
                }
            }
            log.info("Row data where data " + data + " is found in column " + columnHeader + " is " + rowData);
            nlpResponseModel.setMessage(passMessage.replace("*data*", data).replace("*columnHeader*", columnHeader.toString()).replace("*returnValue*", rowData.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("rowData", rowData);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetCompleteRowDataIfGivenDataPresentUnderColumnHeader ", exception);
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
        sb.append("	String[] rowData = null;\n");
        sb.append("	FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append("	Workbook wb = WorkbookFactory.create(ip);\n");
        sb.append("	Sheet sh = wb.getSheet(sheetName);\n");
        sb.append("	int rowCount = sh.getPhysicalNumberOfRows();\n");
        sb.append("	int columnCount = sh.getRow(0).getPhysicalNumberOfCells();\n");
        sb.append("	String columnValue = \"\";\n");
        sb.append("	Integer columnIndex = 0;\n");
        sb.append("	rowData = new String[columnCount];\n");
        sb.append("	for (int i = 0; i < columnCount; i++) {\n");
        sb.append("		Cell columnCell = sh.getRow(0).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("		columnValue = columnCell.toString();\n");
        sb.append("		if (columnValue.equals(columnHeader)) {\n");
        sb.append("			columnIndex = i;\n");
        sb.append("			break;\n");
        sb.append("		} else {\n");
        sb.append("			System.out.println(\"Failed to fetch row Data with Data \" + data + \" found in column \" + columnHeader);\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	for (int i = 1; i < rowCount; i++) {\n");
        sb.append("		Cell cellData = sh.getRow(i).getCell(columnIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("		String cellValue = cellData.toString();\n");
        sb.append("		if (cellValue.equals(data)) {\n");
        sb.append("			for (int j = 0; j < columnCount; j++) {\n");
        sb.append("				Cell rowValue = sh.getRow(i).getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("				rowData[j] = rowValue.toString();\n");
        sb.append("			}\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.xlsx\"");
        params.add("sheetName::\"xyz\"");
        params.add("data::\"abc\"");
        params.add("columnHeader::\"xyz\"");

        return params;
    }
}
