package com.tyss.optimize.nlp.web.webelement;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "VerifyIfXAndYCoordinateOfElement")
public class VerifyIfXAndYCoordinateOfElement implements Nlp {

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
            WebElement element = (WebElement) attributes.get("element");
            String elementType = (String) attributes.get("elementType");
            Integer expectedXCoordinate = (Integer) attributes.get("xCoordinate");
            Integer expectedYCoordinate = (Integer) attributes.get("yCoordinate");
            String [] elementSplit=elementName.split(":");
            elementName=elementSplit[1];
            elementType=elementType.concat(" in "+elementSplit[0] + " page ");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying X and Y location of " + elementName + " " + elementType + " is " + "( " + expectedXCoordinate + ", " + expectedYCoordinate + " )" );
            String modifiedPassMessage = passMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*expectedXLocation*", String.valueOf(expectedXCoordinate)).replace("*expectedYLocation*", String.valueOf(expectedYCoordinate));
            modifiedFailMessage = failMessage.replace("*elementName*", elementName)
                    .replace("*elementType*", elementType).replace("*expectedXLocation*", String.valueOf(expectedXCoordinate)).replace("*expectedYLocation*", String.valueOf(expectedYCoordinate));
            int xCoordinate = element.getLocation().getX();
            int yCoordinate = element.getLocation().getY();
            if (xCoordinate == expectedXCoordinate && yCoordinate == expectedYCoordinate) {
                log.info("The X and Y location of " + elementName + " " + elementType + " is " + expectedXCoordinate + " and " + expectedYCoordinate + " respectively");
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.info("The X and Y location of " + elementName + " " + elementType + " is not " + expectedXCoordinate + " and " + expectedYCoordinate + " respectively");                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in VerifyIfXAndYCoordinateOfElement ", exception);
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

    @Override
    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("WebElement element = driver.findElement(ELEMENT);\n");
        sb.append("int actualXCoordinate = element.getLocation().getX();\n");
        sb.append("int actualYCoordinate = element.getLocation().getY();\n");
        sb.append("if (actualXCoordinate == xCoordinate && actualYCoordinate == yCoordinate) {\n");
        sb.append("	System.out.println(\"The X and Y location is \" + actualXCoordinate + \" and \" + actualYCoordinate + \" respectively\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"The X and Y location is not \" + actualXCoordinate + \" and \" + actualYCoordinate + \" respectively\");\n");
        sb.append("}\n");

        return sb;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("xCoordinate::20");
        params.add("yCoordinate::30");

        return params;
    }
}
