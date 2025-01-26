package com.rivilege.app.customvalidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to validate memberId .
 *
 * @author kousik manik
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MemberIdValidator.class)
public @interface ValidMemberId {

  /**
   * Specifies the default error message when validation fails.
   *
   * @return The default error message.
   */
  String message() default "memberId must start with 'R' followed by exactly 9 alphanumeric characters"
      + " (letters and digits) without any special characters.";

  /**
   * Groups targeted for validation. Used for validation group segregation.
   *
   * @return The validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * Payloads associated with the constraint.
   *
   * @return The payload classes.
   */
  Class<? extends Payload>[] payload() default {};


}

