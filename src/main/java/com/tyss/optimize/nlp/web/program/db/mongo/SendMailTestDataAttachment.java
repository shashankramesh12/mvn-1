package com.tyss.optimize.nlp.web.program.db.mongo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.gridfs.GridFSDBFile;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component(value = "SendMailTestDataAttachment")
public class SendMailTestDataAttachment implements Nlp {

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        Long startTime = System.currentTimeMillis();
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        String passMessage, failMessage = null;
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            String projectName = (String) attributes.get("projectName");
            String subject = (String) attributes.get("subject");
            String toEmail = (String) attributes.get("toEmail");
            String content = (String) attributes.get("content");
            String fileType = (String) attributes.get("fileType");
            GridFSDBFile imageForOutput = (GridFSDBFile) attributes.get("imageForOutput");
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            passMessage = nlpRequestModel.getPassMessage();
            failMessage = nlpRequestModel.getFailMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            InputStream inputStream = imageForOutput.getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            imageForOutput.writeTo(System.getProperty("user.dir") + File.separator + projectName + "_" + imageForOutput.getFilename() + fileType);
            JSONObject input = new JSONObject();
            input.put("subject", subject);
            input.put("toEmail", toEmail);
            input.put("content", content);
            sendMailUtil(input.toJSONString(), System.getProperty("user.dir") + File.separator + projectName + "_" + imageForOutput.getFilename() + fileType);
            File file = new File(System.getProperty("user.dir") + File.separator + projectName + "_" + imageForOutput.getFilename() + fileType);
            if (file.delete()) {
                log.debug(System.getProperty("user.dir") + File.separator + projectName + "_" + imageForOutput.getFilename() + fileType + " File deleted successfully");
            }
            log.debug("Print TestData:" + result.toString(StandardCharsets.UTF_8.name()));
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in SendMailTestDataAttachment ", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            if (containsChildNlp)
                throw new NlpException(exceptionSimpleName);
            nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime((endTime - startTime));
        return nlpResponseModel;
    }

    private void sendMailUtil(String inputJSONString, String filePath) {
        JsonObject jsonObject = new JsonParser().parse(inputJSONString).getAsJsonObject();
        String username = "support.testoptimize@testyantra.com";
        String password = "Support!#%531";
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", 465);
        //props.put("mail.smtp.ssl.checkserveridentity", true);
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));

            StringJoiner toString = new StringJoiner(" ,");
            JsonElement jsonElement = jsonObject.get("toEmail");
            if (jsonElement instanceof JsonArray) {
                JsonArray asJsonArray = jsonElement.getAsJsonArray();
                for (int i = 0; i < asJsonArray.size(); i++) {
                    toString.add(asJsonArray.get(i).getAsString());
                }
            } else {
                toString.add(jsonElement.getAsString());
            }
            String[] recipientList = toString.toString().split(",");
            InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
            int counter = 0;
            for (String recipient : recipientList) {
                recipientAddress[counter] = new InternetAddress(recipient.trim());
                counter++;
            }
            message.setRecipients(Message.RecipientType.TO, recipientAddress);
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toString.toString()));
            message.setSubject(jsonObject.get("subject").getAsString());
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            // Now set the actual message
            messageBodyPart.setText(jsonObject.get("content").getAsString());
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            //String filename = System.getProperty(USERDIR) + File.separator+file.getOriginalFilename();
            DataSource source = new FileDataSource(filePath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filePath);
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            log.error(e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
    }

    public StringBuilder getTestCode() throws NlpException {
        StringBuilder sb = new StringBuilder();

        sb.append("Object imageObject = imageForOutput;\n");
        sb.append("GridFSDBFile imageForOutputObject = (GridFSDBFile) imageObject;\n");
        sb.append("try {\n");
        sb.append("InputStream inputStream = imageForOutputObject.getInputStream();\n");
        sb.append("ByteArrayOutputStream result = new ByteArrayOutputStream();\n");
        sb.append("byte[] buffer = new byte[1024];\n");
        sb.append("int length;\n");
        sb.append("while ((length = inputStream.read(buffer)) != -1) {\n");
        sb.append("	result.write(buffer, 0, length);\n");
        sb.append("}\n");
        sb.append("String filePath = System.getProperty(\"user.dir\") + File.separator + projectName + \"_\" + imageForOutputObject.getFilename() + fileType;\n");
        sb.append("imageForOutputObject.writeTo(filePath);\n");
        sb.append("String username = \"support.testoptimize@testyantra.com\";\n");
        sb.append("String password = \"Support!#%531\";\n");
        sb.append("Properties prop = new Properties();\n");
        sb.append("prop.put(\"mail.smtp.host\", \"smtp.gmail.com\");\n");
        sb.append("prop.put(\"mail.smtp.socketFactory.port\", \"465\");\n");
        sb.append("prop.put(\"mail.smtp.socketFactory.class\", \"javax.net.ssl.SSLSocketFactory\");\n");
        sb.append("prop.put(\"mail.smtp.auth\", \"true\");\n");
        sb.append("prop.put(\"mail.smtp.port\", 465);\n");
        sb.append("Session session = Session.getInstance(prop, new Authenticator() {\n");
        sb.append("	@Override\n");
        sb.append("	protected PasswordAuthentication getPasswordAuthentication() {\n");
        sb.append("		return new PasswordAuthentication(username, password);\n");
        sb.append("	}\n");
        sb.append("});\n");
        sb.append("try {\n");
        sb.append("	Message message = new MimeMessage(session);\n");
        sb.append("	message.setFrom(new InternetAddress(username));\n");
        sb.append("	String[] recipientList = toEmail.toString().split(\",\");\n");
        sb.append("	InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];\n");
        sb.append("	int counter = 0;\n");
        sb.append("	for (String recipient : recipientList) {\n");
        sb.append("		recipientAddress[counter] = new InternetAddress(recipient.trim());\n");
        sb.append("		counter++;\n");
        sb.append("	}\n");
        sb.append("	message.setRecipients(Message.RecipientType.TO, recipientAddress);\n");
        sb.append("	message.setRecipients(Message.RecipientType.TO,\n");
        sb.append("			InternetAddress.parse(toEmail.toString()));\n");
        sb.append("	message.setSubject(subject);\n");
        sb.append("	// Create the message part\n");
        sb.append("	BodyPart messageBodyPart = new MimeBodyPart();\n");
        sb.append("	// Now set the actual message\n");
        sb.append("	messageBodyPart.setText(content);\n");
        sb.append("	// Create a multipar message\n");
        sb.append("	Multipart multipart = new MimeMultipart();\n");
        sb.append("	// Set text message part\n");
        sb.append("	multipart.addBodyPart(messageBodyPart);\n");
        sb.append("	// Part two is attachment\n");
        sb.append("	messageBodyPart = new MimeBodyPart();\n");
        sb.append("	//String filename = System.getProperty(USERDIR) + File.separator+file.getOriginalFilename();\n");
        sb.append("	DataSource source = new FileDataSource(filePath);\n");
        sb.append("	messageBodyPart.setDataHandler(new DataHandler(source));\n");
        sb.append("	messageBodyPart.setFileName(filePath);\n");
        sb.append("	multipart.addBodyPart(messageBodyPart);\n");
        sb.append("	message.setContent(multipart);\n");
        sb.append("	Transport.send(message);\n");
        sb.append("} catch (Exception e) {\n");
        sb.append("	System.out.println(e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));\n");
        sb.append("}\n");
        sb.append("File file = new File(fileName);\n");
        sb.append("if (file.delete()) {\n");
        sb.append("	System.out.println(fileName + \" File deleted successfully\");\n");
        sb.append("}\n");
        sb.append("} catch (Exception exception) {}\n");

        return sb;
    }

    public List<String> getTestParameters() throws NlpException {
        List<String> params = new ArrayList<>();

        params.add("projectName::\"xyz\"");
        params.add("subject::\"ABCD\"");
        params.add("toEmail::\"abc@xyz.com\"");
        params.add("content::\"xyz abc\"");
        params.add("fileType::\"xyz\"");
        params.add("imageForOutput::\"randomObject\"");

        return params;
    }
}
