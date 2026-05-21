package com.autolift.ml.infrastructure.batch;

import com.autolift.customer.domain.model.Customer;
import com.autolift.customer.domain.valueobject.CustomerId;
import com.autolift.customer.domain.valueobject.CustomerSegment;
import com.autolift.customer.domain.valueobject.CustomerStatus;
import com.autolift.customer.infrastructure.persistence.entity.CustomerJpaEntity;
import com.autolift.customer.infrastructure.persistence.repository.CustomerJpaRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CustomerSeedJobConfig {

  private static final Logger log = LoggerFactory.getLogger(CustomerSeedJobConfig.class);

  @Bean
  public Job customerSeedJob(JobRepository jobRepository, Step customerSeedStep) {
    return new JobBuilder("customerSeedJob", jobRepository).start(customerSeedStep).build();
  }

  @Bean
  public Step customerSeedStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<String> customerFileReader,
      ItemProcessor<String, Customer> customerProcessor,
      ItemWriter<Customer> customerWriter) {
    return new StepBuilder("customerSeedStep", jobRepository)
        .<String, Customer>chunk(1000, transactionManager)
        .reader(customerFileReader)
        .processor(customerProcessor)
        .writer(customerWriter)
        .build();
  }

  @Bean
  @StepScope
  public FlatFileItemReader<String> customerFileReader(
      @Value("#{jobParameters['filePath']}") String filePath) {
    log.info("Creating customer file reader for: {}", filePath);
    return new FlatFileItemReaderBuilder<String>()
        .name("customerFileReader")
        .resource(new FileSystemResource(filePath))
        .lineMapper((line, lineNumber) -> line)
        .build();
  }

  @Bean
  @StepScope
  public ItemProcessor<String, Customer> customerProcessor() {
    return new CustomerLineProcessor();
  }

  @Bean
  @StepScope
  public ItemWriter<Customer> customerWriter(
      @Value("#{jobParameters['jobId']}") String jobId, CustomerJpaRepository customerRepository) {
    return new CustomerBatchWriter(customerRepository, jobId);
  }

  static class CustomerLineProcessor implements ItemProcessor<String, Customer> {

    @Override
    public Customer process(String line) throws Exception {
      if (line == null || line.trim().isEmpty()) {
        return null;
      }

      String[] parts = line.split(",", -1);
      if (parts.length < 1) {
        return null;
      }

      String customerId = parts[0].trim().replace("\"", "");
      if (customerId.isEmpty()) {
        return null;
      }

      UUID uuid = customerIdToUuid(customerId);
      CustomerId id = CustomerId.of(uuid);
      String name = generateName(customerId);
      String email = customerId + "@x5.client";
      String phone = parts.length > 4 ? parts[4].trim().replace("\"", "") : "";
      Instant now = Instant.now();

      return Customer.of(
          id, name, email, phone, CustomerSegment.NORMAL, CustomerStatus.ACTIVE, now, now);
    }

    private UUID customerIdToUuid(String customerId) {
      String namespace = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
      return UUID.nameUUIDFromBytes((namespace + customerId).getBytes());
    }

    private String generateName(String customerId) {
      String truncated = customerId.substring(0, Math.min(8, customerId.length()));
      return "X5 Customer " + truncated;
    }
  }

  static class CustomerBatchWriter implements ItemWriter<Customer> {

    private final CustomerJpaRepository repository;
    private final String jobId;
    private final List<Customer> batch = new ArrayList<>();
    private static final int BATCH_SIZE = 500;

    public CustomerBatchWriter(CustomerJpaRepository repository, String jobId) {
      this.repository = repository;
      this.jobId = jobId;
    }

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
      List<? extends Customer> items = chunk.getItems();
      for (Customer customer : items) {
        if (customer != null) {
          batch.add(customer);
          if (batch.size() >= BATCH_SIZE) {
            flushBatch();
          }
        }
      }
    }

    private void flushBatch() {
      if (!batch.isEmpty()) {
        List<CustomerJpaEntity> entities = new ArrayList<>();
        for (Customer c : batch) {
          entities.add(toEntity(c));
        }
        repository.saveAll(entities);
        batch.clear();
      }
    }

    private CustomerJpaEntity toEntity(Customer c) {
      return new CustomerJpaEntity(
          c.getId().getUuid(),
          c.getName(),
          c.getEmail(),
          c.getPhone(),
          c.getSegment(),
          c.getStatus(),
          c.getCreatedAt(),
          c.getUpdatedAt());
    }
  }
}
