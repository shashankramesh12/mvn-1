package com.tyss.optimize.nlp.Nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

@Component(value =  "NLP_SetBrowserName")
public class SetBrowserName implements Nlp{

	//NlpResponseModel nlpResponseModel =  new NlpResponseModel();
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();
		NlpResponseModel nlpResponseModel =  new NlpResponseModel();

		Map<String, Object> paramData = nlpRequestModel.getAttributes();
		IfFailed ifCheckPointIsFailed=null;
		if(paramData.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = paramData.get("ifCheckPointIsFailed").toString();
			ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		DesiredCapabilities d = new DesiredCapabilities();
		String browserName= (String) paramData.get("browserName");
		try {
			d = (DesiredCapabilities) paramData.get("desiredCapabilities");
			d.setBrowserName(browserName);
			nlpResponseModel.setDesiredCapabilities(d);
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage().replace("*browserName*",browserName));
			nlpResponseModel.setStatus(CommonConstants.pass);

		}catch(Throwable e){
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage().replace("*browserName",browserName));
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
