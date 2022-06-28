package com.tyss.optimize.nlp.Nlp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.IDriver;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

@Component(value =  "NLP_OpenBrowser")
public class OpenBrowser implements Nlp{

	//NlpResponseModel nlpResponseModel =  new NlpResponseModel();
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();
		NlpResponseModel nlpResponseModel =  new NlpResponseModel();
		Map<String, Object> param = nlpRequestModel.getAttributes();

		IfFailed ifCheckPointIsFailed=null;
		if(param.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = param.get("ifCheckPointIsFailed").toString();
			ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		URL remoteAddress = null;
		try {
			if(param.get("hubURL")!=null) {
				remoteAddress = new URL(param.get("hubURL").toString());
			}
		} catch (MalformedURLException e) {
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
			nlpResponseModel.setStatus(CommonConstants.fail);
			String exceptionSimpleName=e.getClass().getSimpleName();
			ErrorInfo errorInfo= (ErrorInfo) SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
			nlpResponseModel.setErrorInfo(errorInfo);
			nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
		}
		try {
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities = (DesiredCapabilities) param.get("capabilities");
			IDriver driver = new com.tyss.optimize.data.models.dto.drivers.WebDriver((WebDriver)new RemoteWebDriver(remoteAddress, capabilities));
			nlpResponseModel.setDriver(driver);
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
			nlpResponseModel.setStatus(CommonConstants.pass);

		}catch(Exception e){
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
			nlpResponseModel.setStatus(CommonConstants.fail);
			String exceptionSimpleName=e.getClass().getSimpleName();
			ErrorInfo errorInfo= SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
			nlpResponseModel.setErrorInfo(errorInfo);
			nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
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
