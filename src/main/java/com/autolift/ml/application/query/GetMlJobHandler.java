package com.autolift.ml.application.query;

import com.autolift.ml.domain.repository.MlJobRepository;
import com.autolift.ml.domain.valueobject.MlJobId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GetMlJobHandler {

  private final MlJobRepository mlJobRepository;

  public GetMlJobHandler(MlJobRepository mlJobRepository) {
    this.mlJobRepository = mlJobRepository;
  }

  public Optional<MlJobView> handleByJobId(GetMlJobQuery query) {
    if (query.getJobId() != null) {
      return mlJobRepository.findById(MlJobId.of(query.getJobId())).map(MlJobView::from);
    }
    return Optional.empty();
  }

  public List<MlJobView> handleByCampaign(GetMlJobQuery query) {
    if (query.getCampaignId() != null) {
      return mlJobRepository.findByCampaignId(query.getCampaignId()).stream()
          .map(MlJobView::from)
          .collect(Collectors.toList());
    }
    return List.of();
  }

  public Page<MlJobView> handleByCampaign(GetMlJobQuery query, Pageable pageable) {
    if (query.getCampaignId() != null) {
      return mlJobRepository.findByCampaignId(query.getCampaignId(), pageable).map(MlJobView::from);
    }
    return Page.empty();
  }
}
