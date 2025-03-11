package com.rivilege.app.repository;

import com.rivilege.app.model.BbpsCircleDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a bbps circle details repository class .
 *
 * @author kousik manik
 */
@Repository
public interface BbpsCircleDetailsRepository extends JpaRepository<BbpsCircleDetails, Long> {

  List<BbpsCircleDetails> findByBbpsType(String bbpsType);

}
