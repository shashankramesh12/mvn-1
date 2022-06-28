package com.tyss.optimize.nlp.mobile.program.touch;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.nlp.util.*;

@Slf4j
@Component(value = "MOB_SwipeDown")
public class SwipeDown implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String failMessage = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            IDriver driver = nlpRequestModel.getDriver();
            Integer fromX = (Integer) attributes.get("fromX");
            Integer fromY = (Integer) attributes.get("fromY");
            Integer toX = (Integer) attributes.get("toX");
            Integer toY = (Integer) attributes.get("toY");
            failMessage = (String) nlpRequestModel.getFailMessage();
            String passMessage = (String) nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            NlpRequestModel requestSwipe = new NlpRequestModel();
            requestSwipe.getAttributes().put("containsChildNlp", true);
            requestSwipe.setDriver(driver);
            requestSwipe.getAttributes().put("ifCheckPointIsFailed", ifCheckPointIsFailed);
            requestSwipe.getAttributes().put("fromX", fromX);
            requestSwipe.getAttributes().put("fromY", fromY);
            requestSwipe.getAttributes().put("toX", toX);
            requestSwipe.getAttributes().put("toY", toY);
            requestSwipe.setPassMessage("Swiped from *fromX* x *fromY* coordinate to *toX* x *toY* coordinate");
            requestSwipe.setFailMessage("Failed to swipe from *fromX* x *fromY* coordinate to *toX* x *toY* coordinate");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            log.info("Swiping from top to bottom");
            if (fromY < toY) {
                new Swipe().execute(requestSwipe);
                log.info("Swiped from top to bottom");
                nlpResponseModel.setMessage(passMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            } else {
                log.info("Entered coordinate value is wrong");
                log.error("Failed to swipe from top to bottom");
                nlpResponseModel.setMessage(failMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            }
        } catch (NlpException exception) {
            log.error("NLP_EXCEPTION in VerifyAttributeValue ", exception);
            String exceptionSimpleName = exception.getMessage();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        catch (Exception exception) {
            log.error("NLP_EXCEPTION in SwipeDown ", exception);
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

        sb.append("if (fromY < toY) {\n");
        sb.append("	TouchAction action = new TouchAction(androidDriver);\n");
        sb.append("	action.longPress(PointOption.point(fromX, fromY)).moveTo(PointOption.point(toX, toY)).release().perform();\n");
        sb.append("	System.out.println(\"Swiped from top to bottom\");\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(\"Entered coordinate value is wrong\");\n");
        sb.append("	System.out.println(\"Failed to swipe from top to bottom\");\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("fromX::10");
        params.add("fromY::20");
        params.add("toX::30");
        params.add("toY::40");

        return params;
    }

}
