package com.rivilege.app.dto.request;

import com.rivilege.app.constant.RivilegeConstantService;
import com.rivilege.app.enums.UserDesignationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * this is a registration request dto .
 *
 * @author kousik manik
 */
@Data
public class RegistrationUserRequestDto {

  @NotBlank(message = "fullName cannot be blank")
  @NotNull(message = "fullName cannot be null")
  @NotEmpty(message = "fullName cannot be empty")
  @Size(min = 3, max = 250, message = "Full name must be between 3 and 250 characters.")
  private String fullName;

  @NotNull(message = "emailId cannot be null")
  @Pattern(regexp = RivilegeConstantService.EMAIL_REGEX, message = "Incorrect Email ID")
  private String emailId;

  @NotNull(message = "mobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotNull(message = "userDesignation cannot be null")
  private UserDesignationType userDesignation;

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

  @NotBlank(message = "password cannot be blank.")
  @Pattern(
      regexp = RivilegeConstantService.PASSWORD_REGEX,
      message = "Password must be 4-8 characters long, contain at least one uppercase letter,"
          + " one lowercase letter, one number, and one special character."
  )
  private String password;

}
