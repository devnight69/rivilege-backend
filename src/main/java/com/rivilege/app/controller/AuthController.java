package com.rivilege.app.controller;

import com.rivilege.app.constant.RivilegeConstantService;
import com.rivilege.app.dto.request.LogInRequestDto;
import com.rivilege.app.dto.request.RegistrationUserRequestDto;
import com.rivilege.app.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a controller class for auth .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @Autowired
  private AuthService authService;


  @PostMapping("/register/member")
  public ResponseEntity<?> registerNewMember(@Valid @RequestBody RegistrationUserRequestDto dto) {
    return authService.registerNewMember(dto);
  }

  @GetMapping("/referral/member/details")
  public ResponseEntity<?> getReferralUserDetails(@Valid @RequestParam(name = "memberId")
                                                  @NotBlank(message = "memberId cannot be blank.")
                                                  @Pattern(
                                                      regexp = RivilegeConstantService.MEMBER_ID_REGEX,
                                                      message = "memberId must start with 'R' followed by exactly"
                                                          + " 9 alphanumeric characters (letters and digits)"
                                                          + " without any special characters."
                                                  )
                                                  @Size(min = 10, max = 10,
                                                      message = "Member ID must be exactly 10 characters long")
                                                  String memberId) {
    return authService.getReferralUserDetails(memberId);
  }

  @PostMapping("/login")
  public ResponseEntity<?> loginUser(@Valid @RequestBody LogInRequestDto dto) {
    return authService.loginUser(dto);
  }

}
