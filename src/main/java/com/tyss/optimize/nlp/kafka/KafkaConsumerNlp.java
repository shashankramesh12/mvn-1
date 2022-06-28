package com.tyss.optimize.nlp.kafka;

import com.tyss.optimize.common.util.CommonConstants;
import com.tyss.optimize.nlp.config.KafkaConfiguration;
import com.tyss.optimize.nlp.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component(value = "KafkaConsumer")
public class KafkaConsumerNlp implements Nlp {

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
            String groupId = attributes.get("groupId").toString();
            String passMessage = nlpRequestModel.getPassMessage();
            if (attributes.get("ifCheckPointIsFailed") != null) {
                String ifFailed = attributes.get("ifCheckPointIsFailed").toString();
                ifCheckPointIsFailed = IfFailed.valueOf(ifFailed);
            }
            Properties properties = new Properties();
            List<Object> listOfRecords = new ArrayList<>();
            Map<String, Object> kafkaConfig = kafkaConfiguration.consumerConfig();
            kafkaConfig.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            kafkaConfig.entrySet().stream().forEach(config -> {
                properties.put(config.getKey(), config.getValue());
            });
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_Server);
            DefaultKafkaConsumerFactory<String, Object> cf = new DefaultKafkaConsumerFactory(properties);
            KafkaConsumer consumer = (KafkaConsumer) cf.createConsumer();
            consumer.subscribe(Collections.singletonList(topic));
            ConsumerRecords<String, Object> records = consumer.poll(Duration.ofSeconds(10));
            for (ConsumerRecord<String, Object> record : records){
                log.info("Consumed Data:"+ record.offset()+ record.key()+ record.value());
                listOfRecords.add(record.value());
            }
            passMessage = passMessage.replace("*recordsCount*", String.valueOf(records.count()));
            consumer.commitAsync();
            consumer.close();
            nlpResponseModel.setMessage(passMessage);
            nlpResponseModel.setStatus(CommonConstants.pass);
            nlpResponseModel.getAttributes().put("records", listOfRecords);
        } catch (Exception exception) {
            log.error("NLP_EXCEPTION in KafkaConsumerNlp :", exception);
            String exceptionSimpleName = exception.getClass().getSimpleName();
            nlpResponseModel= ExceptionHandlingInfo.exceptionMessageHandler(nlpRequestModel.getFailMessage(), ifCheckPointIsFailed, exceptionSimpleName, exception.getStackTrace());
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
