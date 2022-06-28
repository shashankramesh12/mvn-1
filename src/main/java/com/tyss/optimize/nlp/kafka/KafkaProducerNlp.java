package com.tyss.optimize.nlp.kafka;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.config.KafkaConfiguration;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import org.apache.kafka.clients.producer.KafkaProducer;

@Slf4j
@Component(value = "KafkaProducer")
public class KafkaProducerNlp implements Nlp {

    @Autowired
    KafkaConfiguration kafkaConfiguration;

    @Override
    public NlpResponseModel execute(NlpRequestModel nlpRequestModel) throws NlpException {
        NlpResponseModel nlpResponseModel = new NlpResponseModel();
        IfFailed ifCheckPointIsFailed = null;
        Boolean containsChildNlp = false;
        Long startTime = System.currentTimeMillis();
        try {
            Map<String, Object> attributes = nlpRequestModel.getAttributes();
            containsChildNlp = (Boolean) attributes.get("containsChildNlp");
            String bootstrap_Server = attributes.get("bootstrapServer").toString();
            String topic = attributes.get("topic").toString();
            Object data = attributes.get("data");
            String key = Objects.nonNull(attributes.get("key")) ? attributes.get("key").toString() : null;
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            Properties properties = new Properties();
            Map<String, Object> kafkaConfig = kafkaConfiguration.producerConfig();
            kafkaConfig.entrySet().stream().forEach(config -> {
                properties.put(config.getKey(), config.getValue());
            });
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_Server);
            KafkaProducer<String, Object> producer = new KafkaProducer<String, Object>(properties);
            producer.send(new ProducerRecord<String, Object>(topic, key, data)).get();
            nlpResponseModel.setMessage(nlpRequestModel.getPassMessage());
            nlpResponseModel.setStatus(CommonConstants.pass);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in KafkaProducerNlp :", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(nlpRequestModel.getFailMessage().toString(), ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
            if(containsChildNlp) {
                throw new NlpException(exceptionSimpleName);
            }
        }
        Long endTime = System.currentTimeMillis();
        nlpResponseModel.setExecutionTime(endTime - startTime);
        return nlpResponseModel;
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
