package com.autolift.kafka.infrastructure.kafka;

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
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaConfig {

  public static final String VOUCHER_REDEEMED_TOPIC = "voucher.redeemed";
  public static final String LOYALTY_POINTS_ADDED_TOPIC = "loyalty.points-added";
  public static final String LOYALTY_POINTS_DEDUCTED_TOPIC = "loyalty.points-deducted";
  public static final String CAMPAIGN_ACTIVATED_TOPIC = "campaign.activated";

  public static final String VOUCHER_REDEEMED_DLT = "voucher.redeemed.dlt";
  public static final String LOYALTY_POINTS_ADDED_DLT = "loyalty.points-added.dlt";
  public static final String LOYALTY_POINTS_DEDUCTED_DLT = "loyalty.points-deducted.dlt";
  public static final String CAMPAIGN_ACTIVATED_DLT = "campaign.activated.dlt";

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, Object> producerFactory() {
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
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "autolift-group");
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.autolift.*");
    configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
    return new DefaultKafkaConsumerFactory<>(configProps);
  }

  @Bean
  public CommonErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
    DefaultErrorHandler errorHandler =
        new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
    errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
    return errorHandler;
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
      ConsumerFactory<String, Object> consumerFactory, CommonErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);
    factory.setConcurrency(3);
    factory.setCommonErrorHandler(errorHandler);
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

  @Bean
  public NewTopic voucherRedeemedDltTopic() {
    return TopicBuilder.name(VOUCHER_REDEEMED_DLT).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic loyaltyPointsAddedDltTopic() {
    return TopicBuilder.name(LOYALTY_POINTS_ADDED_DLT).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic loyaltyPointsDeductedDltTopic() {
    return TopicBuilder.name(LOYALTY_POINTS_DEDUCTED_DLT).partitions(3).replicas(1).build();
  }

  @Bean
  public NewTopic campaignActivatedDltTopic() {
    return TopicBuilder.name(CAMPAIGN_ACTIVATED_DLT).partitions(3).replicas(1).build();
  }
}
