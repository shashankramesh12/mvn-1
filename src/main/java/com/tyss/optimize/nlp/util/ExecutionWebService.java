package com.tyss.optimize.nlp.util;

import com.tyss.optimize.data.models.dto.webserviceworkbench.common.ResponseDto;
import com.tyss.optimize.nlp.config.FeignHttpsWebServiceConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@FeignClient(value = "executionWebService", url = "http://localhost:8112/optimize/v1/public/client/execute", configuration = FeignHttpsWebServiceConfig.class)
public interface ExecutionWebService {
    @RequestMapping(method = RequestMethod.POST, value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseDto executeWebserviceRequest(@PathVariable("body") String multiPartRequestBody) throws Exception;

}


