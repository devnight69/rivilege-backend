package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a send money request dto for dmt express .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressSendMoneyRequestDto {

  @NotNull(message = "customerMobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect customerMobileNumber")
  private String customerMobileNumber;

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

  @NotNull(message = "amount cannot be null")
  private double amount;

  private String comments;

}
