package com.tyss.optimize.nlp.ios.action.get;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.ios.action.natives.GetLocation;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "IOS_GetY")
public class GetY implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Integer yLocation = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String elementName = (String) attributes.get("elementName");
            String elementType = (String) attributes.get("elementType");
            WebElement element = (WebElement) attributes.get("element");
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
            NlpRequestModel requestLocation = new NlpRequestModel();
            requestLocation.getAttributes().put("containsChildNlp", true);
            requestLocation.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestLocation.getAttributes().put("elementName", elementName);
            requestLocation.getAttributes().put("elementType", elementType);
            requestLocation.getAttributes().put("element", element);
            requestLocation.setPassMessage("The location of *elementName* *elementType* is *returnValue*");
            requestLocation.setFailMessage("Failed to fetch location of *elementName* *elementType*");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Getting the y coordinate of " + elementName + " " + elementType);
            yLocation = (Integer) ((Point) ((NlpResponseModel) new GetLocation().execute(requestLocation)).getAttributes().get("location")).getY();
            log.info("Successfully fetched y coordinate of " + elementName + " " + elementType);
            nlpResponseModel.setMessage(modifiedPassMessage.replace("*returnValue*", String.valueOf(yLocation)));
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Throwable e) {
            log.error("NLP_EXCEPTION in GetY ", e);
            String exceptionSimpleName = e.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, e.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("yLocation", yLocation);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = iosDriver.findElement(ELEMENT);\n");
        sb.append("Integer yLocation = element.getLocation().getY();\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");

        return params;
    }

}
