package com.rivilege.app.dto.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * this is a Token generation using refresh token request dto .
 *
 * @author kousik manik
 */
@Data
public class TokenUsingRefreshTokenRequestDto {

  public String token;

  @NotBlank(message = "memberId cannot be blank.")
  @Pattern(
      regexp = RivilegeConstantService.MEMBER_ID_REGEX,
      message = "memberId must start with 'R' followed by exactly 9 alphanumeric characters (letters and digits)"
          + " without any special characters."
  )
  @Size(min = 10, max = 10, message = "Member ID must be exactly 10 characters long")
  private String memberId;

}
