package com.tyss.optimize.nlp.web.program.builtin.checkpoint;


import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.program.browser.GetPageTitle;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component(value = "VerifyTitleInAllPage")
public class VerifyTitleInAllPage implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String expectedTitle = (String) attributes.get("expectedTitle");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            Boolean stopTestIfFailed = (Boolean) attributes.get("stopTestIfFailed");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestTitle = new NlpRequestModel();
            requestTitle.getAttributes().put("containsChildNlp", true);
            modifiedFailMessage = failMessage.replace("*expectedTitle*", expectedTitle);
            String modifiedPassMessage = passMessage.replace("*expectedTitle*", expectedTitle);
            log.info("Verifying the title of all opened browser window");
            String parent = driver.getWindowHandle();
            Set<String> allWHS = driver.getWindowHandles();
            int windowSize = allWHS.size();
            Boolean match = false;
            String details = "";
            log.info("Number of windows/tabs:" + windowSize);
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            for (String wh : allWHS) {
                log.info("Switching to window");
                driver.switchTo().window(wh);
                String actualTitle = (String) ((NlpResponseModel) new GetPageTitle().execute(nlpRequestModel)).getAttributes().get("title");
                details = "Exepected title:" + expectedTitle + "\n" + "Actual title:" + actualTitle;
                log.info(details);
                if (caseSensitive) {
                    match = expectedTitle.equals(actualTitle);
                    log.info("Case sensitive comparision, result is:" + match);
                } else {
                    match = expectedTitle.equalsIgnoreCase(actualTitle);
                    log.info("Case insensitive comparision, result is:" + match);
                }
                if (match) {
                    break;
                }
            }
            log.info("Switching back to parent window");
            driver.switchTo().window(parent);
            if (match) {
                log.info("Title of all opened browser windows matches " + expectedTitle);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.info("Title of all opened browser windows does not match " + expectedTitle);
                if (stopTestIfFailed) {
                    log.error("AssertionFailedException " + details);
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                } else {
                    log.error("VerificationFailedException " + details);
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyTitleInAllPage ", exception);
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

        sb.append("String parent = driver.getWindowHandle();\n");
        sb.append("Set<String> allWHS = driver.getWindowHandles();\n");
        sb.append("int windowSize = allWHS.size();\n");
        sb.append("boolean isSensitive = Boolean.valueOf(caseSensitive);\n");
        sb.append("boolean isStop = Boolean.valueOf(stopTestIfFailed);\n");
        sb.append("boolean match = false;\n");
        sb.append("for (String wh : allWHS) {\n");
        sb.append("	String actualTitle = driver.getTitle();\n");
        sb.append("	if (isSensitive) {\n");
        sb.append("		match = expectedTitle.equals(actualTitle);\n");
        sb.append("	} else {\n");
        sb.append("		match = expectedTitle.equalsIgnoreCase(actualTitle);\n");
        sb.append("	}\n");
        sb.append("	if (match) {\n");
        sb.append("		break;\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("driver.switchTo().window(parent);\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(\"Title of all opened browser windows matches \" + expectedTitle);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Title of all opened browser windows does not match \" + expectedTitle);\n");
        sb.append("	if (isStop) {\n");
        sb.append("		System.out.println(\"AssertionFailedException\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"VerificationFailedException\");\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedTitle::\"xyz\"");
        params.add("caseSensitive::\"true\"");
        params.add("stopTestIfFailed::\"true\"");

        return params;
    }
}
