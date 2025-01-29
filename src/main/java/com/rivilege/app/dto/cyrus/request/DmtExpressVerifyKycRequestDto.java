package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a dmt express verify kyc request dto .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressVerifyKycRequestDto {

  @NotNull(message = "mobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotNull(message = "otp is required")
  @Pattern(regexp = "^[0-9]{6}$", message = "otp must be a 6-digit number")
  private String otp;

}
