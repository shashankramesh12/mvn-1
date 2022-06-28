package com.tyss.optimize.nlp.util.storage;

import com.tyss.optimize.data.models.dto.StorageInfo;
import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Properties;

@Component("SharedDrive")
@Slf4j
public class SharedDriveStorageManager implements StorageManager {

    private SmbFile smbFile = null;

    @Override
    public InputStream getObject(StorageInfo storageInfo, String filePath) {
        createConnection(storageInfo, filePath);
        InputStream inputStream = null;
        try {
            inputStream = smbFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    @Override
    public boolean checkIfObjectExist(StorageInfo storageInfo, String filePath) throws SmbException {
        createConnection(storageInfo, filePath);
        return smbFile.exists();
    }

    @Override
    public void saveTheUpdatedObject(StorageInfo storageInfo, Workbook wb, String workbookPath) {
        createConnection(storageInfo, workbookPath);
        try {
            OutputStream fileOutputStream = new FileOutputStream("//" + storageInfo.getInputs().getBucketName() + "/" + storageInfo.getOutputs().getDirName() + "/" +  workbookPath);
            wb.write(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConnection(StorageInfo storageInfo, String filePath) {

        String username = null, password = null, domain = null;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(storageInfo.getInstalledDir() + "/configuration.json")) {
            //Read JSON file
            JSONObject obj = (JSONObject) jsonParser.parse(reader);
            username = (String) obj.get("username");
            password = (String) obj.get("password");
            domain = (String) obj.get("domain");
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        PropertyConfiguration configuration = null;
        try {
            String storagePath = "smb://" + storageInfo.getInputs().getBucketName() + filePath;
            Properties properties = new Properties();
            configuration = new PropertyConfiguration(properties);
            NtlmPasswordAuthenticator auth = new NtlmPasswordAuthenticator(domain, username, password);
            CIFSContext cif = new BaseContext(configuration).withCredentials(auth);
            smbFile = new SmbFile(storagePath, cif);
        } catch (MalformedURLException | CIFSException e) {
            e.printStackTrace();
        }
    }
}
