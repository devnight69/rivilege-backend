package com.rivilege.app.dto.jwt;

import lombok.Getter;
import lombok.Setter;

/**
 * JWT Data.
 */
@Setter
@Getter
public class JwtPayloadDto {
  private String fullName;
  private String mobileNumber;
  private String userUlid;
  private String userId;
  private String userDesignation;
  private String tokenType;
  private Boolean active;
  private String userType;
}



