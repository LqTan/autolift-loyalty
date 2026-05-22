package com.autolift.ml.application.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MlJobMetricsView {

  private UUID jobId;
  private String modelVersion;
  private Map<String, Object> metrics;
  private List<UpliftCurvePoint> upliftCurve;
  private List<QiniCurvePoint> qiniCurve;
  private EconomicComparison economicComparison;

  public MlJobMetricsView() {}

  public MlJobMetricsView(
      UUID jobId,
      String modelVersion,
      Map<String, Object> metrics,
      List<UpliftCurvePoint> upliftCurve,
      List<QiniCurvePoint> qiniCurve,
      EconomicComparison economicComparison) {
    this.jobId = jobId;
    this.modelVersion = modelVersion;
    this.metrics = metrics;
    this.upliftCurve = upliftCurve;
    this.qiniCurve = qiniCurve;
    this.economicComparison = economicComparison;
  }

  public static MlJobMetricsView from(
      UUID jobId,
      String modelVersion,
      Map<String, Object> metrics,
      List<UpliftCurvePoint> upliftCurve,
      List<QiniCurvePoint> qiniCurve,
      EconomicComparison economicComparison) {
    return new MlJobMetricsView(jobId, modelVersion, metrics, upliftCurve, qiniCurve, economicComparison);
  }

  public UUID getJobId() {
    return jobId;
  }

  public String getModelVersion() {
    return modelVersion;
  }

  public Map<String, Object> getMetrics() {
    return metrics;
  }

  public List<UpliftCurvePoint> getUpliftCurve() {
    return upliftCurve;
  }

  public List<QiniCurvePoint> getQiniCurve() {
    return qiniCurve;
  }

  public EconomicComparison getEconomicComparison() {
    return economicComparison;
  }

  public static class UpliftCurvePoint {
    private double targetFraction;
    private int numCustomers;
    private double observedUplift;
    private Double treatedResponseRate;
    private Double controlResponseRate;

    public UpliftCurvePoint() {}

    public UpliftCurvePoint(
        double targetFraction,
        int numCustomers,
        double observedUplift,
        Double treatedResponseRate,
        Double controlResponseRate) {
      this.targetFraction = targetFraction;
      this.numCustomers = numCustomers;
      this.observedUplift = observedUplift;
      this.treatedResponseRate = treatedResponseRate;
      this.controlResponseRate = controlResponseRate;
    }

    public double getTargetFraction() {
      return targetFraction;
    }

    public int getNumCustomers() {
      return numCustomers;
    }

    public double getObservedUplift() {
      return observedUplift;
    }

    public Double getTreatedResponseRate() {
      return treatedResponseRate;
    }

    public Double getControlResponseRate() {
      return controlResponseRate;
    }
  }

  public static class QiniCurvePoint {
    private double targetFraction;
    private int numCustomers;
    private double qini;
    private int cumTreated;
    private int cumControl;
    private int cumTreatedResponders;
    private int cumControlResponders;

    public QiniCurvePoint() {}

    public QiniCurvePoint(
        double targetFraction,
        int numCustomers,
        double qini,
        int cumTreated,
        int cumControl,
        int cumTreatedResponders,
        int cumControlResponders) {
      this.targetFraction = targetFraction;
      this.numCustomers = numCustomers;
      this.qini = qini;
      this.cumTreated = cumTreated;
      this.cumControl = cumControl;
      this.cumTreatedResponders = cumTreatedResponders;
      this.cumControlResponders = cumControlResponders;
    }

    public double getTargetFraction() {
      return targetFraction;
    }

    public int getNumCustomers() {
      return numCustomers;
    }

    public double getQini() {
      return qini;
    }

    public int getCumTreated() {
      return cumTreated;
    }

    public int getCumControl() {
      return cumControl;
    }

    public int getCumTreatedResponders() {
      return cumTreatedResponders;
    }

    public int getCumControlResponders() {
      return cumControlResponders;
    }
  }

  public static class EconomicComparison {
    private StrategySummary massCampaign;
    private StrategySummary responseTargeting;
    private StrategySummary upliftTargeting;

    public EconomicComparison() {}

    public EconomicComparison(
        StrategySummary massCampaign,
        StrategySummary responseTargeting,
        StrategySummary upliftTargeting) {
      this.massCampaign = massCampaign;
      this.responseTargeting = responseTargeting;
      this.upliftTargeting = upliftTargeting;
    }

    public StrategySummary getMassCampaign() {
      return massCampaign;
    }

    public StrategySummary getResponseTargeting() {
      return responseTargeting;
    }

    public StrategySummary getUpliftTargeting() {
      return upliftTargeting;
    }

    public static class StrategySummary {
      private int numTargeted;
      private double expectedIncrementalConversions;
      private long promotionCost;
      private double expectedRevenue;
      private double netProfit;

      public StrategySummary() {}

      public StrategySummary(
          int numTargeted,
          double expectedIncrementalConversions,
          long promotionCost,
          double expectedRevenue,
          double netProfit) {
        this.numTargeted = numTargeted;
        this.expectedIncrementalConversions = expectedIncrementalConversions;
        this.promotionCost = promotionCost;
        this.expectedRevenue = expectedRevenue;
        this.netProfit = netProfit;
      }

      public int getNumTargeted() {
        return numTargeted;
      }

      public double getExpectedIncrementalConversions() {
        return expectedIncrementalConversions;
      }

      public long getPromotionCost() {
        return promotionCost;
      }

      public double getExpectedRevenue() {
        return expectedRevenue;
      }

      public double getNetProfit() {
        return netProfit;
      }
    }
  }
}