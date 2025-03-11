package com.rivilege.app.repository;

import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.model.IdPackages;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * this is a Id Package Repository .
 *
 * @author kousik manik
 */
@Repository
public interface IdPackagesRepository extends JpaRepository<IdPackages, Long> {

  Optional<IdPackages> findByUserDesignation(UserDesignationType userDesignation);

  // Search by ULID
  Optional<IdPackages> findByUlId(String ulId);

}
