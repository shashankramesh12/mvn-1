package com.tyss.optimize.nlp.Nlp;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.tyss.optimize.nlp.util.*;

@Component( value = "GenerateRandomString" )
public class GenerateRandomString implements Nlp{

	//NlpResponseModel nlpResponseModel = new NlpResponseModel();
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();
		NlpResponseModel nlpResponseModel = new NlpResponseModel();
		Map<String, Object> attributes = nlpRequestModel.getAttributes();

		IfFailed ifCheckPointIsFailed=null;
		if(attributes.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
			ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		String randomString = null;
		int noAlphabets = (int) nlpRequestModel.getAttributes().get("numberOfAlphabets");
		try {
			randomString =	RandomStringUtils.randomAlphabetic(noAlphabets);
			Map<String, Object> nlpResponseAttributes = nlpResponseModel.getAttributes();
			nlpResponseAttributes.put("randomString", randomString);
			nlpResponseModel.setAttributes(nlpResponseAttributes);
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
			nlpResponseModel.setStatus(CommonConstants.pass);

		} catch (Exception e) {
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
