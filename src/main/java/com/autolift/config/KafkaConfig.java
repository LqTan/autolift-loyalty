package com.autolift.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
public class KafkaConfig {

  public static final String VOUCHER_REDEEMED_TOPIC = "voucher.redeemed";
  public static final String LOYALTY_POINTS_ADDED_TOPIC = "loyalty.points-added";
  public static final String LOYALTY_POINTS_DEDUCTED_TOPIC = "loyalty.points-deducted";
  public static final String CAMPAIGN_ACTIVATED_TOPIC = "campaign.activated";

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ConsumerFactory consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "autolift-group");
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.autolift.*");
    configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class.getName());
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(3);
    return factory;
  }

  @Bean
  public NewTopic voucherRedeemedTopic() {
    return TopicBuilder.name(VOUCHER_REDEEMED_TOPIC).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic loyaltyPointsAddedTopic() {
    return TopicBuilder.name(LOYALTY_POINTS_ADDED_TOPIC).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic loyaltyPointsDeductedTopic() {
    return TopicBuilder.name(LOYALTY_POINTS_DEDUCTED_TOPIC).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic campaignActivatedTopic() {
    return TopicBuilder.name(CAMPAIGN_ACTIVATED_TOPIC).partitions(3).replicas(1).build();
  }
}
