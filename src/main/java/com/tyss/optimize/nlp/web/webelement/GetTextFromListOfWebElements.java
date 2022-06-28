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
@Component(value = "GetTextFromListOfWebElements")
public class GetTextFromListOfWebElements implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        List<String> allText = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            List<WebElement> allElements = (List<WebElement>) attributes.get("allElements");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            ifCheckPointIsFailed = null;
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Fetching text from list of webElements");
            allText = new ArrayList<>();
            for (WebElement element : allElements) {
                String text = element.getText();
                log.info("Adding the text:" + text + " to ArrayList");
                allText.add(text);
                log.info("Successfully captured text from each webElements");
            }
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("allText", allText);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetTextFromListOfWebElements ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("List<String> allText = new ArrayList<>();\n");
        sb.append("List<WebElement> allElements = new ArrayList<>();\n");
        sb.append("WebElement element = driver.findElement(ALLELEMENTS);\n");
        sb.append("allElements.add(element);\n");
        sb.append("for (WebElement element : allElements) {\n");
        sb.append("	String text = element.getText();\n");
        sb.append("	System.out.println(\"Adding the text:\" + text + \" to ArrayList\");\n");
        sb.append("	allText.add(text);\n");
        sb.append("	System.out.println(\"Successfully captured text from each webElements\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.xpath(\"//h5[text()='Elements']\")::ALLELEMENTS");

        return params;
    }

}
