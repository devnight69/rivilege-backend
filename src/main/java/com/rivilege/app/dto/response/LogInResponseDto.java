package com.rivilege.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this is a login user response dto .
 *
 * @author kousik manik
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogInResponseDto {

  private String activeToken;

  private String refreshToken;

  private UserDetailsResponseDto userDetails;

}
