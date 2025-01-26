package com.rivilege.app.customvalidator;

import com.rivilege.app.constant.RivilegeConstantService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Validates the slot type field.
 * Ensures the value is either null, "BOTH", or "TELE" (case-insensitive).
 *
 * @author kousik manik
 */
public class MemberIdValidator implements ConstraintValidator<ValidMemberId, String> {


  private static final Pattern MEMBER_ID_PATTERN = Pattern.compile(RivilegeConstantService.MEMBER_ID_REGEX);

  /**
   * Checks if the given value is valid.
   *
   * @param value   the slot type value to validate
   * @param context the validation context
   * @return true if the value match the memberId pattern
   */
  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // Return true if value is null
    if (value == null) {
      return true;
    }

    return MEMBER_ID_PATTERN.matcher(value).matches();
  }
}

