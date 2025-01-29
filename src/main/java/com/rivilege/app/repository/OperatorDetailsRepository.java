package com.rivilege.app.repository;

import com.rivilege.app.model.OperatorDetails;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a operator details repository .
 *
 * @author kousik manik
 */
@Repository
public interface OperatorDetailsRepository extends JpaRepository<OperatorDetails, Long> {

  List<OperatorDetails> findByServiceTypeName(String serviceTypeName);

}
