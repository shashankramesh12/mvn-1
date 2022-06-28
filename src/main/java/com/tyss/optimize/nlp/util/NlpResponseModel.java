package com.tyss.optimize.nlp.util;

import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.data.models.dto.webserviceworkbench.APIExecutionResponseDto;
import com.tyss.optimize.data.models.dto.webserviceworkbench.ApiRequestDto;
import lombok.Data;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.Map;

@Data
public class NlpResponseModel {

	IDriver driver;
	
	String url;
	
	Map<String, Object> attributes = new HashMap<String, Object>();
	
	DesiredCapabilities desiredCapabilities;
	
	String message;
	
	IfFailed ifCheckPointIsFailed;
	
	String status;
	
	Long executionTime;
	
	ErrorInfo errorInfo;
	
	NlpResponseEnum nlp;
	
	String returnValue;
	
	Boolean logicalConditionStatus;
	
	Integer noOfIterations = 0;
	ApiRequestDto webServiceRequest;
	APIExecutionResponseDto webserviceResponse;
	Boolean isWebServiceRequest=Boolean.FALSE;
	
}