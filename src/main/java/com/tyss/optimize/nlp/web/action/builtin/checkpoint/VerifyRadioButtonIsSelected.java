package com.tyss.optimize.nlp.web.action.builtin.checkpoint;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.nlp.util.*;
import com.tyss.optimize.nlp.web.action.natives.GetAttribute;
import com.tyss.optimize.nlp.web.action.natives.GetTagName;
import com.tyss.optimize.nlp.web.action.natives.IsSelected;
import com.tyss.optimize.nlp.web.webelement.WaitTillElementVisible;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyRadioButtonIsSelected")
public class VerifyRadioButtonIsSelected implements Nlp {

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
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            Long explicitTimeOut = (Long) attributes.get("explicitTimeOut");
            String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = (String) nlpRequestModel.getPassMessage();
            String failMessage = (String) nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestVisible = new NlpRequestModel();
            requestVisible.getAttributes().put("containsChildNlp", true);
            requestVisible.setDriver(driver);
            requestVisible.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestVisible.getAttributes().put("elementName", elementName);
            requestVisible.getAttributes().put("elementType", elementType);
            requestVisible.getAttributes().put("element", element);
            requestVisible.getAttributes().put("explicitTimeOut", explicitTimeOut);
            requestVisible.setPassMessage("Successfully waited till *elementName* *elementType* is visible");
            requestVisible.setFailMessage("Failed to wait till *elementName* *elementType* is visible");

            NlpRequestModel requestTagName = new NlpRequestModel();
            requestTagName.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestTagName.getAttributes().put("containsChildNlp", true);
            requestTagName.getAttributes().put("elementName", elementName);
            requestTagName.getAttributes().put("elementType", elementType);
            requestTagName.getAttributes().put("element", element);
            requestTagName.setPassMessage("The tag name of *elementName* *elementType* is *returnValue*");
            requestTagName.setFailMessage("Failed to capture tag name of *elementName* *elementType*");

            NlpRequestModel requestAttribute = new NlpRequestModel();
            requestAttribute.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestAttribute.getAttributes().put("containsChildNlp", true);
            requestAttribute.getAttributes().put("elementName", elementName);
            requestAttribute.getAttributes().put("elementType", elementType);
            requestAttribute.getAttributes().put("element", element);
            requestAttribute.getAttributes().put("attributeName", "type");
            requestAttribute.setPassMessage("Attribute *attributeName* value of *elementName* *elementType* is *returnValue*");
            requestAttribute.setFailMessage("Failed to capture attribute *attributeName* value of *elementName* *elementType*");

            NlpRequestModel requestSelected = new NlpRequestModel();
            requestSelected.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSelected.getAttributes().put("containsChildNlp", true);
            requestSelected.getAttributes().put("elementName", elementName);
            requestSelected.getAttributes().put("elementType", elementType);
            requestSelected.getAttributes().put("element", element);
            requestSelected.setPassMessage("*elementName* *elementType** is selected");
            requestSelected.setFailMessage("*elementName* *elementType* is not selected");

            modifiedFailMessage = failMessage.replace("*elementName*",modElementName);
            String modifiedPassMessage = passMessage.replace("*elementName*",modElementName);
            log.info("Verifying if " + elementName + " " + elementType + " is selected");
            new WaitTillElementVisible().execute(requestVisible);
            String tag = (String) ((NlpResponseModel) new GetTagName().execute(requestTagName)).getAttributes().get("tagName");
            String type = (String) ((NlpResponseModel) new GetAttribute().execute(requestAttribute)).getAttributes().get("value");
            if (!(tag.equalsIgnoreCase("input") && (type.equalsIgnoreCase("radio")))) {
                log.error("InvalidElementTypeException: Element should be radio to verify the 'Selected' status");
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else {
                Boolean actualSelectedStatus = (Boolean) ((NlpResponseModel) new IsSelected().execute(requestSelected)).getAttributes().get("selected");
                if (actualSelectedStatus) {
                    log.info(elementName + " " + elementType + " is selected");
                    nlpResponseModel.setMessage(modifiedPassMessage);
                    nlpResponseModel.setStatus(CommonConstants.pass);
                } else {
                    log.error(elementName + " " + elementType + " is not selected");
                    nlpResponseModel.setMessage(modifiedFailMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyRadioButtonIsSelected ", exception);
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
        sb.append("WebDriverWait wait = new WebDriverWait(driver, explicitTimeOut);\n");
        sb.append("wait.until(ExpectedConditions.visibilityOf(element));\n");
        sb.append("String tag = element.getTagName();\n");
        sb.append("String type = element.getAttribute(\"type\");\n");
        sb.append("if (!(tag.equalsIgnoreCase(\"input\") && (type.equalsIgnoreCase(\"radio\")))) {\n");
        sb.append("	System.out.println(\"InvalidElementTypeException: Element should be radio to verify the \'Selected\' status\");\n");
        sb.append("} else {\n");
        sb.append("	boolean actualSelectedStatus = element.isSelected();\n");
        sb.append("	if (actualSelectedStatus) {\n");
        sb.append("		System.out.println(\"The RadioButton is selected\");\n");
        sb.append("	} else {\n");
        sb.append("		System.out.println(\"The RadioButton is not selected\");\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("explicitTimeOut::20");

        return params;
    }
}
