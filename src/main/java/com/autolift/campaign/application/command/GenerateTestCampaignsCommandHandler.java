package com.autolift.campaign.application.command;

import com.autolift.campaign.api.command.GenerateTestCampaignsResponse;
import com.autolift.campaign.api.command.GenerateTestCampaignsResponse.TestCampaignResult;
import com.autolift.campaign.domain.model.Campaign;
import com.autolift.campaign.domain.repository.CampaignRepository;
import com.autolift.campaign.domain.valueobject.Budget;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

@Component
public class GenerateTestCampaignsCommandHandler {

  private static final String[] CAMPAIGN_NAMES = {
    "Summer Sale",
    "Flash Deal",
    "Weekend Special",
    "Holiday Promo",
    "Member Exclusive",
    "New Customer Offer",
    "Buy More Save More",
    "Free Shipping Day",
    "Clearance Sale",
    "Loyalty Rewards",
    "Birthday Special",
    "VIP Access",
    "Limited Time Offer",
    "Bundle Deal",
    "First Purchase Discount"
  };

  private static final String[] DESCRIPTIONS = {
    "Khuyến mãi hè 2026",
    "Giảm giá cực sốc",
    "Ưu đãi cuối tuần",
    "Khuyến mãi ngày lễ",
    "Dành riêng cho thành viên",
    "Chào mừng khách hàng mới",
    "Mua nhiều giảm nhiều",
    "Miễn phí vận chuyển",
    "Xả kho hàng tồn",
    "Tích điểm đổi quà",
    "Chúc mừng sinh nhật",
    "Quyền truy cập VIP",
    "Ưu đãi có hạn",
    "Mua theo bộ",
    "Giảm 10% cho đơn hàng đầu tiên"
  };

  private static final String[] CURRENCIES = {"VND", "USD", "VND", "VND"};

  private final CampaignRepository repository;

  public GenerateTestCampaignsCommandHandler(CampaignRepository repository) {
    this.repository = repository;
  }

  public GenerateTestCampaignsResponse handle(GenerateTestCampaignsCommand command) {
    List<TestCampaignResult> results =
        IntStream.range(0, command.count())
            .mapToObj(i -> createRandomCampaign())
            .toList();

    return new GenerateTestCampaignsResponse(command.count(), results);
  }

  private TestCampaignResult createRandomCampaign() {
    String id = UUID.randomUUID().toString().substring(0, 8);
    String name = CAMPAIGN_NAMES[(int) (Math.random() * CAMPAIGN_NAMES.length)] + " " + id;
    String description = DESCRIPTIONS[(int) (Math.random() * DESCRIPTIONS.length)];
    BigDecimal budget = BigDecimal.valueOf((long) (Math.random() * 100000000) + 1000000);
    String currency = CURRENCIES[(int) (Math.random() * CURRENCIES.length)];

    Instant startDate = Instant.now().plus((long) (Math.random() * 30), ChronoUnit.DAYS);
    Instant endDate = startDate.plus((long) (Math.random() * 60) + 30, ChronoUnit.DAYS);

    Budget budgetObj = Budget.of(budget, currency);
    Campaign campaign = Campaign.create(name, description, startDate, endDate, budgetObj);
    repository.save(campaign);

    return new TestCampaignResult(
        campaign.getId().getId().toString(),
        campaign.getName(),
        campaign.getStatus().name());
  }
}