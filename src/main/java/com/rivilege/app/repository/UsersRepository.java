package com.rivilege.app.repository;

import com.rivilege.app.model.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * this is a users repository method .
 *
 * @author kousik manik
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

  // Search by mobile number
  Optional<Users> findByMobileNumber(String mobileNumber);

  // Search by member ID
  Optional<Users> findByMemberId(String memberId);

  // Search by email ID
  Optional<Users> findByEmailId(String emailId);

  // Search by ULID
  Optional<Users> findByUlId(String ulId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END "
      + "FROM Users u WHERE u.mobileNumber = :mobileNumber OR u.emailId = :emailId")
  boolean existsByMobileNumberOrEmailId(String mobileNumber, String emailId);

  @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END "
      + "FROM Users u WHERE u.memberId = :memberId")
  boolean existsByMemberId(String memberId);

}
