package com.tyss.optimize.nlp.web.program.file;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@Component(value = "UploadFilePathUsingRobot")
public class UploadFilePathUsingRobot implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            WebDriver driver = (WebDriver) attributes.get("driver");
            String filePath = (String) attributes.get("filePath");
            WebElement element = (WebElement) attributes.get("element");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Uploading file");
            StringSelection stringSelection = new StringSelection(filePath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            Robot robot = null;
            Actions act = new Actions(driver);
            act.click(element).perform();
            robot = new Robot();
            robot.delay(7000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.delay(150);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            log.info("File is uploaded");
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in UploadFilePathUsingRobot ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("try {\n");
        sb.append(" WebElement element = driver.findElement(ELEMENT);\n");
        sb.append(" StringSelection stringSelection = new StringSelection(filePath);\n");
        sb.append(" Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);\n");
        sb.append(" Robot robot = null;\n");
        sb.append(" Actions act = new Actions(driver);\n");
        sb.append(" act.click(element).perform();\n");
        sb.append(" robot = new Robot();\n");
        sb.append(" robot.delay(7000);\n");
        sb.append(" robot.keyPress(KeyEvent.VK_ENTER);\n");
        sb.append(" robot.keyRelease(KeyEvent.VK_ENTER);\n");
        sb.append(" robot.keyPress(KeyEvent.VK_CONTROL);\n");
        sb.append(" robot.keyPress(KeyEvent.VK_V);\n");
        sb.append(" robot.keyRelease(KeyEvent.VK_V);\n");
        sb.append(" robot.keyRelease(KeyEvent.VK_CONTROL);\n");
        sb.append(" robot.delay(150);\n");
        sb.append(" robot.keyPress(KeyEvent.VK_ENTER);\n");
        sb.append(" robot.keyRelease(KeyEvent.VK_ENTER);\n");
        sb.append("}catch (Exception exception){}\n");

        return sb;
    }

    public java.util.List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("ELEMENT::By.id(\"randomId\")");
        params.add("filePath::\"/home/tyss/\"");

        return params;
    }
}
