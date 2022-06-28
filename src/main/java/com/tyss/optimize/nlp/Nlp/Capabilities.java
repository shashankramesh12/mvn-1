package com.tyss.optimize.nlp.Nlp;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component(value =  "Capabilities")
public class Capabilities implements Nlp {

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
		try {
			nlpResponseModel.setDesiredCapabilities(new DesiredCapabilities());
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
			nlpResponseModel.setStatus(CommonConstants.pass);
		}catch(Throwable e){
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
			String exceptionSimpleName=e.getClass().getSimpleName();
			ErrorInfo errorInfo= SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
			nlpResponseModel.setErrorInfo(errorInfo);
			nlpResponseModel.setStatus(CommonConstants.fail);
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
