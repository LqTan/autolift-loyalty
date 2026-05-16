package com.autolift.campaign.domain.valueobject;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class CampaignId {

  private UUID id;

  private CampaignId(UUID id) {
    this.id = id;
  }

  public static CampaignId of(UUID id) {
    return new CampaignId(id);
  }

  public static CampaignId of(String id) {
    return new CampaignId(UUID.fromString(id));
  }

  public static CampaignId random() {
    return new CampaignId(UUID.randomUUID());
  }
}
