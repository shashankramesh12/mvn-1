package com.tyss.optimize.nlp.web.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "CompareImages")
public class CompareImages implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        String modifiedFailMessage = null;
        Boolean containsChildNlp = false;

        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String imageFileType = (String) attributes.get("imageFileType");
            String imageBasePath = (String) attributes.get("imageBasePath");
            String imageFileNameSource = (String) attributes.get("imageFileNameSource");
            String imageFileNameTarget = (String) attributes.get("imageFileNameTarget");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String passMessage = nlpRequestModel.getPassMessage();
            String failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Verifying if " + imageFileNameSource + " matches " + imageFileNameTarget);
            String modifiedPassMessage = passMessage.replace("*imageFileNameSource*", imageFileNameSource)
                    .replace("*imageFileNameTarget*", imageFileNameTarget);
            modifiedFailMessage = failMessage.replace("*imageFileNameSource*", imageFileNameSource)
                    .replace("*imageFileNameTarget*", imageFileNameTarget);
            BufferedImage imgA = ImageIO.read(new File(imageBasePath + imageFileNameSource + "." + imageFileType));
            BufferedImage imgB = ImageIO.read(new File(imageBasePath + imageFileNameTarget + "." + imageFileType));
            boolean imageComparisonResult = bufferedImagesEqual(imgA, imgB);
            log.info("Comparing screenshots from BasePath: " + imageBasePath + " and File Names : " + imageFileNameSource + "," + imageFileNameTarget + ": is :" + String.valueOf(imageComparisonResult));

            if (!imageComparisonResult) {
                log.info(imageFileNameSource + " did not match with " + imageFileNameTarget);
                nlpResponseModel.setMessage(modifiedFailMessage);
                nlpResponseModel.setStatus(CommonConstants.fail);
                nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            } else {
                log.info(imageFileNameSource + " matched with " + imageFileNameTarget);
                nlpResponseModel.setMessage(modifiedPassMessage);
                nlpResponseModel.setStatus(CommonConstants.pass);
            }
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in CompareImages ", exception);
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

    private boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (!(img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight())) {
            return false;
        }
        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y))
                    return false;
            }
        }
        return true;
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("BufferedImage img1 = ImageIO.read(new File(imageBasePath + imageFileNameSource + \".\" + imageFileType));\n");
        sb.append("BufferedImage img2 = ImageIO.read(new File(imageBasePath + imageFileNameTarget + \".\" + imageFileType));\n");
        sb.append("boolean imageComparisonResult = true;\n");
        sb.append("if (!(img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight())) {\n");
        sb.append("	imageComparisonResult = false;\n");
        sb.append("}\n");
        sb.append("for (int x = 0; x < img1.getWidth(); x++) {\n");
        sb.append("	for (int y = 0; y < img1.getHeight(); y++) {\n");
        sb.append("		if (img1.getRGB(x, y) != img2.getRGB(x, y))\n");
        sb.append("			imageComparisonResult = false;\n");
        sb.append("	}\n");
        sb.append("}\n");
        sb.append("if (!imageComparisonResult) {\n");
        sb.append("	System.out.println(imageFileNameSource + \" did not match with \" + imageFileNameTarget);\n");
        sb.append("} else {\n");
        sb.append("	System.out.println(imageFileNameSource + \" matched with \" + imageFileNameTarget);\n");
        sb.append("}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("imageBasePath::\"/home/tyss/images/\"");
        params.add("imageFileType::\"jpg\"");
        params.add("imageFileNameSource::\"Image1\"");
        params.add("imageFileNameTarget::\"Image2\"");

        return params;
    }

}
