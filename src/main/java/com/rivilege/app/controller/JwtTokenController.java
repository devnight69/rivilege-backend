package com.rivilege.app.controller;

import com.rivilege.app.dto.request.TokenUsingRefreshTokenRequestDto;
import com.rivilege.app.service.JwtTokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a jwt refresh token controller .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/refresh-token")
public class JwtTokenController {

  @Autowired
  private JwtTokenService jwtTokenService;


  @PostMapping("/generate-token")
  public ResponseEntity<?> generateTokenFromRefreshToken(@Valid @RequestBody TokenUsingRefreshTokenRequestDto dto) {
    return jwtTokenService.generateTokenFromRefreshToken(dto);
  }

}
