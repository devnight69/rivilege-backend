package com.rivilege.app.repository;

import com.rivilege.app.model.DmtChargesDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * this is a dmt charges details repository class .
 *
 * @author kousik manik
 */
@Repository
public interface DmtChargesDetailsRepository extends JpaRepository<DmtChargesDetails, Long> {

  @Query("SELECT d FROM DmtChargesDetails d WHERE :amount BETWEEN d.minAmount AND d.maxAmount")
  Optional<DmtChargesDetails> findChargeByAmount(Integer amount);

}
