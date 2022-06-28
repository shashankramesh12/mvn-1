package com.tyss.optimize.nlp.Nlp;

import com.tyss.optimize.common.util.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tyss.optimize.nlp.util.*;

@Component( value = "EndIfCondition" )
@Slf4j
public class EndIfCondition implements Nlp{
	
	@Override
	public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
		Long startTime=System.currentTimeMillis();
		log.debug("inside execute method Start time", LocalDateTime.now());
		NlpResponseModel nlpResponseModel = new NlpResponseModel();
		Long endTime=System.currentTimeMillis();
		log.debug("inside execute method end time", LocalDateTime.now());
		nlpResponseModel.setExecutionTime(endTime-startTime);
		nlpResponseModel.setStatus(CommonConstants.pass);
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
