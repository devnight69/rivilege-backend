package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * this is a mobile recharge request dto .
 *
 * @author kousik manik
 */
@Data
public class MobileRechargeRequestDto {

  @NotBlank(message = "memberId cannot be blank.")
  @Pattern(
      regexp = RivilegeConstantService.MEMBER_ID_REGEX,
      message = "memberId must start with 'R' followed by exactly 9 alphanumeric characters (letters and digits)"
          + " without any special characters."
  )
  @Size(min = 10, max = 10, message = "Member ID must be exactly 10 characters long")
  private String memberId;

  @NotBlank(message = "operator cannot be blank")
  @NotNull(message = "operator cannot be null")
  @NotEmpty(message = "operator cannot be empty")
  private String operator;

  @NotBlank(message = "mobileNumber cannot be blank")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotBlank(message = "circle cannot be blank")
  @NotNull(message = "circle cannot be null")
  @NotEmpty(message = "circle cannot be empty")
  private String circle;

  @NotBlank(message = "amount cannot be blank")
  @NotNull(message = "amount cannot be null")
  @NotEmpty(message = "amount cannot be empty")
  private String amount;

}
