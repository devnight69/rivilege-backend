package com.rivilege.app.controller;

import com.rivilege.app.constant.RivilegeConstantService;
import com.rivilege.app.service.AdminMemberIdActiveService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a admin active memberId Controller .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/member")
public class AdminMemberIdActiveController {

  @Autowired
  private AdminMemberIdActiveService adminMemberIdActiveService;

  @PutMapping("/{memberId}/active")
  public ResponseEntity<?> memberIdActivation(@Valid @PathVariable("memberId")
                                              @NotBlank(message = "memberId cannot be blank.")
                                              @Pattern(
                                                  regexp = RivilegeConstantService.MEMBER_ID_REGEX,
                                                  message = "memberId must start with 'R' followed by exactly 9"
                                                      + " alphanumeric characters (letters and digits)"
                                                      + " without any special characters."
                                              )
                                              @Size(min = 10, max = 10,
                                                  message = "Member ID must be exactly 10 characters long")
                                              String memberId) {
    return adminMemberIdActiveService.memberIdActivation(memberId);
  }

}
