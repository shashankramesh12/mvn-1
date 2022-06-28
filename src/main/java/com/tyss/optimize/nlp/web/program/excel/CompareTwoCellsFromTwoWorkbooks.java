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
@Component(value = "CompareTwoCellsFromTwoWorkbooks")
public class CompareTwoCellsFromTwoWorkbooks implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        String filePath1 = (String) attributes.get("filePath1");
        String filePath2 = (String) attributes.get("filePath2");
        String sheetName1 = (String) attributes.get("sheetName1");
        String sheetName2 = (String) attributes.get("sheetName2");
        Integer rowIndex1 = (Integer) attributes.get("rowIndex1");
        Integer columnIndex1 = (Integer) attributes.get("columnIndex1");
        Integer rowIndex2 = (Integer) attributes.get("rowIndex2");
        Integer columnIndex2 = (Integer) attributes.get("columnIndex2");
        containsChildNlp = (Boolean) attributes.get("containsChildNlp");
         passMessage = (String) nlpRequestModel.getPassMessage();
         failMessage = (String) nlpRequestModel.getFailMessage();
        StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
        if (attributes.get("ifCheckPointIsFailed") != null) {
            String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
            ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
        }
        try {
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream ip1 = storageManager.getObject(storageInfo, filePath1);
            InputStream ip2 = storageManager.getObject(storageInfo, filePath2);

            Workbook wb1 = WorkbookFactory.create(ip1);
            Workbook wb2 = WorkbookFactory.create(ip2);
            Sheet sh1 = wb1.getSheet(sheetName1);
            Sheet sh2 = wb2.getSheet(sheetName2);
            Cell cell1 = sh1.getRow(rowIndex1).getCell(columnIndex1, MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell cell2 = sh2.getRow(rowIndex2).getCell(columnIndex2, MissingCellPolicy.CREATE_NULL_AS_BLANK);
            String cellValue1 = cell1.toString();
            String cellValue2 = cell2.toString();
            if (cellValue1.equals(cellValue2)) {
                log.info("The Cell data of given two cells match successfully");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The Cell data of given two cells do not match");
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CompareTwoCellsFromTwoWorkbooks ", exception);
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
        sb.append("	FileInputStream ip1 = new FileInputStream(filePath1);\n");
        sb.append("	FileInputStream ip2 = new FileInputStream(filePath2);\n");
        sb.append("	Workbook wb1 = WorkbookFactory.create(ip1);\n");
        sb.append("	Workbook wb2 = WorkbookFactory.create(ip2);\n");
        sb.append("	Sheet sh1 = wb1.getSheet(sheetName1);\n");
        sb.append("	Sheet sh2 = wb2.getSheet(sheetName2);\n");
        sb.append("	Cell cell1 = sh1.getRow(rowIndex1).getCell(columnIndex1, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("	Cell cell2 = sh2.getRow(rowIndex2).getCell(columnIndex2, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("	String cellValue1 = cell1.toString();\n");
        sb.append("	String cellValue2 = cell2.toString();\n");
        sb.append("	if (cellValue1.equals(cellValue2)) {\n");
        sb.append("	   System.out.println(\"The Cell data of given two cells match successfully\");\n");
        sb.append("	} else {\n");
        sb.append("	   System.out.println(\"The Cell data of given two cells do not match\");\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("filePath1::\"/home/tyss/File1.xlsx\"");
        params.add("filePath2::\"/home/tyss/File2.xlsx\"");
        params.add("sheetName1::\"xyz\"");
        params.add("sheetName2::\"abc\"");
        params.add("rowIndex1::1");
        params.add("columnIndex1::2");
        params.add("rowIndex2::3");
        params.add("columnIndex2::4");

        return params;
    }
}
