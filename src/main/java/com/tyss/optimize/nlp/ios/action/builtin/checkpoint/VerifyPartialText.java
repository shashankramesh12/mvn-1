package com.tyss.optimize.nlp.ios.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.ios.action.natives.GetText;
import com.tyss.optimize.nlp.ios.program.app.Sleep;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_VerifyPartialText")
public class VerifyPartialText implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            String expectedText = (String) attributes.get("expectedText");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " screen ");
        
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName).replace("*elementType*", modElementType).replace("*expectedText*", expectedText);
            String modifiedPassMessage = passMessage.replace("*elementName*", modElementName).replace("*elementType*", modElementType).replace("*expectedText*", expectedText);
            NlpRequestModel requestText = new NlpRequestModel();
            requestText.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestText.getAttributes().put("elementName", elementName);
            requestText.getAttributes().put("elementType", elementType);
            requestText.getAttributes().put("element", element);
            requestText.getAttributes().put("containsChildNlp", true);
            requestText.setPassMessage("The text of *elementName* *elementType* is *returnValue*");
            requestText.setFailMessage("Failed to fetch text from *elementName* *elementType*");

            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Verifying the text of " + elementName + " " + elementType + " contains " + expectedText);
            String details = "";
            Boolean match = false, retry = true;
            int count = 0;
            while (retry) {
                count++;
                String actualText = (String) ((NlpResponseModel) new GetText().execute(requestText)).getAttributes().get("text");
                details = "Expected Text: " + expectedText + "\n" + "Actual text:" + actualText;
                log.info(details);
                if (caseSensitive) {
                    match = actualText.contains(expectedText);
                    log.info("Case Sensitive comparison, result is:" + match);
                } else {
                    match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualText, expectedText);
                    log.info("Case Insensitive comparison, result is:" + match);
                }
                if (match) {
                    log.info("Exit from loop because of match");
                    break;
                } else {
                    if (count < explicitTimeOut) {
                        log.info("Waiting for match " + "\n" + " Iteration:" + count);
                        new Sleep().execute(requestSleep);
                    } else {
                        log.info("Exit from loop because of timeout");
                        break;
                    }
                }
            }
            if (match) {
                log.info("The text of " + elementName + " " + elementType + " contains " + expectedText);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The text of " + elementName + " " + elementType + " does not contain " + expectedText);
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
            log.error("NLP_EXCEPTION in VerifyPartialText ", exception);
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

        sb.append("Integer seconds = 1;\n");
        sb.append("long sec = Integer.toUnsignedLong(1000 * seconds);\n");
        sb.append("WebElement element = iosDriver.findElement(ELEMENT);\n");
        sb.append("boolean isSensitive = Boolean.valueOf(caseSensitive);\n");
        sb.append("boolean match = false, retry = true;\n");
        sb.append("int count = 0;\n");
        sb.append("while (retry) {\n");
        sb.append("	count++;\n");
        sb.append("	String actualText = (String) element.getText();\n");
        sb.append("	if (isSensitive) {\n");
        sb.append("		match = actualText.contains(expectedText);\n");
        sb.append("	} else {\n");
        sb.append("		match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualText, expectedText);\n");
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
        sb.append("	System.out.println(\"The text contains \" + expectedText);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The text does not contain \" + expectedText);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("expectedText::\"randomValue\"");
        params.add("caseSensitive::\"true\"");
        params.add("explicitTimeOut::20");

        return params;
    }

}
