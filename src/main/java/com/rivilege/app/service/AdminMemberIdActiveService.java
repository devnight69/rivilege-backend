package com.rivilege.app.service;

import org.springframework.http.ResponseEntity;

/**
 * this is a admin member id active service .
 *
 * @author kousik manik
 */
public interface AdminMemberIdActiveService {

  public ResponseEntity<?> memberIdActivation(String memberId);

}
