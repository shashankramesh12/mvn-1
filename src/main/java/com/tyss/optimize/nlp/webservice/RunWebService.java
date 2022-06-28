package com.tyss.optimize.nlp.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.data.models.dto.webserviceworkbench.APIExecutionResponseDto;
import com.tyss.optimize.data.models.dto.webserviceworkbench.ApiClientExecutionResponseDto;
import com.tyss.optimize.data.models.dto.webserviceworkbench.ApiRequestDto;
import com.tyss.optimize.data.models.dto.webserviceworkbench.common.ResponseDto;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component(value = "RunWebService")
public class RunWebService implements Nlp {
    @Autowired
    private ExecutionWebService executionWebService;

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException  {

        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        Long startTime = System.currentTimeMillis();
        try {
        log.info("now running the webservice request from client");
        ResponseDto successResponse = null;
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        String body = (String) attributes.get("webServiceRequest");

        if (attributes.get("ifCheckPointIsFailed") != null) {
            String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
            ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
        }
            ApiRequestDto apiRequestDtoOriginal = new ObjectMapper().readValue(body, ApiRequestDto.class);
            attributes.remove("webServiceRequest");
            attributes.remove("ifCheckPointIsFailed");
            apiRequestDtoOriginal.setClientVariableMap(attributes);
            body=new ObjectMapper().writeValueAsString(apiRequestDtoOriginal);
            successResponse = executionWebService.executeWebserviceRequest(body);
            APIExecutionResponseDto apiExecutionResponseDto  = new APIExecutionResponseDto();
            apiExecutionResponseDto = new ObjectMapper().convertValue(successResponse.getSuccess(), APIExecutionResponseDto.class);
            ApiRequestDto apiRequestDto = new ApiRequestDto();
                apiRequestDto = new ObjectMapper().readValue(body, ApiRequestDto.class);
            nlpResponseModel.setWebServiceRequest(apiRequestDto);
           // ApiClientExecutionResponseDto res  = (ApiClientExecutionResponseDto) successResponse.getSuccess();
            if(Objects.nonNull(apiExecutionResponseDto.getAssertionResult()) &&    apiExecutionResponseDto.getAssertionResult().getStatus().equalsIgnoreCase("SUCCESS")){
                nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
                nlpResponseModel.setStatus(CommonConstants.pass);
            }else{
                nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
                nlpResponseModel.setStatus(CommonConstants.fail);
            }
            nlpResponseModel.setWebserviceResponse(apiExecutionResponseDto);
            nlpResponseModel.setIsWebServiceRequest(Boolean.TRUE);
            nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
        } catch (Exception exception) {
            log.error("Exception occurred while executing webservice request",exception);
            nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
            nlpResponseModel.setStatus(CommonConstants.fail);
            String exceptionSimpleName=exception.getClass().getSimpleName();
            ErrorInfo errorInfo= SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName, exception.getStackTrace());
            nlpResponseModel.setErrorInfo(errorInfo);
            nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
            nlpResponseModel.setWebServiceRequest(null);
            nlpResponseModel.setWebserviceResponse(null);
            nlpResponseModel.setIsWebServiceRequest(Boolean.TRUE);
        }
        long endTime = System.currentTimeMillis();
        log.info("webservice request from client executed successfully");
        nlpResponseModel.setExecutionTime((endTime - startTime));
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
