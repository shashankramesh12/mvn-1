package com.tyss.optimize.nlp.web.action.builtin.keyboard;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "PressAnyKey")
public class PressAnyKey implements Nlp {

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
            String key = (String) attributes.get("key");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Pressing" + key + "Key");
            String modifiedPassMessage = passMessage.replace("*key*", key);
            modifiedFailMessage = failMessage.replace("*key*", key);
            key.toUpperCase().replace(" ", "").replace("_", "");
            Actions actions = new Actions(driver);
            switch (key) {
                case "ENTER":
                    actions.sendKeys(Keys.ENTER).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ESCAPE":
                    actions.sendKeys(Keys.ESCAPE).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ARROWUP":
                    actions.sendKeys(Keys.ARROW_UP).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ARROWDOWN":
                    actions.sendKeys(Keys.ARROW_DOWN).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ARROWLEFT":
                    actions.sendKeys(Keys.ARROW_LEFT).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ARROWRIGHT":
                    actions.sendKeys(Keys.ARROW_RIGHT).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "DELETE":
                    actions.sendKeys(Keys.DELETE).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "CONTROL":
                    actions.sendKeys(Keys.CONTROL).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SHIFT":
                    actions.sendKeys(Keys.SHIFT).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "EQUALS":
                    actions.sendKeys(Keys.EQUALS).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "END":
                    actions.sendKeys(Keys.END).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ADD":
                    actions.sendKeys(Keys.ADD).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ALT":
                    actions.sendKeys(Keys.ALT).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "TAB":
                    actions.sendKeys(Keys.TAB).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SPACE":
                    actions.sendKeys(Keys.SPACE).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "BACKSPACE":
                    actions.sendKeys(Keys.BACK_SPACE).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "CANCEL":
                    actions.sendKeys(Keys.CANCEL).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "DIVIDE":
                    actions.sendKeys(Keys.DIVIDE).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F1":
                    actions.sendKeys(Keys.F1).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F2":
                    actions.sendKeys(Keys.F2).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F3":
                    actions.sendKeys(Keys.F3).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F4":
                    actions.sendKeys(Keys.F4).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F5":
                    actions.sendKeys(Keys.F5).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F6":
                    actions.sendKeys(Keys.F6).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F7":
                    actions.sendKeys(Keys.F7).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F8":
                    actions.sendKeys(Keys.F8).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F9":
                    actions.sendKeys(Keys.F9).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F10":
                    actions.sendKeys(Keys.F10).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "F11":
                    actions.sendKeys(Keys.F11).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "HELP":
                    actions.sendKeys(Keys.HELP).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SUBTRACT":
                    actions.sendKeys(Keys.SUBTRACT).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "MULTIPLY":
                    actions.sendKeys(Keys.MULTIPLY).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "DOWN":
                    actions.sendKeys(Keys.DOWN).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "UP":
                    actions.sendKeys(Keys.UP).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "PAGEDOWN":
                    actions.sendKeys(Keys.PAGE_DOWN).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "PAGEUP":
                    actions.sendKeys(Keys.PAGE_UP).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SEMICOLON":
                    actions.sendKeys(Keys.SEMICOLON).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SEPARATOR":
                    actions.sendKeys(Keys.SEPARATOR).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "RETURN":
                    actions.sendKeys(Keys.RETURN).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "0":
                    actions.sendKeys(Keys.NUMPAD0).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "1":
                    actions.sendKeys(Keys.NUMPAD1).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "2":
                    actions.sendKeys(Keys.NUMPAD2).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "3":
                    actions.sendKeys(Keys.NUMPAD3).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "4":
                    actions.sendKeys(Keys.NUMPAD4).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "5":
                    actions.sendKeys(Keys.NUMPAD5).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "6":
                    actions.sendKeys(Keys.NUMPAD6).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "7":
                    actions.sendKeys(Keys.NUMPAD7).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "8":
                    actions.sendKeys(Keys.NUMPAD8).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "9":
                    actions.sendKeys(Keys.NUMPAD9).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ZERO":
                    actions.sendKeys(Keys.NUMPAD0).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "ONE":
                    actions.sendKeys(Keys.NUMPAD1).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "TWO":
                    actions.sendKeys(Keys.NUMPAD2).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "THREE":
                    actions.sendKeys(Keys.NUMPAD3).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "FOUR":
                    actions.sendKeys(Keys.NUMPAD4).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "FIVE":
                    actions.sendKeys(Keys.NUMPAD5).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SIX":
                    actions.sendKeys(Keys.NUMPAD6).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "SEVEN":
                    actions.sendKeys(Keys.NUMPAD7).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "EIGHT":
                    actions.sendKeys(Keys.NUMPAD8).perform();
                    log.info("Pressed" + key + "Key");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                    break;
                case "NINE":
                    actions.sendKeys(Keys.NUMPAD9).perform();
                    log.info("Pressed" + key + "Key");
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
            log.error("NLP_EXCEPTION in PressAnyKey ", exception);
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

        sb.append("String keyVal = key;\n");
        sb.append("keyVal.toUpperCase().replace(\" \", \"\").replace(\"_\", \"\");\n");
        sb.append("Actions actions = new Actions(driver);\n");
        sb.append("switch (keyVal) {\n");
        sb.append("	case \"ENTER\":\n");
        sb.append("		actions.sendKeys(Keys.ENTER).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ESCAPE\":\n");
        sb.append("		actions.sendKeys(Keys.ESCAPE).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWUP\":\n");
        sb.append("		actions.sendKeys(Keys.ARROW_UP).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWDOWN\":\n");
        sb.append("		actions.sendKeys(Keys.ARROW_DOWN).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWLEFT\":\n");
        sb.append("		actions.sendKeys(Keys.ARROW_LEFT).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ARROWRIGHT\":\n");
        sb.append("		actions.sendKeys(Keys.ARROW_RIGHT).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"DELETE\":\n");
        sb.append("		actions.sendKeys(Keys.DELETE).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"CONTROL\":\n");
        sb.append("		actions.sendKeys(Keys.CONTROL).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SHIFT\":\n");
        sb.append("		actions.sendKeys(Keys.SHIFT).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"EQUALS\":\n");
        sb.append("		actions.sendKeys(Keys.EQUALS).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"END\":\n");
        sb.append("		actions.sendKeys(Keys.END).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ADD\":\n");
        sb.append("		actions.sendKeys(Keys.ADD).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ALT\":\n");
        sb.append("		actions.sendKeys(Keys.ALT).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"TAB\":\n");
        sb.append("		actions.sendKeys(Keys.TAB).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SPACE\":\n");
        sb.append("		actions.sendKeys(Keys.SPACE).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"BACKSPACE\":\n");
        sb.append("		actions.sendKeys(Keys.BACK_SPACE).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"CANCEL\":\n");
        sb.append("		actions.sendKeys(Keys.CANCEL).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"DIVIDE\":\n");
        sb.append("		actions.sendKeys(Keys.DIVIDE).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F1\":\n");
        sb.append("		actions.sendKeys(Keys.F1).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F2\":\n");
        sb.append("		actions.sendKeys(Keys.F2).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F3\":\n");
        sb.append("		actions.sendKeys(Keys.F3).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F4\":\n");
        sb.append("		actions.sendKeys(Keys.F4).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F5\":\n");
        sb.append("		actions.sendKeys(Keys.F5).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F6\":\n");
        sb.append("		actions.sendKeys(Keys.F6).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F7\":\n");
        sb.append("		actions.sendKeys(Keys.F7).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F8\":\n");
        sb.append("		actions.sendKeys(Keys.F8).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F9\":\n");
        sb.append("		actions.sendKeys(Keys.F9).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F10\":\n");
        sb.append("		actions.sendKeys(Keys.F10).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"F11\":\n");
        sb.append("		actions.sendKeys(Keys.F11).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"HELP\":\n");
        sb.append("		actions.sendKeys(Keys.HELP).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SUBTRACT\":\n");
        sb.append("		actions.sendKeys(Keys.SUBTRACT).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"MULTIPLY\":\n");
        sb.append("		actions.sendKeys(Keys.MULTIPLY).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"DOWN\":\n");
        sb.append("		actions.sendKeys(Keys.DOWN).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"UP\":\n");
        sb.append("		actions.sendKeys(Keys.UP).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"PAGEDOWN\":\n");
        sb.append("		actions.sendKeys(Keys.PAGE_DOWN).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"PAGEUP\":\n");
        sb.append("		actions.sendKeys(Keys.PAGE_UP).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SEMICOLON\":\n");
        sb.append("		actions.sendKeys(Keys.SEMICOLON).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SEPARATOR\":\n");
        sb.append("		actions.sendKeys(Keys.SEPARATOR).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"RETURN\":\n");
        sb.append("		actions.sendKeys(Keys.RETURN).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"0\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD0).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"1\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD1).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"2\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD2).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"3\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD3).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"4\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD4).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"5\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD5).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"6\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD6).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"7\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD7).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"8\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD8).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"9\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD9).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ZERO\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD0).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"ONE\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD1).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"TWO\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD2).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"THREE\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD3).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"FOUR\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD4).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"FIVE\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD5).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SIX\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD6).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"SEVEN\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD7).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"EIGHT\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD8).perform();\n");
        sb.append("		break;\n");
        sb.append("	case \"NINE\":\n");
        sb.append("		actions.sendKeys(Keys.NUMPAD9).perform();\n");
        sb.append("		break;\n");
        sb.append("	default:\n");
        sb.append("		System.out.println(\"Entered wrong key\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("key::\"xyz\"");

        return params;
    }
}
