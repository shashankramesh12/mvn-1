package com.tyss.optimize.nlp.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface Nlp {
	
	NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException;

	StringBuilder getTestCode() throws NlpException;

	List<String> getTestParameters() throws NlpException;
}
