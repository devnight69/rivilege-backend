package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * This is request dto for mobile recharge plan fetch .
 *
 * @author kousik manik
 */
@Data
public class MobileRechargePlanFetchRequestDto {

  @NotBlank(message = "operatorCode cannot be blank")
  @NotNull(message = "operatorCode cannot be null")
  @NotEmpty(message = "operatorCode cannot be empty")
  private String operatorCode;

  @NotBlank(message = "circleCode cannot be blank")
  @NotNull(message = "circleCode cannot be null")
  @NotEmpty(message = "circleCode cannot be empty")
  private String circleCode;

  @NotBlank(message = "mobileNumber cannot be blank")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

}
