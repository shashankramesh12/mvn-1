package com.tyss.optimize.nlp.util;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;

public class ExceptionHandlingInfo {

    public static NlpResponseModel exceptionMessageHandler(String modifiedFailMessage, IfFailed ifCheckPointIsFailed, String exceptionSimpleName, StackTraceElement[] stackTrace) {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        nlpResponseModel.setMessage(modifiedFailMessage);
        nlpResponseModel.setStatus(CommonConstants.fail);
        ErrorInfo errorInfo = SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName, stackTrace);
        nlpResponseModel.setErrorInfo(errorInfo);
        nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
        return  nlpResponseModel;
    }
}
