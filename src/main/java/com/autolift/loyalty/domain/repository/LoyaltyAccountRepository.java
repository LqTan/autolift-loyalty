package com.autolift.loyalty.domain.repository;

import com.autolift.loyalty.domain.model.LoyaltyAccount;
import com.autolift.loyalty.domain.model.PointTransaction;
import com.autolift.loyalty.domain.valueobject.LoyaltyAccountId;
import java.util.List;
import java.util.Optional;

public interface LoyaltyAccountRepository {

  LoyaltyAccount save(LoyaltyAccount account);

  Optional<LoyaltyAccount> findById(LoyaltyAccountId id);

  Optional<LoyaltyAccount> findByCustomerId(String customerId);

  List<LoyaltyAccount> findAll();

  void saveTransaction(PointTransaction transaction);
}
