package com.rivilege.app.repository;

import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.model.ReferralDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * this is a ReferralDetails repository .
 *
 * @author kousik manik
 */
@Repository
public interface ReferralDetailsRepository extends JpaRepository<ReferralDetails, Long> {

  Optional<ReferralDetails> findByMemberId(String memberId);

  Optional<ReferralDetails> findByMobileNumber(String mobileNumber);

  Optional<ReferralDetails> findByReferredMemberId(String referredMemberId);

  @Query("SELECT r.regionalManagerId FROM ReferralDetails r WHERE r.referredMemberId = :memberId AND"
      + " r.referredDesignation = :referredDesignation")
  String findRegionalManagerIdByMemberIdAndDesignation(@Param("memberId") String memberId,
                                                       @Param("referredDesignation") UserDesignationType
                                                           referredDesignation);

  @Query("SELECT r.superDistributorId FROM ReferralDetails r WHERE r.referredMemberId = :memberId AND"
      + " r.referredDesignation = :referredDesignation")
  String findSuperDistributorIdByMemberIdAndDesignation(@Param("memberId") String memberId,
                                                        @Param("referredDesignation") UserDesignationType
                                                            referredDesignation);


  @Query("SELECT r.distributorId FROM ReferralDetails r WHERE r.referredMemberId = :memberId AND "
      + "r.referredDesignation = :referredDesignation")
  String findDistributorIdByMemberIdAndDesignation(@Param("memberId") String memberId,
                                                   @Param("referredDesignation") UserDesignationType
                                                       referredDesignation);


}
