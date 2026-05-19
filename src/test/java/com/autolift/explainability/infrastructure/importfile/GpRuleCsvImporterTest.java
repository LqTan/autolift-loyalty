package com.autolift.explainability.infrastructure.importfile;

import static org.assertj.core.api.Assertions.assertThat;

import com.autolift.explainability.domain.model.GpRule;
import com.autolift.explainability.domain.repository.GpRuleRepository;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class GpRuleCsvImporterTest {

  private GpRuleRepository repository;
  private GpRuleCsvImporter importer;

  @BeforeEach
  void setUp() {
    repository = new InMemoryGpRuleRepository();
    importer = new GpRuleCsvImporter(repository);
  }

  @Test
  void shouldImportValidCsvWithCorrectColumnMapping() {
    String csv =
        "campaign_id,rule_text,rule_expression,target_label,precision_value,recall_value,f1_score,accuracy_value,coverage_value,model_version,source_file,created_at\n"
            + "x5-campaign-v1,(recency_days < 14) AND (frequency_total > 5),(recency_days < 14) AND (frequency_total > 5),PERSUADABLE,0.75,0.6,0.667,0.7,0.15,v1,gp_rules.csv,2026-05-18T18:03:27\n"
            + "x5-campaign-v1,(avg_basket_value > 15000) AND (unique_product_count > 30),(avg_basket_value > 15000) AND (unique_product_count > 30),PERSUADABLE,0.72,0.55,0.625,0.68,0.12,v1,gp_rules.csv,2026-05-18T18:03:27";

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "gp_rules.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    int count = importer.importFromCsv(file, "x5-campaign-v1", "v1");

    assertThat(count).isEqualTo(2);
    List<GpRule> rules = ((InMemoryGpRuleRepository) repository).getAll();
    assertThat(rules).hasSize(2);

    GpRule firstRule = rules.get(0);
    assertThat(firstRule.getCampaignId()).isEqualTo("x5-campaign-v1");
    assertThat(firstRule.getRuleText()).isEqualTo("(recency_days < 14) AND (frequency_total > 5)");
    assertThat(firstRule.getPrecisionValue()).isEqualByComparingTo(new BigDecimal("0.75"));
    assertThat(firstRule.getRecallValue()).isEqualByComparingTo(new BigDecimal("0.6"));
    assertThat(firstRule.getF1Score()).isEqualByComparingTo(new BigDecimal("0.667"));
    assertThat(firstRule.getAccuracyValue()).isEqualByComparingTo(new BigDecimal("0.7"));
    assertThat(firstRule.getCoverageValue()).isEqualByComparingTo(new BigDecimal("0.15"));
    assertThat(firstRule.getModelVersion()).isEqualTo("v1");
    assertThat(firstRule.getSourceFile()).isEqualTo("gp_rules.csv");
  }

  @Test
  void shouldSkipMalformedRowsWithInsufficientColumns() {
    String csv =
        "campaign_id,rule_text,rule_expression,target_label,precision_value,recall_value,f1_score,accuracy_value,coverage_value,model_version,source_file,created_at\n"
            + "x5-campaign-v1,(recency_days < 14),PERSUADABLE,0.75\n";

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "gp_rules.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    int count = importer.importFromCsv(file, "x5-campaign-v1", "v1");

    assertThat(count).isEqualTo(0);
  }

  @Test
  void shouldHandleEmptyFile() {
    String csv =
        "campaign_id,rule_text,rule_expression,target_label,precision_value,recall_value,f1_score,accuracy_value,coverage_value,model_version,source_file,created_at";

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "gp_rules.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

    int count = importer.importFromCsv(file, "x5-campaign-v1", "v1");

    assertThat(count).isEqualTo(0);
  }

  @Test
  void shouldBatchSaveWhenBatchSizeReached() {
    StringBuilder csv = new StringBuilder();
    csv.append(
        "campaign_id,rule_text,rule_expression,target_label,precision_value,recall_value,f1_score,accuracy_value,coverage_value,model_version,source_file,created_at\n");
    for (int i = 0; i < 501; i++) {
      csv.append("x5-campaign-v1,(rule_")
          .append(i)
          .append("),PERSUADABLE,0.75,0.6,0.667,0.7,0.15,v1,gp_rules.csv,2026-05-18T18:03:27\n");
    }

    MockMultipartFile file =
        new MockMultipartFile(
            "file", "gp_rules.csv", "text/csv", csv.toString().getBytes(StandardCharsets.UTF_8));

    int count = importer.importFromCsv(file, "x5-campaign-v1", "v1");

    assertThat(count).isEqualTo(501);
  }

  static class InMemoryGpRuleRepository implements GpRuleRepository {
    private final List<GpRule> rules = new ArrayList<>();

    @Override
    public GpRule save(GpRule rule) {
      rules.add(rule);
      return rule;
    }

    @Override
    public List<GpRule> saveAll(List<GpRule> rules) {
      this.rules.addAll(rules);
      return rules;
    }

    @Override
    public List<GpRule> findByCampaignId(String campaignId) {
      return rules.stream().filter(r -> r.getCampaignId().equals(campaignId)).toList();
    }

    @Override
    public List<GpRule> findByCampaignIdOrderByF1ScoreDesc(String campaignId) {
      return findByCampaignId(campaignId);
    }

    List<GpRule> getAll() {
      return new ArrayList<>(rules);
    }
  }
}
