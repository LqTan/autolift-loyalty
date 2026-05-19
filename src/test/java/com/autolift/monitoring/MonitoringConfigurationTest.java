package com.autolift.monitoring;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.repository.MlJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class MonitoringConfigurationTest {

    @Mock private MlJobRepository mlJobRepository;

    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
    }

    @Test
    void mlJobHealthIndicator_whenNoFailedJobs_shouldReturnUp() {
        when(mlJobRepository.countByStatus(MlJobStatus.PENDING)).thenReturn(5L);
        when(mlJobRepository.countByStatus(MlJobStatus.FAILED)).thenReturn(0L);

        MonitoringConfiguration.MlJobHealthIndicator healthIndicator =
                new MonitoringConfiguration.MlJobHealthIndicator(mlJobRepository);
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(5L, health.getDetails().get("pendingJobs"));
        assertEquals(0L, health.getDetails().get("failedJobs"));
    }

    @Test
    void mlJobHealthIndicator_whenHasFailedJobs_shouldReturnDown() {
        when(mlJobRepository.countByStatus(MlJobStatus.PENDING)).thenReturn(3L);
        when(mlJobRepository.countByStatus(MlJobStatus.FAILED)).thenReturn(2L);

        MonitoringConfiguration.MlJobHealthIndicator healthIndicator =
                new MonitoringConfiguration.MlJobHealthIndicator(mlJobRepository);
        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(2L, health.getDetails().get("failedJobs"));
        assertEquals("There are failed ML jobs", health.getDetails().get("reason"));
    }

    @Test
    void mlJobHealthIndicator_whenHighPendingJobs_shouldReturnUpWithWarning() {
        when(mlJobRepository.countByStatus(MlJobStatus.PENDING)).thenReturn(15L);
        when(mlJobRepository.countByStatus(MlJobStatus.FAILED)).thenReturn(0L);

        MonitoringConfiguration.MlJobHealthIndicator healthIndicator =
                new MonitoringConfiguration.MlJobHealthIndicator(mlJobRepository);
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(15L, health.getDetails().get("pendingJobs"));
        assertNotNull(health.getDetails().get("warning"));
    }

    @Test
    void mlJobMetrics_shouldRegisterGauges() {
        MonitoringConfiguration.MlJobMetrics metrics =
                new MonitoringConfiguration.MlJobMetrics(meterRegistry);

        metrics.recordPending(10);
        metrics.recordRunning(2);
        metrics.recordCompleted();
        metrics.recordCompleted();
        metrics.recordFailed();

        assertEquals(10.0, meterRegistry.get("ml.jobs.pending").gauge().value());
        assertEquals(2.0, meterRegistry.get("ml.jobs.running").gauge().value());
        assertEquals(2.0, meterRegistry.get("ml.jobs.completed").gauge().value());
        assertEquals(1.0, meterRegistry.get("ml.jobs.failed").gauge().value());
    }

    @Test
    void mlJobMetrics_countersShouldIncrement() {
        MonitoringConfiguration.MlJobMetrics metrics =
                new MonitoringConfiguration.MlJobMetrics(meterRegistry);

        metrics.recordCompleted();
        metrics.recordCompleted();
        metrics.recordCompleted();
        metrics.recordFailed();
        metrics.recordFailed();

        assertEquals(3.0, meterRegistry.get("ml.jobs.completed.total").counter().count());
        assertEquals(2.0, meterRegistry.get("ml.jobs.failed.total").counter().count());
    }

    @Test
    void campaignHealthIndicator_shouldReturnUp() {
        MonitoringConfiguration.CampaignHealthIndicator healthIndicator =
                new MonitoringConfiguration.CampaignHealthIndicator();
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Campaign module is running", health.getDetails().get("status"));
    }

    @Test
    void voucherHealthIndicator_shouldReturnUp() {
        MonitoringConfiguration.VoucherHealthIndicator healthIndicator =
                new MonitoringConfiguration.VoucherHealthIndicator();
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Voucher module is running", health.getDetails().get("status"));
    }

    @Test
    void loyaltyHealthIndicator_shouldReturnUp() {
        MonitoringConfiguration.LoyaltyHealthIndicator healthIndicator =
                new MonitoringConfiguration.LoyaltyHealthIndicator();
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Loyalty module is running", health.getDetails().get("status"));
    }

    @Test
    void targetingHealthIndicator_shouldReturnUp() {
        MonitoringConfiguration.TargetingHealthIndicator healthIndicator =
                new MonitoringConfiguration.TargetingHealthIndicator();
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("Targeting module is running", health.getDetails().get("status"));
    }
}