package com.tyss.optimize.nlp.Nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.*;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

@Component( value = "NavigateUrl" )
public class NavigateUrl implements Nlp {

	//NlpResponseModel nlpResponseModel =  new NlpResponseModel();
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();

		NlpResponseModel nlpResponseModel =  new NlpResponseModel();
		Map<String, Object> attributes = nlpRequestModel.getAttributes();
		IfFailed ifCheckPointIsFailed=null;
		if(attributes.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
			ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		String url = "";
		if(nlpRequestModel.getUrl() != null) {
			url = nlpRequestModel.getUrl();
		}else {
		//	Map<String, Object> attributes = nlpRequestModel.getAttributes();
			if(attributes.get("url")!=null) {
				url = attributes.get("url").toString();
			}
		}
		try {
			WebDriver webDriver = (WebDriver) nlpRequestModel.getDriver().getSpecificIDriver();
			webDriver.navigate().to(url);
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage().replace("*url*",url));
			nlpResponseModel.setStatus(CommonConstants.pass);

		}catch (Exception e){
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage().replace("*url*",url));
			nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
			String exceptionSimpleName=e.getClass().getSimpleName();
			ErrorInfo errorInfo= SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
			nlpResponseModel.setErrorInfo(errorInfo);
			nlpResponseModel.setStatus(CommonConstants.fail);
		}
		Long endTime=System.currentTimeMillis();
		nlpResponseModel.setExecutionTime(endTime-startTime);
		return nlpResponseModel;
	}

	public StringBuilder getTestCode() throws NlpException {
		StringBuilder sb = new StringBuilder();



		return sb;
	}

	public List<String> getTestParameters() throws NlpException {
		List<String> params = new ArrayList<>();


		return params;
	}

}
