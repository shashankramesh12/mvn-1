package com.tyss.optimize.nlp.mobile.action.get;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.mobile.action.natives.GetLocation;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "MOB_GetX")
public class GetX implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage = null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Integer xLocation = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String [] elementSplit=elementName.split(":");
            String modElementName=elementSplit[1];
            String modElementType=elementType.concat(" in "+elementSplit[0] + " screen ");
		
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            modifiedPassMessage = passMessage.replace("*elementName*", modElementName).replace("*elementType*", modElementType);
            modifiedFailMessage = failMessage.replace("*elementName*", modElementName).replace("*elementType*", modElementType);
            NlpRequestModel requestLocation = new NlpRequestModel();
            requestLocation.getAttributes().put("containsChildNlp", true);
            requestLocation.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestLocation.getAttributes().put("elementName", elementName);
            requestLocation.getAttributes().put("elementType", elementType);
            requestLocation.getAttributes().put("element", element);
            requestLocation.setPassMessage("The location of *elementName* *elementType* is *returnValue*");
            requestLocation.setFailMessage("Failed to fetch location of *elementName* *elementType*");
            log.info("Getting the x coordinate of " + elementName + " " + elementType);
            xLocation = ((Point) ((NlpResponseModel) new GetLocation().execute(requestLocation)).getAttributes().get("location")).getX();
            log.info("Successfully fetched x coordinate of " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage.replace("*returnValue*", xLocation.toString()));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("xLocation", xLocation);
        } catch (NlpException e) {
            log.error("NLP_EXCEPTION in GetX ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        } catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetX ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = androidDriver.findElement(ELEMENT);\n");
        sb.append("Integer xLocation = element.getLocation().getX();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");

        return params;
    }

}
