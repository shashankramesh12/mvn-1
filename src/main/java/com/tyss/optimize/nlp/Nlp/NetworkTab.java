package com.tyss.optimize.nlp.Nlp;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.data.models.dto.results.ErrorInfo;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v97.network.Network;
import org.springframework.stereotype.Component;

import java.util.*;

@Component(value =  "NetworkTab")
@Slf4j
public class NetworkTab implements Nlp{
    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        ChromeDriver driver;
        DevTools chromeDevTools;
        System.setProperty("webdriver.chrome.driver","D:\\requiredFiles\\localnode\\localnode\\chromedriver.exe");
        Long startTime=System.currentTimeMillis();
        NlpResponseModel nlpResponseModel =  new NlpResponseModel();
        Map<String, Object> attributes = nlpRequestModel.getAttributes();
        IfFailed ifCheckPointIsFailed=null;
        if(attributes.get("ifCheckPointIsFailed")!=null) {
            String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
            ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
        }
        try {
            driver = new ChromeDriver();
            chromeDevTools = driver.getDevTools();
            chromeDevTools.createSession();
            chromeDevTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            //Map<RequestId, String> map = new HashMap<>();
            Map<String,Integer> collect=new HashMap<>();
            Map<String,Integer> gtm=new HashMap<>();
            Map<String,Integer> score=new HashMap<>();
            String url= "https://www.abplive.com/news";
            log.info("Before chromeDevTools ");
            chromeDevTools.addListener(Network.responseReceived(),responseReceived -> {
                if(responseReceived.getResponse().getUrl().contains("collect")) {
                    log.info("Collect Url: " + responseReceived.getResponse().getUrl() + "Response Code: " + responseReceived.getResponse().getStatus());
                    collect.put(responseReceived.getResponse().getUrl(),responseReceived.getResponse().getStatus());
                }
                else if(responseReceived.getResponse().getUrl().contains("GTM"))
                {
                    log.info("GTM Url: " + responseReceived.getResponse().getUrl() + "Response Code: " + responseReceived.getResponse().getStatus());
                    gtm.put(responseReceived.getResponse().getUrl(),responseReceived.getResponse().getStatus());
                }
                else if(responseReceived.getResponse().getUrl().contains("score")){
                    log.info("Score Url: " + responseReceived.getResponse().getUrl() + "Response Code: " + responseReceived.getResponse().getStatus());
                    score.put(responseReceived.getResponse().getUrl(),responseReceived.getResponse().getStatus());
                }
            });



            driver.get(url);
            log.info("score size "+score.size());
            log.info("collect size "+collect.size());
            log.info("gtm size "+gtm.size());
            chromeDevTools.send(Network.disable());
            driver.quit();
            nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
            nlpResponseModel.setStatus(CommonConstants.pass);
        }catch(Throwable e){
            nlpResponseModel.setMessage(nlpRequestModel.getFailMessage());
            String exceptionSimpleName=e.getClass().getSimpleName();
            ErrorInfo errorInfo= SeleniumExceptionMapper.getErrorInfo(exceptionSimpleName,e.getStackTrace());
            nlpResponseModel.setErrorInfo(errorInfo);
            nlpResponseModel.setStatus(CommonConstants.fail);
            nlpResponseModel.setIfCheckPointIsFailed(ifCheckPointIsFailed);
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
