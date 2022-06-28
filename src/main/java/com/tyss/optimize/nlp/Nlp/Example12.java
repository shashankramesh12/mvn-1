package com.tyss.optimize.nlp.Nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Component;

import io.github.bonigarcia.wdm.WebDriverManager;

@Component( value = "Example12" )
public class Example12 implements Nlp {

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
		String url = "";
		try {
			WebDriverManager.chromedriver().arch64().arch32().setup();
			WebDriver driver=new ChromeDriver();
			driver.navigate().to("https://www.google.com");
			//driver.close();
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage().replace("*url*",url));
			nlpResponseModel.setStatus(CommonConstants.pass);
		}catch(Exception e){
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage().replace("*url*",url));
			nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
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
