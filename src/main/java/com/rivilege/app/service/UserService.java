package com.rivilege.app.service;

import org.springframework.http.ResponseEntity;

/**
 * this is a user service class .
 *
 * @author kousik manik
 */
public interface UserService {

  public ResponseEntity<?> getUserDetails(String memberId);

  public ResponseEntity<?> getReferralDetails(String memberId);

}
