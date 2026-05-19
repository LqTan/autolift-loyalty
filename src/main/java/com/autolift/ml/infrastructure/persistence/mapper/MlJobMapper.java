package com.autolift.ml.infrastructure.persistence.mapper;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.valueobject.MlJobId;
import com.autolift.ml.infrastructure.persistence.entity.MlJobJpaEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class MlJobMapper {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static MlJob toDomain(MlJobJpaEntity entity) {
    return MlJob.of(
        MlJobId.of(entity.getId()),
        entity.getJobType(),
        entity.getCampaignId(),
        entity.getStatus(),
        entity.getModelVersion(),
        parseJsonParams(entity.getInputParams()),
        entity.getResultPath(),
        entity.getErrorMessage(),
        entity.getUpliftScoreJobId(),
        entity.getCreatedAt(),
        entity.getStartedAt(),
        entity.getCompletedAt());
  }

  public static MlJobJpaEntity toEntity(MlJob domain) {
    return new MlJobJpaEntity(
        domain.getId().getId(),
        domain.getJobType(),
        domain.getCampaignId(),
        domain.getStatus(),
        domain.getModelVersion(),
        toJsonParams(domain.getInputParams()),
        domain.getResultPath(),
        domain.getErrorMessage(),
        domain.getUpliftScoreJobId(),
        domain.getCreatedAt(),
        domain.getStartedAt(),
        domain.getCompletedAt());
  }

  private static Map<String, Object> parseJsonParams(String json) {
    if (json == null || json.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  private static String toJsonParams(Map<String, Object> params) {
    if (params == null) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(params);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
