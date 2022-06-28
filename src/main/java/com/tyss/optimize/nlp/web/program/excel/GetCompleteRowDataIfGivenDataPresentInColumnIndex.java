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
@Component(value = "GetCompleteRowDataIfGivenDataPresentInColumnIndex")
public class GetCompleteRowDataIfGivenDataPresentInColumnIndex implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String[] rowData = null;
        String data = null;
        Integer columnIndex = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            String sheetName = (String) attributes.get("sheetName");
            data = (String) attributes.get("data");
            columnIndex = (Integer) attributes.get("columnIndex");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
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
            rowData = new String[columnCount];
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
            log.info("Row data where data " + data + " is found in column " + columnIndex + " is " + rowData);
            nlpResponseModel.setMessage(passMessage.replace("*data*", data).replace("*columnIndex*", columnIndex.toString()).replace("*returnValue*", rowData.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("rowData", rowData);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetCompleteRowDataIfGivenDataPresentInColumnIndex ", exception);
            failMessage = failMessage.replace("*data*", data).replace("*columnIndex*", columnIndex.toString());
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
        sb.append("	String[] rowData = null;\n");
        sb.append("	FileInputStream ip = new FileInputStream(filePath);\n");
        sb.append("	Workbook wb = WorkbookFactory.create(ip);\n");
        sb.append("	Sheet sh = wb.getSheet(sheetName);\n");
        sb.append("	int rowCount = sh.getPhysicalNumberOfRows();\n");
        sb.append("	int columnCount = sh.getRow(0).getPhysicalNumberOfCells();\n");
        sb.append("	rowData = new String[columnCount];\n");
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
        params.add("columnIndex::4");

        return params;
    }
}
