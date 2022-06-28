package com.tyss.optimize.nlp.web.console;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.devtools.v96.log.Log;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component(value = "GetAllConsoleWarnings")
public class GetAllConsoleWarnings implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        boolean containsChildNlp = false;
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Long startTime = System.currentTimeMillis();
        try{
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            WebDriver driver =null;
            if(Objects.nonNull(nlpRequestModel.getDriver())) {
                driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            }
            LogEntries entry = driver.manage().logs().get(LogType.BROWSER);
            List<LogEntry> logs= entry.getAll();
            List<String> logEntries = new ArrayList<>();
            if(logs.size() > 0){
                logs.stream().filter(log -> log.getLevel().toString().equalsIgnoreCase("WARNING")).map(log -> logEntries.add(log.getMessage())).collect(Collectors.toList());
            }
            if(logEntries.isEmpty()){
                new WebDriverDevTools().getDevTools(driver).send(Log.enable());
                new WebDriverDevTools().getDevTools(driver).addListener(Log.entryAdded(), logEntry -> {
                    if(logEntry.getLevel().name().equalsIgnoreCase("WARNING")){
                        logEntries.add(logEntry.getText());
                    }
                });
            }
            nlpResponseModel.getAttributes().put("logs",logEntries);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception){
            log.error("NLP_EXCEPTION in GetAllConsoleWarnings ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("LogEntries entry = driver.manage().logs().get(LogType.BROWSER);\n");
        sb.append("List<LogEntry> logs= entry.getAll();\n");
        sb.append("List<String> logEntries = new ArrayList<>();\n");
        sb.append("if(logs.size() > 0){\n");
        sb.append("	logs.stream().filter(log -> log.getLevel().toString().equalsIgnoreCase(\"WARNING\")).map(log -> logEntries.add(log.getMessage())).collect(Collectors.toList());\n");
        sb.append("}\n");
        sb.append("if(logEntries.isEmpty()){\n");
        sb.append("	new WebDriverDevTools().getDevTools(driver).send(Log.enable());\n");
        sb.append("	new WebDriverDevTools().getDevTools(driver).addListener(Log.entryAdded(),logEntry -> {\n");
        sb.append("		if(logEntry.getLevel().name().equalsIgnoreCase(\"WARNING\")){\n");
        sb.append("			logEntries.add(logEntry.getText());\n");
        sb.append("		}\n");
        sb.append("	});\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
