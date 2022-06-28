package com.tyss.optimize.nlp.web.program.db.mongo;

import com.mongodb.gridfs.GridFSDBFile;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value = "PrintTestDataDocument")
public class PrintTestDataDocument implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        int length;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            GridFSDBFile imageForOutput = (GridFSDBFile) attributes.get("imageForOutput");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            log.info("Printing test data document");
            InputStream inputStream = imageForOutput.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            log.debug("Print TestData:" + result.toString(StandardCharsets.UTF_8.name()));
            log.info("Successfully fetched testdata document");
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in PrintTestDataDocument ", exception);
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

        sb.append("Object imageObject = imageForOutput;\n");
        sb.append("GridFSDBFile imageForOutputObject = (GridFSDBFile) imageObject;\n");
        sb.append("try {\n");
        sb.append("	InputStream inputStream = imageForOutputObject.getInputStream();\n");
        sb.append("	ByteArrayOutputStream result = new ByteArrayOutputStream();\n");
        sb.append("	byte[] buffer = new byte[1024];\n");
        sb.append("	int length;\n");
        sb.append("	while ((length = inputStream.read(buffer)) != -1) {\n");
        sb.append("		result.write(buffer, 0, length);\n");
        sb.append("	}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("imageForOutput::\"randomObject\"");

        return params;
    }
}
