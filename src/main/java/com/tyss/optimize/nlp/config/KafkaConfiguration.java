package com.tyss.optimize.nlp.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.core.env.Environment;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaConfiguration {

    @Autowired
    KafkaProperties kafkaProperties;

    @Autowired
    private Environment env;

    public Map<String, Object> producerConfig(){

        Map<String, Object> config = kafkaProperties.buildProducerProperties();
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, env.getProperty("max.in.flight.requests.per.connection"));
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, env.getProperty("enable.idempotent"));
        config.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, env.getProperty("max.block.ms"));
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, env.getProperty("max.request.size"));
        config.put(ProducerConfig.LINGER_MS_CONFIG, env.getProperty("linger.ms"));
        return config;
    }

    public Map<String, Object> consumerConfig(){

        Map<String, Object> config = kafkaProperties.buildProducerProperties();
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.SEND_BUFFER_CONFIG, env.getProperty("send-buffer-bytes"));
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, env.getProperty("reconnect-backoff-ms"));
        config.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, env.getProperty("reconnect-backoff-max-ms"));
        config.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, env.getProperty("retry-backoff-ms"));
        config.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, env.getProperty("partition-assignment-strategy"));
        config.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, env.getProperty("metadata-max-age-ms"));
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, env.getProperty("session-timeout-ms"));
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, env.getProperty("fetch-min-bytes"));
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, env.getProperty("max-poll-interval-ms"));
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, env.getProperty("fetch-max-bytes"));
        config.put(ConsumerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, env.getProperty("connection-max-idle-ms"));
        config.put(ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG, env.getProperty("exclude-internal-topics"));
        config.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, env.getProperty("receive-buffer-bytes"));
        config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, env.getProperty("request-timeout-ms"));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);
        return config;
    }
}
