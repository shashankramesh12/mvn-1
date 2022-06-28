package com.tyss.optimize.nlp.ios.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.ios.action.natives.IsEnabled;
import com.tyss.optimize.nlp.ios.program.app.Sleep;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_VerifyElementIsEnabled")
public class VerifyElementIsEnabled implements Nlp {
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
            Integer timeOutInSeconds = (Integer) attributes.get("timeOutInSeconds");
		    String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " screen ");
        
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName).replace("*elementType*", modElementType);
            String modifiedPassMessage = passMessage.replace("*elementName*", modElementName).replace("*elementType*", modElementType);
            NlpRequestModel requestEnabled = new NlpRequestModel();
            requestEnabled.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestEnabled.getAttributes().put("elementName", elementName);
            requestEnabled.getAttributes().put("elementType", elementType);
            requestEnabled.getAttributes().put("element", element);
            requestEnabled.getAttributes().put("containsChildNlp", true);
            requestEnabled.setPassMessage("*elementName* *elementType* is enabled");
            requestEnabled.setFailMessage("*elementName* *elementType* is disabled");

            NlpRequestModel requestSleep = new NlpRequestModel();
            requestSleep.getAttributes().put("containsChildNlp", true);
            requestSleep.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSleep.getAttributes().put("seconds", 1);
            requestSleep.setPassMessage("Waited for *seconds* seconds");
            requestSleep.setFailMessage("Failed to wait for *seconds* seconds");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Verifying if element: " + elementName + " " + elementType + " is enabled");
            String details = "";
            Boolean match = false, retry = true;
            int count = 0;
            while (retry) {
                count++;
                Boolean actualEnabledStatus = (Boolean) ((NlpResponseModel) new IsEnabled().execute(requestEnabled)).getAttributes().get("enable");
                details = "Expected Value:True \n  Actual Value:" + actualEnabledStatus;
                log.info(details);
                match = actualEnabledStatus.equals(true);
                if (match) {
                    log.info("Exit from loop because of match");
                    break;
                } else {
                    if (count < timeOutInSeconds) {
                        log.info("Waiting for match \n Iteration:" + count);
                        new Sleep().execute(requestSleep);
                    } else {
                        log.info("Exit from loop because of timeout");
                        break;
                    }
                }
            }
            if (match) {
                log.info(elementName + " " + elementType + " is enabled");
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error(elementName + " " + elementType + " is disabled");
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
            log.error("NLP_EXCEPTION in VerifyElementIsEnabled ", exception);
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
        sb.append("Boolean match = false, retry = true;\n");
        sb.append("int count = 0;\n");
        sb.append("while (retry) {\n");
        sb.append("	count++;\n");
        sb.append("	Boolean actualEnabledStatus = (Boolean) element.isEnabled();\n");
        sb.append("	match = actualEnabledStatus.equals(true);\n");
        sb.append("	if (match) {\n");
        sb.append("		break;\n");
        sb.append("	} else {\n");
        sb.append("		if (count < timeOutInSeconds) {\n");
        sb.append("			try{\n");
        sb.append("				Thread.sleep(sec);\n");
        sb.append("			}catch(InterruptedException ie){}\n");
        sb.append("		} else {\n");
        sb.append("			break;\n");
        sb.append("		}\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (match) {\n");
        sb.append("	System.out.println(\"Element is enabled\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Element is disabled\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("timeOutInSeconds::20");

        return params;
    }

}
