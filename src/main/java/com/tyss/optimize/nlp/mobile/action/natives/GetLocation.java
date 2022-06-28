package com.tyss.optimize.nlp.mobile.action.natives;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component(value =  "MOB_GetLocation")
public class GetLocation implements Nlp {

	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime = System.currentTimeMillis();
		NlpResponseModel nlpResponseModel = new NlpResponseModel();
		String passMessage = null, failMessage = null, modifiedPassMessage = null, modifiedFailMessage = null;
		IfFailed ifCheckPointIsFailed = null;
		Boolean containsChildNlp = false;
		Point location = null;
		try {
			Map<String, Object> attributes = nlpRequestModel.getAttributes();
			String elementName = (String) attributes.get("elementName");
			WebElement element = (WebElement) attributes.get("element");
			String elementType = (String) attributes.get("elementType");
			containsChildNlp = (Boolean) attributes.get("containsChildNlp");
			String [] elementSplit=elementName.split(":");
			elementName=elementSplit[1];
			elementType=elementType.concat(" in "+elementSplit[0] + " screen ");
			passMessage = nlpRequestModel.getPassMessage();
			failMessage = nlpRequestModel.getFailMessage();
			if(attributes.get("ifCheckPointIsFailed")!=null) {
				String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
				ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
			}
			log.info("Getting location of " + elementName + " " + elementType);
			modifiedPassMessage = passMessage.replace("*elementName*", elementName)
					.replace("*elementType*", elementType);
			modifiedFailMessage = failMessage.replace("*elementName*", elementName)
					.replace("*elementType*", elementType);
			location = element.getLocation();
			log.info("Successfully fetched location of " + elementName + " " + elementType);
			nlpResponseModel.setMessage(modifiedPassMessage.replace("*returnValue*", String.valueOf(location)));
			nlpResponseModel.setStatus(CommonConstants.pass);
			nlpResponseModel.getAttributes().put("location", location);
		} catch(Throwable exception) {
         	log.error("NLP_EXCEPTION in GetLocation ", exception);
			String exceptionSimpleName = exception.getClass().getSimpleName();
			if (containsChildNlp)
				throw new NlpException(exceptionSimpleName);
			nlpResponseModel = ExceptionHandlingInfo.exceptionMessageHandler(modifiedFailMessage, ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
		}
		Long endTime =  System.currentTimeMillis();
		nlpResponseModel.setExecutionTime(endTime-startTime);
		return nlpResponseModel;
	}

	public StringBuilder getTestCode() throws NlpException {
		StringBuilder sb = new StringBuilder();

		sb.append("WebElement element = androidDriver.findElement(ELEMENT);\n");
		sb.append("Point location = element.getLocation();\n");

		return sb;
	}

	public List<String> getTestParameters() throws NlpException {
		List<String> params = new ArrayList<>();

		params.add("ELEMENT::By.id(\"randomId\")");

		return params;
	}

}
