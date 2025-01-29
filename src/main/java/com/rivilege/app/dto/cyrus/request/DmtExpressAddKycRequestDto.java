package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a dmt express add kyc request dto .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressAddKycRequestDto {

  @NotNull(message = "mobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotBlank(message = "panNumber cannot be blank.")
  @Pattern(regexp = RivilegeConstantService.PAN_REGEX,
      message = "Invalid PAN number format. It must match the format XXXXX9999X.")
  private String panNumber;

  @NotBlank(message = "aadhaarNumber cannot be blank.")
  @Pattern(
      regexp = RivilegeConstantService.AADHAAR_REGEX,
      message = "Invalid Aadhaar number format. It must be a 12-digit number."
  )
  private String aadhaarNumber;

}
