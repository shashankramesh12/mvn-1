package com.tyss.optimize.nlp.util.storage;


import com.tyss.optimize.data.models.dto.StorageInfo;
import jcifs.smb.SmbException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public interface StorageManager {

    InputStream getObject(StorageInfo storageInfo, String filePath);

    boolean checkIfObjectExist(StorageInfo storageInfo, String filePath) throws SmbException;

    void saveTheUpdatedObject(StorageInfo storageInfo, Workbook wb, String workbookPath);

}
