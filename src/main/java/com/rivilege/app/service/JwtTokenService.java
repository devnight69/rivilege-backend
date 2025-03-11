package com.rivilege.app.service;

import com.rivilege.app.dto.request.TokenUsingRefreshTokenRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a jwt token service .
 *
 * @author kousik manik
 */
public interface JwtTokenService {

  public ResponseEntity<?> generateTokenFromRefreshToken(TokenUsingRefreshTokenRequestDto dto);

}
