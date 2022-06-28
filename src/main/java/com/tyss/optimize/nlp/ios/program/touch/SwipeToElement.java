package com.tyss.optimize.nlp.ios.program.touch;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_SwipeToElement")
public class SwipeToElement implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IOSDriver iosDriver = (IOSDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String locatorType = (String) attributes.get("locatorType");
            String locatorValue = (String) attributes.get("locatorValue");
            Integer fromX = (Integer) attributes.get("fromX");
            Integer fromY = (Integer) attributes.get("fromY");
            Integer toX = (Integer) attributes.get("toX");
            Integer toY = (Integer) attributes.get("toY");
            Integer maxScroll = (Integer) attributes.get("maxScroll");
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Swiping to element");
            Boolean flag = false;

            while (maxScroll != 0) {
                try {
                    By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);
                    if (iosDriver.findElement(b).getSize().width > 0) {
                        log.info("Found element");
                        flag = true;
                        break;
                    }
                } catch (Exception e) {
                    TouchAction action = new TouchAction(iosDriver);
                    action.longPress(PointOption.point(fromX, fromY))
                            .waitAction(WaitOptions.waitOptions(Duration.ofMillis(3)))
                            .moveTo(PointOption.point(toX, toY)).release().perform();
                    maxScroll--;
                }
            }
            if (flag) {
                log.info("Swiped to element");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.error("Failed to swipe to element");
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }

        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in SwipeToElement", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Boolean flag = false;\n");
        sb.append("while (maxScroll != 0) {\n");
        sb.append("	try {\n");
        sb.append("		By b = (By) By.class.getDeclaredMethod(locatorType, String.class).invoke(null, locatorValue);\n");
        sb.append("		if (iosDriver.findElement(b).getSize().width > 0) {\n");
        sb.append("			flag = true;\n");
        sb.append("			break;\n");
        sb.append("		}\n");
        sb.append("	} catch (Exception e) {\n");
        sb.append("		TouchAction action = new TouchAction(iosDriver);\n");
        sb.append("		action.longPress(PointOption.point(fromX, fromY))\n");
        sb.append("				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(3)))\n");
        sb.append("				.moveTo(PointOption.point(toX, toY)).release().perform();\n");
        sb.append("		maxScroll--;\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (flag) {\n");
        sb.append("	System.out.println(\"Swiped to element\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Failed to swipe to element\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("maxScroll::5");
        params.add("locatorType::\"id\"");
        params.add("locatorValue::\"randomId\"");
        params.add("fromX::10");
        params.add("fromY::20");
        params.add("toX::30");
        params.add("toY::40");

        return params;
    }

}
