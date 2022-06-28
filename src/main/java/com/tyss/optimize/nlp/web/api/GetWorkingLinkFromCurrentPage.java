package com.tyss.optimize.nlp.web.api;

import com.tyss.optimize.nlp.util.Nlp;
import com.tyss.optimize.nlp.util.NlpException;
import com.tyss.optimize.nlp.util.NlpRequestModel;
import com.tyss.optimize.nlp.util.NlpResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component(value = "GetWorkingLinkFromCurrentPage")
public class GetWorkingLinkFromCurrentPage implements Nlp {
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        return null;
    }

    @Override
    public StringBuilder getTestCode() throws NlpException {
        return null;
    }

    @Override
    public List<String> getTestParameters() throws NlpException {
        return null;
    }
}
