package com.tyss.optimize.nlp.web.program.browser;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component(value = "GetScreenshot")
public class GetScreenshot implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        File source = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Capturing screenshot of current page");
            if(isAlertPresent(driver)) {
                System.setProperty("java.awt.headless", "false");
                BufferedImage image = new Robot().createScreenCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                int randomNumber = ThreadLocalRandom.current().nextInt();
                source = new File("C:\\Users\\Admin\\AppData\\Local\\Temp\\screenshot+"+randomNumber+".png");
                ImageIO.write(image, "png", source);
            } else {
                source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            }
            log.debug("Location of screenshot " + source.getAbsolutePath());
            log.info("Successfully captured screenshot of page");
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*", String.valueOf(source)));
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetScreenshot ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        nlpResponseModel.getAttributes().put("source", source);
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public boolean isAlertPresent(WebDriver driver) {
        try {
            driver.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException exception) {
            return false;
        }
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("File source = null;\n");
        sb.append("boolean isAlertPresent = false;\n");
        sb.append("try {\n");
        sb.append("	driver.switchTo().alert();\n");
        sb.append("	isAlertPresent = true;\n");
        sb.append("}catch (NoAlertPresentException exception) {\n");
        sb.append("	isAlertPresent = false;\n");
        sb.append("}\n");
        sb.append("if(isAlertPresent) {\n");
        sb.append("	System.setProperty(\"java.awt.headless\", \"false\");\n");
        sb.append("	BufferedImage image = new Robot().createScreenCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));\n");
        sb.append("	int randomNumber = ThreadLocalRandom.current().nextInt();\n");
        sb.append("	source = new File(\"C:\\Users\\Admin\\AppData\\Local\\Temp\\screenshot+\"+randomNumber+\".png\");\n");
        sb.append("	ImageIO.write(image, \"png\", source);\n");
        sb.append("} else {\n");
        sb.append("	source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);\n");
        sb.append("}\n");

        return sb;
    }

    public java.util.List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }

}
