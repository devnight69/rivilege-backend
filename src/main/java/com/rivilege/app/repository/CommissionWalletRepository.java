package com.rivilege.app.repository;

import com.rivilege.app.model.CommissionWallet;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a CommissionWalletRepository class .
 *
 * @author kousik manik
 */
@Repository
public interface CommissionWalletRepository extends JpaRepository<CommissionWallet, Long> {

  // Search by referredMemberId
  List<CommissionWallet> findByReferredMemberId(String referredMemberId);

  // Search by referredMobileNumber
  List<CommissionWallet> findByReferredMobileNumber(String referredMobileNumber);

}
