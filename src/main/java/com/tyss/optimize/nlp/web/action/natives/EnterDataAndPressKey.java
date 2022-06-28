package com.tyss.optimize.nlp.web.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "EnterDataAndPressKey")
public class EnterDataAndPressKey implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String input = (String) attributes.get("input");
            String key = (String) attributes.get("key");
			String [] elementSplit=elementName.split(":");
        	elementName=elementSplit[1];
        	elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Entering input " + input + " and press key " + key + " on " + elementName + " " + elementType);
            String modifiedPassMessage = passMessage.replace("*input*", input).replace("*key*", key)
                    .replace("*elementName*", elementName).replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*input*", input).replace("*key*", key)
                    .replace("*elementName*", elementName).replace("*elementType*", elementType);
            key.toUpperCase().replace(" ", "").replace("_", "");
            switch (key) {
                case "ENTER":
                    element.sendKeys(input, Keys.ENTER);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ARROWUP":
                    element.sendKeys(input, Keys.ARROW_UP);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ARROWDOWN":
                    element.sendKeys(input, Keys.ARROW_DOWN);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ARROWLEFT":
                    element.sendKeys(input, Keys.ARROW_LEFT);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ARROWRIGHT":
                    element.sendKeys(input, Keys.ARROW_RIGHT);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "CONTROL":
                    element.sendKeys(input, Keys.CONTROL);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ESCAPE":
                    element.sendKeys(input, Keys.ESCAPE);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "SHIFT":
                    element.sendKeys(input, Keys.SHIFT);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "DELETE":
                    element.sendKeys(input, Keys.DELETE);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "TAB":
                    element.sendKeys(input, Keys.TAB);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ADD":
                    element.sendKeys(input, Keys.ADD);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "ALT":
                    element.sendKeys(input, Keys.ALT);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "SPACE":
                    element.sendKeys(input, Keys.SPACE);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "BACKSPACE":
                    element.sendKeys(input, Keys.BACK_SPACE);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "END":
                    element.sendKeys(input, Keys.END);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "CANCEL":
                    element.sendKeys(input, Keys.CANCEL);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "DOWN":
                    element.sendKeys(input, Keys.DOWN);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                case "UP":
                    element.sendKeys(input, Keys.UP);
                    log.info("Entered input " + input + " and pressed key " + key + " on " + elementName + " " + elementType);
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;

                default:
                    log.error("Entered wrong key");
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in EnterDataAndPressKey ", exception);
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

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("String value = key;\n");
        sb.append("value.toUpperCase().replace(\" \", \"\").replace(\"_\", \"\");\n");
        sb.append("switch (value) {\n");
        sb.append("	case \"ENTER\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ENTER);\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWUP\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ARROW_UP);\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWDOWN\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ARROW_DOWN);\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWLEFT\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ARROW_LEFT);\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWRIGHT\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ARROW_RIGHT);\n");
        sb.append("		break;\n");
        sb.append("	case \"CONTROL\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.CONTROL);\n");
        sb.append("		break;\n");
        sb.append("	case \"ESCAPE\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ESCAPE);\n");
        sb.append("		break;\n");
        sb.append("	case \"SHIFT\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.SHIFT);\n");
        sb.append("		break;\n");
        sb.append("	case \"DELETE\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.DELETE);\n");
        sb.append("		break;\n");
        sb.append("	case \"TAB\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.TAB);\n");
        sb.append("		break;\n");
        sb.append("	case \"ADD\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ADD);\n");
        sb.append("		break;\n");
        sb.append("	case \"ALT\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.ALT);\n");
        sb.append("		break;\n");
        sb.append("	case \"SPACE\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.SPACE);\n");
        sb.append("		break;\n");
        sb.append("	case \"BACKSPACE\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.BACK_SPACE);\n");
        sb.append("		break;\n");
        sb.append("	case \"END\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.END);\n");
        sb.append("		break;\n");
        sb.append("	case \"CANCEL\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.CANCEL);\n");
        sb.append("		break;\n");
        sb.append("	case \"DOWN\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.DOWN);\n");
        sb.append("		break;\n");
        sb.append("	case \"UP\":\n");
        sb.append("		element.sendKeys(INPUT, Keys.UP);\n");
        sb.append("		break;\n");
        sb.append("	default:\n");
        sb.append("		System.out.println(\"Entered wrong Key\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("key::\"xyz\"");
        params.add("INPUT::\"Test\"");

        return params;
    }

}
