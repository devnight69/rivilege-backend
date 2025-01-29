package com.rivilege.app.dto.cyrus.request;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * this is a dmt express registration request dto .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressRegistrationRequestDto {

  @NotBlank(message = "fistName cannot be blank")
  @NotEmpty(message = "firstName cannot be empty")
  @NotNull(message = "firstName cannot be null")
  private String firstName;

  @NotBlank(message = "lastName cannot be blank")
  @NotEmpty(message = "lastName cannot be empty")
  @NotNull(message = "lastName cannot be null")
  private String lastName;

  @NotNull(message = "mobileNumber cannot be null")
  @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX, message = "Incorrect mobileNumber")
  private String mobileNumber;

  @NotNull(message = "Date of birth is required")
  @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})$",
      message = "Date of birth must be in the format dd-MM-yy (e.g., 25-10-1900)")
  private String dateOfBirth;

  // Validate pinCode: it should be a 6-digit number
  @NotNull(message = "Pin code is required")
  @Pattern(regexp = "^[0-9]{6}$", message = "Pin code must be a 6-digit number")
  private String pinCode;

  @NotBlank(message = "address cannot be blank")
  @NotEmpty(message = "address cannot be empty")
  @NotNull(message = "address cannot be null")
  private String address;

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
