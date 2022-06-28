package com.tyss.optimize.nlp.Nlp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;
import com.tyss.optimize.common.util.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.tyss.optimize.nlp.util.*;

@Component( value = "ElseIfCondition" )
@Slf4j
public class ElseIfCondition implements Nlp{
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();
		log.debug("inside execute method Start time", LocalDateTime.now());
		NlpResponseModel nlpResponseModel = new NlpResponseModel();
		Map<String, Object> attributes = nlpRequestModel.getAttributes();
		String value1 = (String) attributes.get("value1");
		String operator = (String) attributes.get("operator");
		String value2 = (String) attributes.get("value2");
		Boolean status = false;
		try {
			status = new Condition().condition(value1,operator,value2);
			nlpResponseModel.setLogicalConditionStatus(status);
			if (status) {
				nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
				nlpResponseModel.setStatus(CommonConstants.pass);
			} else {
				nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
				nlpResponseModel.setStatus(CommonConstants.fail);
			}
		}catch (EvalError exception) {
			nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
			nlpResponseModel.setStatus(CommonConstants.fail);
			nlpResponseModel.setLogicalConditionStatus(status);
			log.error("NLP_EXCEPTION in ElseIf", exception);
		}
		Long endTime=System.currentTimeMillis();
		log.debug("Inside execute method end time", LocalDateTime.now());
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
