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
@Component(value = "CompareTwoRowsFromTwoWorkbooks")
public class CompareTwoRowsFromTwoWorkbooks implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        String returnAddress = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String expectedFilePath = (String) attributes.get("expectedFilePath");
            String actualFilePath = (String) attributes.get("actualFilePath");
            String expectedSheetName = (String) attributes.get("expectedSheetName");
            String actualSheetName = (String) attributes.get("actualSheetName");
            Integer rowIndexToBeCompared = (Integer) attributes.get("rowIndexToBeCompared");
            Integer rowIndexOfColumnHeader = (Integer) attributes.get("rowIndexOfColumnHeader");
            Boolean changeColor = (Boolean) attributes.get("changeColor");
            String writeDataIfEmpty = (String) attributes.get("writeDataIfEmpty");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = (String) nlpRequestModel.getPassMessage();
            failMessage = (String) nlpRequestModel.getFailMessage();
            StorageInfo storageInfo = nlpRequestModel.getStorageInfo();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            StorageManager storageManager = StorageConfigFactoryBuilder.getStorageManager(storageInfo.getType());
            InputStream ip1 = storageManager.getObject(storageInfo, expectedFilePath);
            InputStream ip2 = storageManager.getObject(storageInfo, actualFilePath);
            Workbook wb1 = WorkbookFactory.create(ip1);
            Workbook wb2 = WorkbookFactory.create(ip2);
            Sheet sh1 = wb1.getSheet(expectedSheetName);
            Sheet sh2 = wb2.getSheet(actualSheetName);
            short firstCellNum1 = sh1.getRow(rowIndexOfColumnHeader).getFirstCellNum();
            Integer columnCount1 = sh1.getRow(rowIndexOfColumnHeader).getPhysicalNumberOfCells();
            Integer columnCount2 = sh2.getRow(rowIndexOfColumnHeader).getPhysicalNumberOfCells();
            Boolean flag = false;
            short count = firstCellNum1;
            Integer failCount = 0;
            String address[] = new String[columnCount1];
            if (columnCount1 == columnCount2) {
                for (int i = firstCellNum1; i < firstCellNum1 + columnCount1; i++) {
                    Cell cell1 = sh1.getRow(rowIndexToBeCompared).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    Cell cell2 = sh2.getRow(rowIndexToBeCompared).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue1 = cell1.toString();
                    String cellValue2 = cell2.toString();
                    if (cellValue1.equals(cellValue2)) {
                        count++;
                        if (count == columnCount1) {
                            flag = true;
                        }
                    } else {
                        if (cell2.getCellType().equals(CellType.BLANK)) {
                            sh2.getRow(rowIndexToBeCompared).createCell(i).setCellValue(writeDataIfEmpty);
                            storageManager.saveTheUpdatedObject(storageInfo, wb2, actualFilePath);
                        }
                        if (changeColor) {
                            //To add the color to unmatched cells
                            CellStyle style = wb2.createCellStyle();
                            Row row = sh2.getRow(rowIndexToBeCompared);
                            Cell cell = row.getCell(i);
                            cell.setCellStyle(style);
                            style.setFillForegroundColor(IndexedColors.RED.getIndex());
                            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            storageManager.saveTheUpdatedObject(storageInfo, wb2, actualFilePath);
                            log.info("Colour added to unmatched cells");
                        } else {
                            log.info("Colour not added to unmatched cells");
                        }
                        address[failCount] = "[" + rowIndexToBeCompared + ":" + i + "]";
                        failCount++;
                    }
                }
            }
            if (flag == true) {
                log.info("The Cell data of given two rows match successfully");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                // Return address of unmatched cells
                log.error("The Cell data of given two rows do not match");
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < failCount; i++) {
                    sb.append(address[i]);
                    if (i != failCount - 1)
                        sb.append(",");
                }
                String str = sb.toString();
                returnAddress = "Unmatched Cell Address(RowIndex:CellIndex) : " + "\n" + str;
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CompareTwoRowsFromTwoWorkbooks ", exception);
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
        sb.append("	String returnAddress = null;\n");
        sb.append("	FileInputStream ip1 = new FileInputStream(expectedFilePath);\n");
        sb.append("	FileInputStream ip2 = new FileInputStream(actualFilePath);\n");
        sb.append("	Workbook wb1 = WorkbookFactory.create(ip1);\n");
        sb.append("	Workbook wb2 = WorkbookFactory.create(ip2);\n");
        sb.append("	Sheet sh1 = wb1.getSheet(expectedSheetName);\n");
        sb.append("	Sheet sh2 = wb2.getSheet(actualSheetName);\n");
        sb.append("	short firstCellNum1 = sh1.getRow(rowIndexOfColumnHeader).getFirstCellNum();\n");
        sb.append("	Integer columnCount1 = sh1.getRow(rowIndexOfColumnHeader).getPhysicalNumberOfCells();\n");
        sb.append("	Integer columnCount2 = sh2.getRow(rowIndexOfColumnHeader).getPhysicalNumberOfCells();\n");
        sb.append("	boolean flag = false;\n");
        sb.append("	short count = firstCellNum1;\n");
        sb.append("	Integer failCount = 0;\n");
        sb.append("	String address[] = new String[columnCount1];\n");
        sb.append("	if (columnCount1 == columnCount2) {\n");
        sb.append("		for (int i = firstCellNum1; i < firstCellNum1 + columnCount1; i++) {\n");
        sb.append("			Cell cell1 = sh1.getRow(rowIndexToBeCompared).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("			Cell cell2 = sh2.getRow(rowIndexToBeCompared).getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);\n");
        sb.append("			String cellValue1 = cell1.toString();\n");
        sb.append("			String cellValue2 = cell2.toString();\n");
        sb.append("			if (cellValue1.equals(cellValue2)) {\n");
        sb.append("				count++;\n");
        sb.append("				if (count == columnCount1) {\n");
        sb.append("					flag = true;\n");
        sb.append("				}\n");
        sb.append("			} else {\n");
        sb.append("				if (cell2.getCellType().equals(CellType.BLANK)) {\n");
        sb.append("					sh2.getRow(rowIndexToBeCompared).createCell(i).setCellValue(writeDataIfEmpty);\n");
        sb.append("					FileOutputStream out = new FileOutputStream(actualFilePath);\n");
        sb.append("					wb2.write(out);\n");
        sb.append("				}\n");
        sb.append("				if (changeColor) {\n");
        sb.append("					CellStyle style = wb2.createCellStyle();\n");
        sb.append("					Row row = sh2.getRow(rowIndexToBeCompared);\n");
        sb.append("					Cell cell = row.getCell(i);\n");
        sb.append("					cell.setCellStyle(style);\n");
        sb.append("					style.setFillForegroundColor(IndexedColors.RED.getIndex());\n");
        sb.append("					style.setFillPattern(FillPatternType.SOLID_FOREGROUND);\n");
        sb.append("					FileOutputStream out = new FileOutputStream(actualFilePath);\n");
        sb.append("					wb2.write(out);\n");
        sb.append("				}\n");
        sb.append("				address[failCount] = \"[\" + rowIndexToBeCompared + \":\" + i + \"]\";\n");
        sb.append("				failCount++;\n");
        sb.append("			}\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("	if (flag == true) {\n");
        sb.append("		System.out.println(\"The Cell data of given two rows match successfully\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"The Cell data of given two rows do not match\");\n");
        sb.append("		StringBuffer sb = new StringBuffer();\n");
        sb.append("		for (int i = 0; i < failCount; i++) {\n");
        sb.append("			sb.append(address[i]);\n");
        sb.append("			if (i != failCount - 1)\n");
        sb.append("				sb.append(\",\");\n");
        sb.append("		}\n");
        sb.append("		String str = sb.toString();\n");
        sb.append("		returnAddress = \"Unmatched Cell Address(RowIndex:CellIndex) : \n\"+ str;\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedFilePath::\"/home/tyss/File1.xlsx\"");
        params.add("actualFilePath::\"/home/tyss/File2.xlsx\"");
        params.add("expectedSheetName::\"xyz\"");
        params.add("actualSheetName::\"abc\"");
        params.add("rowIndexToBeCompared::1");
        params.add("rowIndexOfColumnHeader::2");
        params.add("changeColor::true");
        params.add("writeDataIfEmpty::\"xyz\"");

        return params;
    }
}
