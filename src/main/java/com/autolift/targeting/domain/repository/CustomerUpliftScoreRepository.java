package com.autolift.targeting.domain.repository;

import com.autolift.targeting.domain.model.CustomerUpliftScore;
import com.autolift.targeting.domain.valueobject.UpliftScoreId;
import java.util.List;
import java.util.Optional;

public interface CustomerUpliftScoreRepository {

  CustomerUpliftScore save(CustomerUpliftScore score);

  void saveAll(List<CustomerUpliftScore> scores);

  Optional<CustomerUpliftScore> findById(UpliftScoreId id);

  List<CustomerUpliftScore> findByCampaignId(String campaignId);

  List<CustomerUpliftScore> findByCustomerId(String customerId);

  List<CustomerUpliftScore> findTopByCampaignIdOrderByUpliftScoreDesc(String campaignId, int limit);

  Optional<CustomerUpliftScore> findByCustomerIdAndCampaignId(String customerId, String campaignId);
}
