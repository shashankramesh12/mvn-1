package com.tyss.optimize.nlp.config;

import feign.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FeignHttpsWebServiceConfig {
    @Bean
    public Client feignWebserviceClient() {
        Client trustSSLSockets = new Client.Default(null, new NoopHostnameVerifier());
        log.info("feignClient called");
        return trustSSLSockets;
    }
}
