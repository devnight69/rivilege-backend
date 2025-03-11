package com.rivilege.app.repository;

import com.rivilege.app.model.MobileRechargeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a mobile recharge history repository class .
 *
 * @author kousik manik
 */
@Repository
public interface MobileRechargeHistoryRepository extends JpaRepository<MobileRechargeHistory, Long> {
}
