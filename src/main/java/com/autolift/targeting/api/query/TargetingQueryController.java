package com.autolift.targeting.api.query;

import com.autolift.targeting.application.query.GetCustomerFeatureHandler;
import com.autolift.targeting.application.query.GetCustomerFeatureQuery;
import com.autolift.targeting.application.query.GetTargetCustomersHandler;
import com.autolift.targeting.application.query.GetTargetCustomersQuery;
import java.util.List;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/targeting")
@Import({GetTargetCustomersHandler.class, GetCustomerFeatureHandler.class})
public class TargetingQueryController {

  private final GetTargetCustomersHandler targetCustomersHandler;
  private final GetCustomerFeatureHandler customerFeatureHandler;

  public TargetingQueryController(
      GetTargetCustomersHandler targetCustomersHandler,
      GetCustomerFeatureHandler customerFeatureHandler) {
    this.targetCustomersHandler = targetCustomersHandler;
    this.customerFeatureHandler = customerFeatureHandler;
  }

  @GetMapping("/campaigns/{campaignId}/candidates")
  public ResponseEntity<List<TargetCustomerResponse>> getTargetCustomers(
      @PathVariable String campaignId, @RequestParam(defaultValue = "100") int limit) {
    var view = targetCustomersHandler.handle(new GetTargetCustomersQuery(campaignId, limit));
    List<TargetCustomerResponse> response =
        view.stream()
            .map(
                v ->
                    new TargetCustomerResponse(
                        v.customerId(),
                        v.upliftScore(),
                        v.segment(),
                        v.treatmentProbability(),
                        v.controlProbability()))
            .toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/campaigns/{campaignId}/features/{customerId}")
  public ResponseEntity<CustomerFeatureResponse> getCustomerFeature(
      @PathVariable String campaignId, @PathVariable String customerId) {
    var view = customerFeatureHandler.handle(new GetCustomerFeatureQuery(customerId, campaignId));
    if (view == null) {
      return ResponseEntity.notFound().build();
    }
    CustomerFeatureResponse response =
        new CustomerFeatureResponse(
            view.customerId(),
            view.recencyDays(),
            view.frequency90d(),
            view.monetary90d(),
            view.avgBasketValue(),
            view.totalQuantity90d(),
            view.uniqueProductCount(),
            view.uniqueCategoryCount(),
            view.favoriteCategory(),
            view.featureVersion(),
            view.createdAt());
    return ResponseEntity.ok(response);
  }
}
