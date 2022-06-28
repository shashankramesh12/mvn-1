package com.tyss.optimize.nlp.web.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "GetAllBrokenLinkCount")
public class GetAllBrokenLinkCount implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        int brokenCount = 0;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            String passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info(" Getting number of broken links");
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            List<WebElement> goodLinks = new ArrayList<>();

            for (int i = 0; i < allLinks.size(); i++) {
                WebElement we = allLinks.get(i);
                if (we.getAttribute("href") != null && (we.getAttribute("href").startsWith("http") || we.getAttribute("href").startsWith("https"))) {
                    goodLinks.add(we);
                }
            }
            for (int i = 0; i < goodLinks.size(); i++) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(goodLinks.get(i).getAttribute("href")).openConnection();
                    connection.connect();
                    String responseMsg = connection.getResponseMessage();
                    connection.disconnect();
                    log.info("Links : " + goodLinks.get(i).getAttribute("href") + "-->" + responseMsg);
                    if (!responseMsg.equals("OK")) {
                        brokenCount++;
                    }
                } catch (Exception e) {
                    log.info("Failed to open connection " + e);
                    nlpResponseModel.setMessage(failMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            }
            log.info("The number of broken links are " + brokenCount);
            nlpResponseModel.getAttributes().put("brokenCount", brokenCount);
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetAllBrokenLinkCount ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
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

        sb.append("int brokenCount = 0;\n");
        sb.append("List<WebElement> allLinks = driver.findElements(By.tagName(\"a\"));\n");
        sb.append("List<WebElement> goodLinks = new ArrayList<>();\n");
        sb.append("for (int i = 0; i < allLinks.size(); i++) {\n");
        sb.append("	WebElement we = allLinks.get(i);\n");
        sb.append("	if (we.getAttribute(\"href\") != null && (we.getAttribute(\"href\").startsWith(\"http\") || we.getAttribute(\"href\").startsWith(\"https\"))) {\n");
        sb.append("		goodLinks.add(we);\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("for (int i = 0; i < goodLinks.size(); i++) {\n");
        sb.append("	try {\n");
        sb.append("		HttpURLConnection connection = (HttpURLConnection) new URL(goodLinks.get(i).getAttribute(\"href\")).openConnection();\n");
        sb.append("		connection.connect();\n");
        sb.append("		String responseMsg = connection.getResponseMessage();\n");
        sb.append("		connection.disconnect();\n");
        sb.append("		System.out.println(\"Links : \" + goodLinks.get(i).getAttribute(\"href\") + \"-->\" + responseMsg);\n");
        sb.append("		if (!responseMsg.equals(\"OK\")) {\n");
        sb.append("			brokenCount++;\n");
        sb.append("		}\n");
        sb.append("	} catch (Exception e) {\n");
        sb.append("		System.out.println(\"Failed to open connection \" + e);\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
