package com.tyss.optimize.nlp.web.program.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.program.browser.GetPageTitle;
import com.tyss.optimize.nlp.web.program.browser.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyPartialTitleOfCurrentPage")
public class VerifyPartialTitleOfCurrentPage implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IDriver driver = nlpRequestModel.getDriver();
            String expectedTitle = (String) attributes.get("expectedTitle");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*expectedTitle*", expectedTitle);
            String modifiedPassMessage = passMessage.replace("*expectedTitle*", expectedTitle);

            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");

            NlpRequestModel requestTitle = new NlpRequestModel();
            requestTitle.setDriver(driver);
            requestTitle.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestTitle.setPassMessage("Title of current page is *returnValue*");
            requestTitle.setFailMessage("Failed to capture title of page");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Verifying the Title of the Current page");
            String details = "";
            Boolean match = false, retry = true;
            int count = 0;
            while (retry) {
                count++;
                String actualTitle = (String) ((NlpResponseModel) new GetPageTitle().execute(requestTitle)).getAttributes().get("title");
                details = "Expected title:" + expectedTitle + "\n" + "Actual title:" + actualTitle;
                log.info(details);
                if (caseSensitive) {
                    match = actualTitle.contains(expectedTitle);
                    log.info("Case sensitive comparison, result is: " + match);
                } else {
                    match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualTitle, expectedTitle);
                    log.info("Case insensitive comparison, result is: " + match);
                }
                if (match) {
                    log.info("Exit from loop because of match");
                    break;
                } else {
                    if (count < explicitTimeOut) {
                        log.info("Waiting for match; Iteration: " + count);
                        new Sleep().execute(requestSleep);
                    } else {
                        log.info("Exit from loop because of timeout");
                        break;
                    }
                }
            }
            if (match) {
                log.info("Window title contains " + expectedTitle);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Window title does not contain " + expectedTitle);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyPartialTitleOfCurrentPage ", exception);
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

        sb.append("int seconds = 1, count = 0;\n");
        sb.append("long sec = Integer.toUnsignedLong(1000 * seconds);\n");
        sb.append("boolean isSensitive = Boolean.valueOf(caseSensitive);\n");
        sb.append("boolean match = false, retry = true;\n");
        sb.append("while (retry) {\n");
        sb.append("	count++;\n");
        sb.append("	String actualTitle = driver.getTitle();\n");
        sb.append("	if (isSensitive) {\n");
        sb.append("		match = actualTitle.contains(expectedTitle);\n");
        sb.append("	} else {\n");
        sb.append("		match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualTitle, expectedTitle);\n");
        sb.append("	}\n");
        sb.append("	if (match) {\n");
        sb.append("		break;\n");
        sb.append("	} else {\n");
        sb.append("		if (count < explicitTimeOut) {\n");
        sb.append("			try{\n");
        sb.append("				Thread.sleep(sec);\n");
        sb.append("			}catch(InterruptedException ie){}\n");
        sb.append("		} else {\n");
        sb.append("			break;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(\"Window title contains \" + expectedTitle);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Window title does not contain \" + expectedTitle);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("expectedTitle::\"xyz\"");
        params.add("explicitTimeOut::10");
        params.add("caseSensitive::\"true\"");

        return params;
    }
}
