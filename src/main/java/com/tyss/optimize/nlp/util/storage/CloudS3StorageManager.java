package com.tyss.optimize.nlp.util.storage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.tyss.optimize.data.models.dto.StorageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;


@Slf4j
@Component("CloudS3")
public class CloudS3StorageManager implements StorageManager {

    private AmazonS3 s3Client = null;

    @Override
    public InputStream getObject(StorageInfo storageInfo, String filePath) {
        createConnection(storageInfo.getInstalledDir());
        S3Object s3object = s3Client.getObject(storageInfo.getInputs().getBucketName(), filePath);
        return s3object.getObjectContent();
    }

    @Override
    public boolean checkIfObjectExist(StorageInfo storageInfo, String filePath) {
        createConnection(storageInfo.getInstalledDir());
        return s3Client.doesObjectExist(storageInfo.getInputs().getBucketName(), filePath);
    }

    @Override
    public void saveTheUpdatedObject(StorageInfo storageInfo, Workbook wb, String workbookPath) {
        createConnection(storageInfo.getInstalledDir());
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            wb.write(byteArrayOutputStream);
            ByteArrayInputStream bi = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            long contentLength = byteArrayOutputStream.toByteArray().length;

            ObjectMetadata objectMetaData = new ObjectMetadata();
            objectMetaData.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            objectMetaData.setContentLength(contentLength);

            s3Client.putObject(new PutObjectRequest(storageInfo.getInputs().getBucketName(), workbookPath, bi, objectMetaData));
            byteArrayOutputStream.close();
            bi.close();
        } catch (IOException e) {
            log.error("Exception while writing the data: " + e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
    }

    private void createConnection(String filePath) {
        String accessKey = null, secretKey = null;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(filePath + "/configuration.json")) {
            //Read JSON file
            JSONObject obj = (JSONObject) jsonParser.parse(reader);
            accessKey = (String) obj.get("accesskey");
            secretKey = (String) obj.get("secretkey");
        } catch (ParseException | IOException e) {
            log.error("Exception while creating connection for cloudS3: " + e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
        }
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1)
                .build();
    }
}
