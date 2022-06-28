package com.tyss.optimize.nlp.web.action.builtin.mouse;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "DoubleClickAtSpecifiedLocationWithInElement")
public class DoubleClickAtSpecifiedLocationWithInElement implements Nlp {

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
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            Integer xCoordinate = (Integer) attributes.get("xCoordinate");
            Integer yCoordinate = (Integer) attributes.get("yCoordinate");
            WebElement element = (WebElement) attributes.get("element");
			String [] elementSplit=elementName.split(":");
        	elementName=elementSplit[1];
        	elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            String modifiedPassMessage = passMessage.replace("*xCoordinate*", String.valueOf(xCoordinate))
                    .replace("*yCoordinate*", String.valueOf(yCoordinate)).replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            modifiedFailMessage = failMessage.replace("*xCoordinate*", String.valueOf(xCoordinate))
                    .replace("*yCoordinate*", String.valueOf(yCoordinate)).replace("*elementName*", elementName)
                    .replace("*elementType*", elementType);
            log.info("Double clicking on the location[x:" + xCoordinate + "\r\n " + "; y:" + yCoordinate + "] with in the element: " + elementName + " " + elementType);
            Actions actions = new Actions(driver);
            actions.moveToElement(element, xCoordinate, yCoordinate).doubleClick().build().perform();
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in DoubleClickAtSpecifiedLocationWithInElement ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("Actions actions = new Actions(driver);\n");
        sb.append("actions.moveToElement(element, xCoordinate, yCoordinate).doubleClick().build().perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.xpath(\"//h5[text()='Elements']\")");
        params.add("xCoordinate::20");
        params.add("yCoordinate::30");

        return params;
    }
}
