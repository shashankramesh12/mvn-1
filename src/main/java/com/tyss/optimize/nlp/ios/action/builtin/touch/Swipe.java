package com.tyss.optimize.nlp.ios.action.builtin.touch;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.ios.action.get.GetX;
import com.tyss.optimize.nlp.ios.action.get.GetY;
import com.tyss.optimize.nlp.ios.program.touch.SwipeDown;
import com.tyss.optimize.nlp.ios.program.touch.SwipeLeftToRight;
import com.tyss.optimize.nlp.ios.program.touch.SwipeRightToLeft;
import com.tyss.optimize.nlp.ios.program.touch.SwipeUp;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_Builtin_Swipe")
public class Swipe implements Nlp {
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
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
            String direction = (String) attributes.get("direction");
            Integer toCoordinate = (Integer) attributes.get("toCoordinate");
            Integer howManyTimes = (Integer) attributes.get("howManyTimes");
            String failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestX = new NlpRequestModel();
            requestX.getAttributes().put("containsChildNlp", true);
            requestX.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestX.getAttributes().put("elementName", elementName);
            requestX.getAttributes().put("elementType", elementType);
            requestX.getAttributes().put("element", element);
            requestX.setPassMessage(" X Coordinate of *elementName* *elementType* is *returnValue*");
            requestX.setFailMessage("Failed to fetch X Coordinate of *elementName* *elementType*");

            NlpRequestModel requestY = new NlpRequestModel();
            requestY.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestY.getAttributes().put("elementName", elementName);
            requestY.getAttributes().put("elementType", elementType);
            requestY.getAttributes().put("element", element);
            requestY.getAttributes().put("containsChildNlp", true);
            requestY.setPassMessage("Y Coordinate of *elementName* *elementType* is *returnValue*");
            requestY.setFailMessage("Failed to get Y Coordinate of *elementName* *elementType*");

            Integer fromX = (Integer) ((NlpResponseModel) new GetX().execute(requestX)).getAttributes().get("xLocation");
            Integer fromY = (Integer) ((NlpResponseModel) new GetY().execute(requestY)).getAttributes().get("yLocation");

            NlpRequestModel requestRight = new NlpRequestModel();
            requestRight.setDriver(driver);
            requestRight.getAttributes().put("containsChildNlp", true);
            requestRight.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestRight.getAttributes().put("fromX", fromX);
            requestRight.getAttributes().put("fromY", fromY);
            requestRight.getAttributes().put("toCoordinate", toCoordinate);
            requestRight.setPassMessage("Swiped the screen from  right to left");
            requestRight.setFailMessage("Failed to swipe screen from right to left");

            NlpRequestModel requestLeft = new NlpRequestModel();
            requestLeft.setDriver(driver);
            requestLeft.getAttributes().put("containsChildNlp", true);
            requestLeft.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestLeft.getAttributes().put("fromX", fromX);
            requestLeft.getAttributes().put("fromY", fromY);
            requestLeft.getAttributes().put("toCoordinate", toCoordinate);
            requestLeft.setPassMessage("Swiped from left to right");
            requestLeft.setFailMessage("Failed to swipe from left to right");

            NlpRequestModel requestDown = new NlpRequestModel();
            requestDown.setDriver(driver);
            requestDown.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestDown.getAttributes().put("fromX", fromX);
            requestDown.getAttributes().put("fromY", fromY);
            requestDown.getAttributes().put("containsChildNlp", true);
            requestDown.getAttributes().put("toCoordinate", toCoordinate);
            requestDown.setPassMessage("Swiped from top to bottom");
            requestDown.setFailMessage("Failed to swipe from top to bottom");

            NlpRequestModel requestUp = new NlpRequestModel();
            requestUp.setDriver(driver);
            requestUp.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestUp.getAttributes().put("fromX", fromX);
            requestUp.getAttributes().put("fromY", fromY);
            requestUp.getAttributes().put("containsChildNlp", true);
            requestUp.getAttributes().put("toCoordinate", toCoordinate);
            requestUp.setPassMessage("Swiped screen from bottom to top");
            requestUp.setFailMessage("Failed to swipe screen from bottom to top");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            modifiedFailMessage = failMessage.replace("*direction*", direction).replace("*toCoordinate*", String.valueOf(toCoordinate)).replace("*howManyTimes*", String.valueOf(howManyTimes));
            String modifiedPassMessage = passMessage.replace("*direction*", direction).replace("*toCoordinate*", String.valueOf(toCoordinate)).replace("*howManyTimes*", String.valueOf(howManyTimes));
            log.info("Swipe " + direction + " to " + toCoordinate + " " + howManyTimes + " times");
            for (int i = 1; i <= howManyTimes; i++) {
                switch (direction) {
                    case "Left":
                        new SwipeRightToLeft().execute(requestRight);
                        break;
                    case "Right":
                        new SwipeLeftToRight().execute(requestLeft);
                        break;
                    case "Down":
                        new SwipeDown().execute(requestDown);
                        break;
                    case "Up":
                        new SwipeUp().execute(requestUp);
                        break;
                    default:
                        break;
                }
            }
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in Swipe", exception);
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

        sb.append("WebElement element = iosDriver.findElement(ELEMENT);\n");
        sb.append("Integer fromX = element.getLocation().getX();\n");
        sb.append("Integer fromY = element.getLocation().getY();\n");
        sb.append("for (int i = 1; i <= howManyTimes; i++) {\n");
        sb.append("	switch (direction) {\n");
        sb.append("		case \"Left\":\n");
        sb.append("			if (fromX > toX) {\n");
        sb.append("				TouchAction action = new TouchAction(iosDriver);\n");
        sb.append("				action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();\n");
        sb.append("			}\n");
        sb.append("			break;\n");
        sb.append("		case \"Right\":\n");
        sb.append("			if (fromX < toX) {\n");
        sb.append("				TouchAction action = new TouchAction(iosDriver);\n");
        sb.append("				action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();\n");
        sb.append("			}\n");
        sb.append("			break;\n");
        sb.append("		case \"Down\":\n");
        sb.append("			if (fromY < toY) {\n");
        sb.append("				TouchAction action = new TouchAction(iosDriver);\n");
        sb.append("				action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();\n");
        sb.append("			}\n");
        sb.append("			break;\n");
        sb.append("		case \"Up\":\n");
        sb.append("			if (fromY > toY) {\n");
        sb.append("				TouchAction action = new TouchAction(iosDriver);\n");
        sb.append("				action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();\n");
        sb.append("			}\n");
        sb.append("			break;\n");
        sb.append("		default:\n");
        sb.append("			break;\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("howManyTimes::10");
        params.add("direction::\"Left\"");

        return params;
    }

}
