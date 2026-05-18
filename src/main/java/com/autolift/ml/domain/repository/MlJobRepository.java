package com.autolift.ml.domain.repository;

import com.autolift.ml.domain.model.MlJob;
import com.autolift.ml.domain.valueobject.MlJobId;
import com.autolift.ml.domain.valueobject.MlJobStatus;
import com.autolift.ml.domain.valueobject.MlJobType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MlJobRepository {

  MlJob save(MlJob job);

  Optional<MlJob> findById(MlJobId id);

  List<MlJob> findByCampaignId(String campaignId);

  List<MlJob> findByStatus(MlJobStatus status);

  List<MlJob> findByJobTypeAndStatus(MlJobType jobType, MlJobStatus status);

  List<MlJob> findByUpliftScoreJobId(UUID upliftScoreJobId);

  Optional<MlJob> findFirstPendingByJobTypeOrderByCreatedAtAsc(MlJobType jobType);
}