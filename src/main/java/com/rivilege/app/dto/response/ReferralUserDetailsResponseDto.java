package com.rivilege.app.dto.response;

import com.rivilege.app.enums.UserDesignationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this is a referral user details response dto class .
 *
 * @author kousik manik
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralUserDetailsResponseDto {

  private String fullName;

  private String memberId;

  private String mobileNumber;

  private UserDesignationType userDesignation;

}
