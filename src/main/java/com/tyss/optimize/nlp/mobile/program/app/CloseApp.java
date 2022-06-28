package com.tyss.optimize.nlp.mobile.program.app;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value =  "MOB_CloseApp")
public class CloseApp implements Nlp {

	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		NlpResponseModel nlpResponseModel =  new NlpResponseModel();
		IfFailed ifCheckPointIsFailed = null;
		String failMessage = null;
		Boolean containsChildNlp = false;
		Long startTime = System.currentTimeMillis();
		try
		{
		Map<String, Object> attributes = nlpRequestModel.getAttributes();
		AndroidDriver androidDriver=(AndroidDriver) nlpRequestModel.getDriver().getSpecificIDriver();
		failMessage = (String) nlpRequestModel.getFailMessage();
		String passMessage = (String) nlpRequestModel.getPassMessage();
		containsChildNlp = (Boolean) attributes.get("containsChildNlp");
		if(attributes.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
			ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		log.info("Closing the application");
			log.info("Application is closed");
			androidDriver.closeApp();
			nlpResponseModel.setMessage(passMessage);
			nlpResponseModel.setStatus(CommonConstants.pass);
		}
		 catch(Exception exception){
			log.error("NLP_EXCEPTION in CloseApp ", exception);
			 String exceptionSimpleName = exception.getClass().getSimpleName();
			 nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(failMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
			 if (containsChildNlp) {
				 throw new NlpException(exceptionSimpleName);
			 }
		}
		Long endTime = System.currentTimeMillis();
		nlpResponseModel.setExecutionTime(endTime-startTime);
		return nlpResponseModel;
	}

	public StringBuilder getTestCode() throws NlpException {
		StringBuilder sb = new StringBuilder();

		sb.append("androidDriver.closeApp();\n");

		return sb;
	}

	public List<String> getTestParameters() throws NlpException {
		List<String> params = new ArrayList<>();


		return params;
	}

}
