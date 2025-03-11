package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * this is a beneficiary account verification request dto for dmt express .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressBeneficiaryAccountVerificationRequestDto {

  @NotBlank(message = "memberId cannot be blank.")
  @Pattern(
      regexp = RivilegeConstantService.MEMBER_ID_REGEX,
      message = "memberId must start with 'R' followed by exactly 9 alphanumeric characters (letters and digits)"
          + " without any special characters."
  )
  @Size(min = 10, max = 10, message = "Member ID must be exactly 10 characters long")
  private String memberId;

  @NotNull(message = "mobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotNull(message = "accountNumber cannot be null")
  @NotBlank(message = "accountNumber cannot be blank")
  @NotEmpty(message = "accountNumber cannot be empty")
  private String accountNumber;

  @NotNull(message = "ifscCode cannot be null")
  @NotBlank(message = "ifscCode cannot be blank")
  @NotEmpty(message = "ifscCode cannot be empty")
  private String ifscCode;

  @NotNull(message = "orderId cannot be null")
  @NotBlank(message = "orderId cannot be blank")
  @NotEmpty(message = "orderId cannot be empty")
  private String orderId;

}
