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
@Component(value = "Builtin_DragAndDropToElement")
public class DragAndDropToElement implements Nlp {

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
            String toElementName = (String) attributes.get("toElementName");
            String toElementType = (String) attributes.get("toElementType");
            WebElement element = (WebElement) attributes.get("element");
            WebElement toElement = (WebElement) attributes.get("toElement");
			String [] elementSplit=elementName.split(":");
        	elementName=elementSplit[1];
        	elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*toElementName*", toElementName)
                    .replace("*toElementType*", toElementType);
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*toElementName*", toElementName)
                    .replace("*toElementType*", toElementType);
            log.info("Dragging the element " + elementName + " " + elementType + " and droping to the element " + toElementName + " " + toElementType);
            NlpRequestModel requestOptions = new NlpRequestModel();
            requestOptions.getAttributes().put("containsChildNlp", true);
            Actions actions = new Actions(driver);
            actions.dragAndDrop(element, toElement).perform();
            nlpResponseModel.setMessage(modifiedPassMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in DragAndDropToElement ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
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
        sb.append("WebElement toElement = driver.findElement(TOELEMENT);\n");
        sb.append("Actions actions = new Actions(driver);\n");
        sb.append("actions.dragAndDrop(element, toElement).perform();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.xpath(\"//h5[text()='Elements']\")");
        params.add("ELEMENT::By.xpath(\"//h6[text()='Elements']\")::TOELEMENT");

        return params;
    }
}
