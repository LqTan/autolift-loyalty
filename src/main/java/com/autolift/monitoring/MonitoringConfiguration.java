package com.autolift.monitoring;

import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.repository.MlJobRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class MonitoringConfiguration {

    @Component("mlJobHealthIndicator")
    public static class MlJobHealthIndicator implements HealthIndicator {

        private final MlJobRepository mlJobRepository;

        public MlJobHealthIndicator(MlJobRepository mlJobRepository) {
            this.mlJobRepository = mlJobRepository;
        }

        @Override
        public Health health() {
            long pendingCount = mlJobRepository.countByStatus(MlJobStatus.PENDING);
            long failedCount = mlJobRepository.countByStatus(MlJobStatus.FAILED);

            if (failedCount > 0) {
                return Health.down()
                        .withDetail("failedJobs", failedCount)
                        .withDetail("pendingJobs", pendingCount)
                        .withDetail("reason", "There are failed ML jobs")
                        .build();
            }

            if (pendingCount > 10) {
                return Health.up()
                        .withDetail("pendingJobs", pendingCount)
                        .withDetail("warning", "High number of pending ML jobs")
                        .build();
            }

            return Health.up()
                    .withDetail("pendingJobs", pendingCount)
                    .withDetail("failedJobs", failedCount)
                    .build();
        }
    }

    @Component("mlJobMetrics")
    public static class MlJobMetrics {

        private final AtomicLong pendingJobs = new AtomicLong(0);
        private final AtomicLong runningJobs = new AtomicLong(0);
        private final AtomicLong completedJobs = new AtomicLong(0);
        private final AtomicLong failedJobs = new AtomicLong(0);

        private final Counter completedCounter;
        private final Counter failedCounter;

        public MlJobMetrics(MeterRegistry meterRegistry) {
            Gauge.builder("ml.jobs.pending", pendingJobs, AtomicLong::get)
                    .description("Number of pending ML jobs")
                    .register(meterRegistry);

            Gauge.builder("ml.jobs.running", runningJobs, AtomicLong::get)
                    .description("Number of running ML jobs")
                    .register(meterRegistry);

            Gauge.builder("ml.jobs.completed", completedJobs, AtomicLong::get)
                    .description("Number of completed ML jobs")
                    .register(meterRegistry);

            Gauge.builder("ml.jobs.failed", failedJobs, AtomicLong::get)
                    .description("Number of failed ML jobs")
                    .register(meterRegistry);

            completedCounter = Counter.builder("ml.jobs.completed.total")
                    .description("Total number of completed ML jobs")
                    .register(meterRegistry);

            failedCounter = Counter.builder("ml.jobs.failed.total")
                    .description("Total number of failed ML jobs")
                    .register(meterRegistry);
        }

        public void recordPending(long count) {
            pendingJobs.set(count);
        }

        public void recordRunning(long count) {
            runningJobs.set(count);
        }

        public void recordCompleted() {
            completedJobs.incrementAndGet();
            completedCounter.increment();
        }

        public void recordFailed() {
            failedJobs.incrementAndGet();
            failedCounter.increment();
        }
    }

    @Component("campaignHealthIndicator")
    public static class CampaignHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.up().withDetail("status", "Campaign module is running").build();
        }
    }

    @Component("voucherHealthIndicator")
    public static class VoucherHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.up().withDetail("status", "Voucher module is running").build();
        }
    }

    @Component("loyaltyHealthIndicator")
    public static class LoyaltyHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.up().withDetail("status", "Loyalty module is running").build();
        }
    }

    @Component("targetingHealthIndicator")
    public static class TargetingHealthIndicator implements HealthIndicator {

        @Override
        public Health health() {
            return Health.up().withDetail("status", "Targeting module is running").build();
        }
    }
}