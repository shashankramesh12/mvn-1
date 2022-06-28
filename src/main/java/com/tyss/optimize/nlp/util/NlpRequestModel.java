package com.tyss.optimize.nlp.util;


import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.data.models.dto.StorageInfo;
import groovy.transform.builder.Builder;
import lombok.Data;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class NlpRequestModel {

	IDriver driver;
	
	String url;
	
	Map<String, Object> attributes = new HashMap<>();
	
	DesiredCapabilities desiredCapabilities;
	
	String passMessage;
	
	String failMessage;

	StorageInfo storageInfo;

	String driverPath;
}
