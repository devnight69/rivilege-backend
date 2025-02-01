package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a dmt express add beneficiary request dto .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressAddBeneficiaryRequestDto {

  @NotNull(message = "mobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotNull(message = "customerMobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect customerMobileNumber")
  private String customerMobileNumber;

  @NotNull(message = "bankId cannot be null")
  @NotBlank(message = "bankId cannot be blank")
  @NotEmpty(message = "bankId cannot be empty")
  private String bankId;

  @NotNull(message = "customerName cannot be null")
  @NotBlank(message = "customerName cannot be blank")
  @NotEmpty(message = "customerName cannot be empty")
  private String customerName;

  @NotNull(message = "accountNumber cannot be null")
  @NotBlank(message = "accountNumber cannot be blank")
  @NotEmpty(message = "accountNumber cannot be empty")
  private String accountNumber;

  @NotNull(message = "ifscCode cannot be null")
  @NotBlank(message = "ifscCode cannot be blank")
  @NotEmpty(message = "ifscCode cannot be empty")
  private String ifscCode;

}
