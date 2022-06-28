package com.tyss.optimize.nlp.Nlp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;
import com.tyss.optimize.nlp.util.*;

@Slf4j
@Component( value = "EnterInput" )
public class EnterInput implements Nlp{

	//NlpResponseModel nlpResponseModel =  new NlpResponseModel();
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();
		log.debug("inside execute method Start time", LocalDateTime.now());
		NlpResponseModel nlpResponseModel =  new NlpResponseModel();

		Map<String, Object> attributes = nlpRequestModel.getAttributes();
		WebElement element = (WebElement) attributes.get("element");
		String elementName=(String)attributes.get("elementName");
		String elementType=(String)attributes.get("elementType");

		String [] elementSplit=elementName.split(":");
		elementName=elementSplit[1];
		elementType=elementType.concat(" in "+elementSplit[0] + " page ");
		String input=(String)attributes.get("input");
		IfFailed ifCheckPointIsFailed=null;
		if(attributes.get("ifCheckPointIsFailed")!=null) {
			String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
			ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
		}
		try
			{if(element==null){
				throw new NoSuchElementException(null);
			}
		element.sendKeys(input);
			nlpResponseModel.setMessage(nlpRequestModel.getPassMessage().replace("*input*",input).replace("*elementName*",elementName).replace("*elementType*",elementType));
			nlpResponseModel.setStatus(CommonConstants.pass);
		}
		catch(Exception e)
		{
			log.error("NLP_EXCEPTION in EnterInput ", e);
			log.error("Exception in step execution: "+e.getMessage() + System.lineSeparator() + Arrays.toString(e.getStackTrace()));
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage().replace("*input*",input).replace("*elementName*",elementName).replace("*elementType*",elementType));
			String exceptionSimpleName=e.getClass().getSimpleName();
			ErrorInfo errorInfo=  SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
			nlpResponseModel.setErrorInfo(errorInfo);
			nlpResponseModel.setStatus(CommonConstants.fail);
			nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
		}
		Long endTime=System.currentTimeMillis();
		log.debug("inside execute method end time", LocalDateTime.now());
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
