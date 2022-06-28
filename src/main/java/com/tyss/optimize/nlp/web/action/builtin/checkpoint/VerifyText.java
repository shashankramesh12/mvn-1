package com.tyss.optimize.nlp.web.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.natives.GetText;
import com.tyss.optimize.nlp.web.program.browser.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyText")
public class VerifyText implements Nlp {

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
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String expectedText = (String) attributes.get("expectedText");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = (String) nlpRequestModel.getPassMessage();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestText = new NlpRequestModel();
            requestText.getAttributes().put("containsChildNlp", true);
            requestText.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestText.getAttributes().put("elementName", elementName);
            requestText.getAttributes().put("elementType", elementType);
            requestText.getAttributes().put("element", element);
            requestText.setPassMessage("The text of *elementName* *elementType* is *returnValue*");
            requestText.setFailMessage("Failed to capture text from *elementName* *elementType*");

            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");

            modifiedFailMessage = failMessage.replace("*elementName*",modElementName).replace("*elementType*",modElementType).replace("*expectedText*",expectedText);
            String modifiedPassMessage = passMessage.replace("*elementName*",modElementName).replace("*elementType*",modElementType).replace("*expectedText*",expectedText);
            log.info("Verifying text of " + elementName + " " + elementType+"is "+expectedText);
            String details = "";
            Boolean match = false, retry = true;
            int count = 0;
            while (retry) {
                count++;
                String actualText = (String) ((NlpResponseModel) new GetText().execute(requestText)).getAttributes().get("text");
                details = "Expected Text:" + expectedText + "\n" + "Actual    Text:" + actualText;
                log.info(details);
                if (caseSensitive) {
                    match = expectedText.equals(actualText);
                    log.info("Case Sensitive comparison, result is:" + match);
                } else {
                    match = expectedText.equalsIgnoreCase(actualText);
                    log.info("Case Insensitive comparison, result is:" + match);
                }
                if (match) {
                    log.info("Exit from loop because of match");
                    break;
                } else {
                    if (count < explicitTimeOut) {
                        log.info("Waiting for match" + "\n" + "Iteration:" + count);
                        new Sleep().execute(requestSleep);
                    } else {
                        log.info("Exit from loop because of TimeOut");
                        break;
                    }
                }
            }
            if (match) {
                log.info("The text of  " + elementName + " " + elementType + " is " + expectedText);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("The text of  " + elementName + " " + elementType + " is not " + expectedText);
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
            log.error("NLP_EXCEPTION in VerifyText ", exception);
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

        sb.append("boolean match = false, retry = true;\n");
        sb.append("int count = 0, seconds = 1;\n");
        sb.append("long sec = Integer.toUnsignedLong(1000 * seconds);\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("while (retry) {\n");
        sb.append("	count++;\n");
        sb.append("	String actualText = element.getText();\n");
        sb.append("	if (caseSensitive) {\n");
        sb.append("		match = expectedText.equals(actualText);\n");
        sb.append("	} else {\n");
        sb.append("		match = expectedText.equalsIgnoreCase(actualText);\n");
        sb.append("	}\n");
        sb.append("	if (match) {\n");
        sb.append("		break;\n");
        sb.append("	} else {\n");
        sb.append("		if (count < explicitTimeOut) {\n");
        sb.append("			try {\n");
        sb.append("				Thread.sleep(sec);\n");
        sb.append("			} catch (InterruptedException ie) {}\n");
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
        params.add("expectedText::\"xyz\"");
        params.add("explicitTimeOut::20");
        params.add("caseSensitive::true");

        return params;
    }
}
