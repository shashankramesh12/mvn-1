package com.tyss.optimize.nlp.web.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.natives.GetAttribute;
import com.tyss.optimize.nlp.web.program.browser.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyPartialAttributeValue")
public class VerifyPartialAttributeValue implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String attributeName = (String) attributes.get("attributeName");
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            WebElement element = (WebElement) attributes.get("element");
            String expectedValue = (String) attributes.get("expectedValue");
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
            modifiedFailMessage = failMessage.replace("*attributeName*", attributeName).replace("*elementName*", elementName).replace("*elementType*", elementType).replace("*expectedValue*", expectedValue);
            String modifiedPassMessage = passMessage.replace("*attributeName*", attributeName).replace("*elementName*", elementName).replace("*elementType*", elementType).replace("*expectedValue*", expectedValue);
            NlpRequestModel requestAttribute = new NlpRequestModel();
            requestAttribute.getAttributes().put("containsChildNlp", true);
            requestAttribute.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestAttribute.getAttributes().put("elementName", elementName);
            requestAttribute.getAttributes().put("elementType", elementType);
            requestAttribute.getAttributes().put("element", element);
            requestAttribute.getAttributes().put("attributeName", attributeName);
            requestAttribute.setPassMessage("Attribute *attributeName* value of *elementName* *elementType* is *returnValue*");
            requestAttribute.setFailMessage("Failed to capture attribute *attributeName* value of *elementName* *elementType*");

            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");

            log.info("Verifying  attribute" + attributeName + " value of " + elementName + " " + elementType);
            String details = "";
            Boolean match = false, retry = true;
            int count = 0;
            while (retry) {
                count++;
                String actualValue = (String) ((NlpResponseModel) new GetAttribute().execute(requestAttribute)).getAttributes().get("value");
                details = "Expected Value:" + expectedValue + "\n" + "Actual Value:" + actualValue;
                log.info(details);
                if (caseSensitive) {
                    match = actualValue.contains(expectedValue);
                    log.info("Case Sensitive comparison, result is:" + match);
                } else {
                    match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualValue, expectedValue);
                    log.info("Case insensitive comparison, result is:" + match);
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
                log.info(attributeName + " " + " attribute value of " + elementName + " " + elementType + " contains " + expectedValue);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(attributeName + " " + " attribute value of " + elementName + " " + elementType + " does not contain " + expectedValue);
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
            log.error("NLP_EXCEPTION in VerifyPartialAttributeValue ", exception);
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
        sb.append("	String actualValue = element.getAttribute(attributeName);\n");
        sb.append("	if (caseSensitive) {\n");
        sb.append("		match = actualValue.contains(expectedValue);\n");
        sb.append("	} else {\n");
        sb.append("		match = org.apache.commons.lang3.StringUtils.containsIgnoreCase(actualValue, expectedValue);\n");
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
        sb.append("	System.out.println(attributeName + \" attribute value contains \" + expectedValue);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(attributeName + \" attribute value does not contain \" + expectedValue);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("attributeName::\"randomName\"");
        params.add("expectedValue::\"xyz\"");
        params.add("explicitTimeOut::20");
        params.add("caseSensitive::true");

        return params;
    }
}
