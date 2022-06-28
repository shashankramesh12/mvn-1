package com.tyss.optimize.nlp.mobile.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.mobile.action.natives.GetAttribute;
import com.tyss.optimize.nlp.mobile.program.app.Sleep;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_VerifyAttributeValue")
public class VerifyAttributeValue implements Nlp {

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
            String attributeName = (String) attributes.get("attributeName");
            String expectedValue = (String) attributes.get("expectedValue");
            Boolean caseSensitive = (Boolean) attributes.get("caseSensitive");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " screen ");
            String passMessage=(String) nlpRequestModel.getPassMessage();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            modifiedFailMessage = failMessage.replace("*attributeName*", attributeName).replace("*elementName*", modElementName).replace("*elementType*", modElementType).replace("*expectedValue*", expectedValue);
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestAttribute = new NlpRequestModel();
            requestAttribute.getAttributes().put("containsChildNlp", true);
            requestAttribute.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestAttribute.getAttributes().put("elementName", elementName);
            requestAttribute.getAttributes().put("elementType", elementType);
            requestAttribute.getAttributes().put("element", element);
            requestAttribute.getAttributes().put("attributeName", attributeName);
            requestAttribute.setPassMessage("The attribute *attributeName* value of *elementName* *elementType* is *returnValue*");
            requestAttribute.setFailMessage("Failed to fetch attribute *attributeName* value of *elementName* *elementType*");

            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Verifying " + attributeName + " attribute value of " + elementName + " " + elementType + " is " + expectedValue);
            String details = "";
            Boolean match = false;
            int count = 0;

            while (count < explicitTimeOut) {
                count++;
                String actualValue = (String) ((NlpResponseModel) new GetAttribute().execute(requestAttribute)).getAttributes().get("value");

                details = "Exepected value:" + expectedValue + "\n" + "Actual value: " + actualValue;
                log.info(details);
                if (caseSensitive) {
                    match = expectedValue.equals(actualValue);
                    log.info("Case sensitive comparision, result is:" + match);
                } else {
                    match = expectedValue.equalsIgnoreCase(actualValue);
                    log.info("Case insensitive comparision, result is:" + match);
                }
                if (match) {
                    log.info("Exit from loop because of match");
                    break;
                }
                new Sleep().execute(requestSleep);
            }
            if (match) {
                log.info(attributeName + "attribute value of " + elementName + " " + elementType + " is " + expectedValue);
                nlpResponseModel.setMessage(passMessage.replace("*attributeName*", attributeName).replace("*elementName*", modElementName).replace("*elementType*", modElementType).replace("*expectedValue*", expectedValue));
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.info(attributeName + " attribute value of " + elementName + " " + elementType + " is not " + expectedValue);
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
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
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
        sb.append("Integer seconds = 1;\n");
        sb.append("long sec = Integer.toUnsignedLong(1000 * seconds);\n");
        sb.append("WebElement element = androidDriver.findElement(ELEMENT);\n");
        sb.append("boolean isSensitive = Boolean.valueOf(caseSensitive);\n");
        sb.append("int count = 0;\n");
        sb.append("while (count < explicitTimeOut) {\n");
        sb.append("	count++;\n");
        sb.append("	String actualValue = element.getAttribute(attributeName);\n");
        sb.append("	if (isSensitive) {\n");
        sb.append("		match = expectedValue.equals(actualValue);\n");
        sb.append("	} else {\n");
        sb.append("		match = expectedValue.equalsIgnoreCase(actualValue);\n");
        sb.append("	}\n");
        sb.append("	if (match) {\n");
        sb.append("		break;\n");
        sb.append("	}\n");
        sb.append("	try{\n");
        sb.append("		Thread.sleep(sec);\n");
        sb.append("	}catch(InterruptedException ie){}\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(attributeName + \" attribute value is \" + expectedValue);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(attributeName + \" attribute value is not \" + expectedValue);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("attributeName::\"randomValue\"");
        params.add("expectedValue::\"randomValue\"");
        params.add("caseSensitive::\"true\"");
        params.add("explicitTimeOut::20");

        return params;
    }

}
