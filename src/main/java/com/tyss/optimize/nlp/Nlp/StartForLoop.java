package com.tyss.optimize.nlp.Nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;

@Component(value =  "StartForLoop")
public class StartForLoop implements Nlp{
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.nanoTime();
		NlpResponseModel nlpResponseModel =  new NlpResponseModel();
		Map<String, Object> attributes = nlpRequestModel.getAttributes();
		IfFailed ifCheckPointIsFailed=null;
		if(attributes.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
			 ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		if(attributes.get("LoopVariable")!=null && (attributes.get("LoopVariable") instanceof String)) {
			nlpResponseModel.setNoOfIterations(Integer.parseInt((String) attributes.get("LoopVariable")));
		}else if(attributes.get("LoopVariable")!=null && (attributes.get("LoopVariable") instanceof Integer)) {
			nlpResponseModel.setNoOfIterations((Integer) attributes.get("LoopVariable"));
		}
		nlpResponseModel.getAttributes().put("startIterationCount", 0);
		try {
			nlpResponseModel.setDesiredCapabilities(new DesiredCapabilities());
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
			nlpResponseModel.setStatus(CommonConstants.pass);	
		}catch(Throwable e){
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
			nlpResponseModel.setStatus(CommonConstants.fail);
			String exceptionSimpleName=e.getClass().getSimpleName();
			ErrorInfo errorInfo= SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
			nlpResponseModel.setErrorInfo(errorInfo);
			nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
		}
		Long endTime=System.nanoTime();
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
