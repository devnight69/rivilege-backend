package com.rivilege.app.repository;

import com.rivilege.app.model.DmtExpress;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a dmt express repository .
 *
 * @author kousik manik
 */
@Repository
public interface DmtExpressRepository extends JpaRepository<DmtExpress, Long> {

  Optional<DmtExpress> findByMemberId(String memberId);

}
