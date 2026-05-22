package com.autolift.ml.application.query;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobId;
import com.autolift.ml.application.query.MlJobMetricsView.EconomicComparison;
import com.autolift.ml.application.query.MlJobMetricsView.EconomicComparison.StrategySummary;
import com.autolift.ml.application.query.MlJobMetricsView.QiniCurvePoint;
import com.autolift.ml.application.query.MlJobMetricsView.UpliftCurvePoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GetMlJobMetricsHandler {

  private final MlJobRepository mlJobRepository;

  public GetMlJobMetricsHandler(MlJobRepository mlJobRepository) {
    this.mlJobRepository = mlJobRepository;
  }

  public MlJobMetricsView handle(GetMlJobMetricsQuery query) {
    MlJob job =
        mlJobRepository
            .findById(MlJobId.of(query.jobId()))
            .orElseThrow(() -> new IllegalArgumentException("ML Job not found: " + query.jobId()));

    Map<String, Object> metrics = job.getMetrics();
    List<UpliftCurvePoint> upliftCurve = buildUpliftCurve(metrics);
    List<QiniCurvePoint> qiniCurve = buildQiniCurve(metrics);
    EconomicComparison economicComparison = buildEconomicComparison(metrics);

    return MlJobMetricsView.from(
        job.getId().getId(),
        job.getModelVersion(),
        metrics,
        upliftCurve,
        qiniCurve,
        economicComparison);
  }

  @SuppressWarnings("unchecked")
  private List<UpliftCurvePoint> buildUpliftCurve(Map<String, Object> metrics) {
    List<UpliftCurvePoint> curve = new ArrayList<>();

    if (metrics == null || !metrics.containsKey("upliftCurve")) {
      return curve;
    }

    Object upliftCurveObj = metrics.get("upliftCurve");
    if (upliftCurveObj instanceof List<?> rawList) {
      for (Object item : rawList) {
        if (item instanceof Map<?, ?> point) {
          Map<String, Object> p = (Map<String, Object>) item;
          curve.add(
              new UpliftCurvePoint(
                  ((Number) p.getOrDefault("targetFraction", 0.0)).doubleValue(),
                  ((Number) p.getOrDefault("numCustomers", 0)).intValue(),
                  ((Number) p.getOrDefault("observedUplift", 0.0)).doubleValue(),
                  toDouble(p.get("treatedResponseRate")),
                  toDouble(p.get("controlResponseRate"))));
        }
      }
    }

    return curve;
  }

  @SuppressWarnings("unchecked")
  private List<QiniCurvePoint> buildQiniCurve(Map<String, Object> metrics) {
    List<QiniCurvePoint> curve = new ArrayList<>();

    if (metrics == null || !metrics.containsKey("qiniCurve")) {
      return curve;
    }

    Object qiniCurveObj = metrics.get("qiniCurve");
    if (qiniCurveObj instanceof List<?> rawList) {
      for (Object item : rawList) {
        if (item instanceof Map<?, ?> point) {
          Map<String, Object> p = (Map<String, Object>) item;
          curve.add(
              new QiniCurvePoint(
                  ((Number) p.getOrDefault("targetFraction", 0.0)).doubleValue(),
                  ((Number) p.getOrDefault("numCustomers", 0)).intValue(),
                  ((Number) p.getOrDefault("qini", 0.0)).doubleValue(),
                  ((Number) p.getOrDefault("cumTreated", 0)).intValue(),
                  ((Number) p.getOrDefault("cumControl", 0)).intValue(),
                  ((Number) p.getOrDefault("cumTreatedResponders", 0)).intValue(),
                  ((Number) p.getOrDefault("cumControlResponders", 0)).intValue()));
        }
      }
    }

    return curve;
  }

  @SuppressWarnings("unchecked")
  private EconomicComparison buildEconomicComparison(Map<String, Object> metrics) {
    StrategySummary massCampaign = new StrategySummary();
    StrategySummary responseTargeting = new StrategySummary();
    StrategySummary upliftTargeting = new StrategySummary();

    if (metrics == null || !metrics.containsKey("economicSummary")) {
      return new EconomicComparison(massCampaign, responseTargeting, upliftTargeting);
    }

    Object economicObj = metrics.get("economicSummary");
    if (economicObj instanceof List<?> rawList) {
      for (Object item : rawList) {
        if (item instanceof Map<?, ?> point) {
          Map<String, Object> p = (Map<String, Object>) item;
          String strategy = (String) p.getOrDefault("strategy", "");
          StrategySummary summary =
              new StrategySummary(
                  ((Number) p.getOrDefault("numTargeted", 0)).intValue(),
                  ((Number) p.getOrDefault("expectedIncrementalConversions", 0.0)).doubleValue(),
                  ((Number) p.getOrDefault("promotionCost", 0L)).longValue(),
                  ((Number) p.getOrDefault("expectedRevenue", 0.0)).doubleValue(),
                  ((Number) p.getOrDefault("netProfit", 0.0)).doubleValue());

          if ("Mass Campaign".equals(strategy)) {
            massCampaign = summary;
          } else if ("Response Targeting".equals(strategy)) {
            responseTargeting = summary;
          } else if ("Uplift Targeting".equals(strategy)) {
            upliftTargeting = summary;
          }
        }
      }
    }

    return new EconomicComparison(massCampaign, responseTargeting, upliftTargeting);
  }

  private Double toDouble(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    return null;
  }
}