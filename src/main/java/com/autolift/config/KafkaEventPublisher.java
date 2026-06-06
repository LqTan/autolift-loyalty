package com.autolift.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void publish(ApplicationEvent event, String topic, String key) {
    Map<String, Object> payload = new HashMap<>();
    payload.put("eventType", event.getClass().getSimpleName());
    payload.put("timestamp", event.getTimestamp());
    payload.put("source", event.getSource());
    payload.put("payload", event);

    sendMessage(topic, key, payload);
  }

  private void sendMessage(String topic, String key, Map<String, Object> payload) {
    kafkaTemplate
        .send(topic, key, payload)
        .whenComplete(
            (result, ex) -> {
              if (ex == null) {
                log.info(
                    "Event sent to Kafka: topic={}, key={}, partition={}, offset={}",
                    topic,
                    key,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
              } else {
                log.error("Failed to send event to Kafka: topic={}, key={}", topic, key, ex);
              }
            });
  }
}
