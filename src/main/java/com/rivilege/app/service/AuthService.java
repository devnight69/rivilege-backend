package com.rivilege.app.service;

import com.rivilege.app.dto.request.LogInRequestDto;
import com.rivilege.app.dto.request.RegistrationUserRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a auth service class .
 *
 * @author kousik manik
 */
public interface AuthService {

  public ResponseEntity<?> registerNewMember(RegistrationUserRequestDto dto);

  public ResponseEntity<?> getReferralUserDetails(String memberId);

  public ResponseEntity<?> loginUser(LogInRequestDto dto);

}
