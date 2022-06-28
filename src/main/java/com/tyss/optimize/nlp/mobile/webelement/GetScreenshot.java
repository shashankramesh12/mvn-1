package com.tyss.optimize.nlp.mobile.webelement;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
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
@Component(value = "MOB_GetScreenshot")
public class GetScreenshot implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String failMessage = null, passMessage = null;
        int height = 0;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        File source = null;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            AndroidDriver androidDriver = (AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            failMessage = (String) nlpRequestModel.getFailMessage();
            passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Capturing screenshot of current page");
            if(isAlertPresent(androidDriver)) {
                System.setProperty("java.awt.headless", "false");
                BufferedImage image = new Robot().createScreenCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                int randomNumber = ThreadLocalRandom.current().nextInt();
                source = new File("C:\\Users\\Admin\\AppData\\Local\\Temp\\screenshot+"+randomNumber+".png");
                ImageIO.write(image, "png", source);
            } else {
                source = ((TakesScreenshot) androidDriver).getScreenshotAs(OutputType.FILE);
            }
            log.debug("Location of screenshot " + source.getAbsolutePath());
            log.info("Successfully captured screenshot of page");
            nlpResponseModel.setMessage(passMessage.replace("*returnValue*", String.valueOf(source)));
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("source", source);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in GetScreenshot", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
    }

    public boolean isAlertPresent(AndroidDriver androidDriver) {
        try {
            androidDriver.switchTo().alert();
            return true;
        }
        catch (NoAlertPresentException exception) {
            return false;
        }
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("boolean flag = false;\n");
        sb.append("File source = null;\n");
        sb.append("try{\n");
        sb.append("  androidDriver.switchTo().alert();\n");
        sb.append("  flag = true;\n");
        sb.append("}catch (NoAlertPresentException exception) {\n");
        sb.append("  flag = false;\n");
        sb.append("}\n");
        sb.append("if(flag) {\n");
        sb.append("  System.setProperty(\"java.awt.headless\", \"false\");\n");
        sb.append("  BufferedImage image = new Robot().createScreenCapture(new java.awt.Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));\n");
        sb.append("  int randomNumber = ThreadLocalRandom.current().nextInt();\n");
        sb.append("  source = new File(\"C:/Users/Admin/AppData/Local/Temp/screenshot+\"+randomNumber+\".png\");\n");
        sb.append("  ImageIO.write(image, \"png\", source);\n");
        sb.append("} else {\n");
        sb.append("  source = ((TakesScreenshot) androidDriver).getScreenshotAs(OutputType.FILE);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();


        return params;
    }


}
