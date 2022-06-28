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
@Component(value = "GetAllBrokenLinks")
public class GetAllBrokenLinks implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        List<String> brokenLinks = new ArrayList<>();
        Boolean containsChildNlp = false;
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
            log.info(" Getting all broken links");
            List<WebElement> goodLinks = new ArrayList<>();
            String url = "";
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
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
                    url = goodLinks.get(i).getAttribute("href");
                    if (!responseMsg.equals("OK")) {
                        brokenLinks.add(url);
                    }
                } catch (Exception e) {
                    log.info("Failed to open connection \n" + e);
                    nlpResponseModel.setMessage(failMessage);
                    nlpResponseModel.setStatus(CommonConstants.fail);
                    nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
                }
            }
            log.info("Successfully captured broken links");
            nlpResponseModel.getAttributes().put("brokenLinks", brokenLinks.toString());
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetAllBrokenLinks ", exception);
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

        sb.append("List<String> brokenLinks = new ArrayList<>();\n");
        sb.append("List<WebElement> goodLinks = new ArrayList<>();\n");
        sb.append("String url = \"\";\n");
        sb.append("List<WebElement> allLinks = driver.findElements(By.tagName(\"a\"));\n");
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
        sb.append("		url = goodLinks.get(i).getAttribute(\"href\");\n");
        sb.append("		if (!responseMsg.equals(\"OK\")) {\n");
        sb.append("			brokenLinks.add(url);\n");
        sb.append("		}\n");
        sb.append("	} catch (Exception e) {\n");
        sb.append("		System.out.println(\"Failed to open connection \n\" + e);\n");
        sb.append("	}\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
