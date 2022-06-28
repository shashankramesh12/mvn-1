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
@Component(value = "GetCompleteRowDataUsingRowIndex")
public class GetCompleteRowDataUsingRowIndex implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String[] rowData = null;
        Integer rowIndex = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String filePath = (String) attributes.get("filePath");
            String sheetName = (String) attributes.get("sheetName");
            rowIndex = (Integer) attributes.get("rowIndex");
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
            Row row = sh.getRow(rowIndex);
            int count = row.getPhysicalNumberOfCells();
            rowData = new String[count];
            for (int i = 0; i < count; i++) {
                Cell cellData = sh.getRow(rowIndex).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                rowData[i] = cellData.toString();
            }
            log.info("Row data of row " + rowIndex + " is " + rowData);
            nlpResponseModel.setMessage(passMessage.replace("*rowIndex*", rowIndex.toString()).replace("*returnValue*", rowData.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("rowData", rowData);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetCompleteRowDataUsingRowIndex ", exception);
            failMessage = failMessage.replace("*rowIndex*", rowIndex.toString());
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
        sb.append("	Row row = sh.getRow(rowIndex);\n");
        sb.append("	int count = row.getPhysicalNumberOfCells();\n");
        sb.append("	rowData = new String[count];\n");
        sb.append("	for (int i = 0; i < count; i++) {\n");
        sb.append("		Cell cellData = sh.getRow(rowIndex).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("		rowData[i] = cellData.toString();\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath::\"/home/tyss/File1.xlsx\"");
        params.add("sheetName::\"xyz\"");
        params.add("rowIndex::4");

        return params;
    }
}
