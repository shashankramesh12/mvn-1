package com.tyss.optimize.nlp.web.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.program.browser.Sleep;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component(value = "VerifyCssAttributeValue")
public class VerifyCssAttributeValue implements Nlp {

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
            WebElement element = (WebElement) attributes.get("element");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String elementType = (String) attributes.get("elementType");
            String attributeName = (String) attributes.get("attributeName");
            String expectedValue = (String) attributes.get("expectedValue");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying CSS attribute" + attributeName + " value of the element " + elementName + " " + elementType);
            String modifiedPassMessage = passMessage.replace("*attributeName*", attributeName)
                    .replace(" *elementName*", modElementName).replace("*elementType*", modElementType)
                    .replace("*expectedValue*", expectedValue);
            modifiedFailMessage = failMessage.replace("*attributeName*", attributeName)
                    .replace(" *elementName*", modElementName).replace("*elementType*", modElementType)
                    .replace("*expectedValue*", expectedValue);
            String details = "";
            Boolean match = false;
            int count = 0;
            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");

            while (count < explicitTimeOut) {
                count++;
                String actualValue = element.getCssValue(attributeName);
                details = "Expected Value:" + expectedValue + "\n" + "Actual    Value:" + actualValue;
                log.info(details);
                if (caseSensitive) {
                    match = expectedValue.equals(actualValue);
                    log.info("Case sensitive comparison, result is " + match);
                } else {
                    match = expectedValue.equalsIgnoreCase(actualValue);
                    log.info("Case insensitive comparison, result is " + match);
                }
                if (match) {
                    log.info("Exit from loop because of match");
                    break;
                }
                new Sleep().execute(requestSleep);
            }

            if (match) {
                log.info(attributeName + " CSS attribute value of " + elementName + " " + elementType + " is " + expectedValue);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(attributeName + "CSS attribute value of " + elementName + " " + elementType + " is not" + expectedValue);
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
            log.error("NLP_EXCEPTION in VerifyCssAttributeValue ", exception);
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

        sb.append("boolean match = false;\n");
        sb.append("int count = 0, seconds = 1;\n");
        sb.append("long sec = Integer.toUnsignedLong(1000 * seconds);\n");
        sb.append("boolean isSensitive = Boolean.valueOf(caseSensitive);\n");
        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("while (count < explicitTimeOut) {\n");
        sb.append("	count++;\n");
        sb.append("	String actualValue = element.getCssValue(attributeName);\n");
        sb.append("	if (isSensitive) {\n");
        sb.append("		match = expectedValue.equals(actualValue);\n");
        sb.append("	} else {\n");
        sb.append("		match = expectedValue.equalsIgnoreCase(actualValue);\n");
        sb.append("	}\n");
        sb.append("	if (match) {\n");
        sb.append("		break;\n");
        sb.append("	}\n");
        sb.append("	try {\n");
        sb.append("		Thread.sleep(sec);\n");
        sb.append("	} catch (InterruptedException ie) {}\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(attributeName + \" CSS attribute value is \" + expectedValue);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(attributeName + \" CSS attribute value is not \" + expectedValue);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("attributeName::\"randomName\"");
        params.add("expectedValue::\"xyz\"");
        params.add("explicitTimeOut::20");
        params.add("caseSensitive::\"true\"");

        return params;
    }
}
