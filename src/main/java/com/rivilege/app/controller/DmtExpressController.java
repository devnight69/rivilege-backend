package com.rivilege.app.controller;

import com.rivilege.app.constant.RivilegeConstantService;
import com.rivilege.app.dto.cyrus.request.DmtExpressAddBeneficiaryRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressAddKycRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressBeneficiaryAccountVerificationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressRegistrationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressSendMoneyRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressVerifyKycRequestDto;
import com.rivilege.app.service.DmtExpressService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a dmt express controller class .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/dmt-express")
public class DmtExpressController {

  @Autowired
  private DmtExpressService dmtExpressService;


  @GetMapping("/getBankList")
  public ResponseEntity<?> getBankList() {
    return dmtExpressService.getBankList();
  }

  @GetMapping("/getCustomerDetails")
  public ResponseEntity<?> getCustomerDetails(@Valid @RequestParam
                                              @NotNull(message = "mobileNumber cannot be null")
                                              @NotBlank(message = "mobileNumber cannot be blank")
                                              @Pattern(regexp = RivilegeConstantService.MOBILE_NUMBER_REGEX,
                                                  message = "Incorrect mobile number.")
                                              String mobileNumber) {
    return dmtExpressService.getCustomerDetails(mobileNumber);
  }

  @PostMapping("/customerRegistration")
  public ResponseEntity<?> customerRegistration(@Valid @RequestBody DmtExpressRegistrationRequestDto dto) {
    return dmtExpressService.customerRegistration(dto);
  }

  @PostMapping("/addKycDetails")
  public ResponseEntity<?> addKycDetails(@Valid @RequestBody DmtExpressAddKycRequestDto dto) {
    return dmtExpressService.addKycDetails(dto);
  }

  @PostMapping("/verifyKycDetails")
  public ResponseEntity<?> verifyKycDetails(@Valid @RequestBody DmtExpressVerifyKycRequestDto dto) {
    return dmtExpressService.verifyKycDetails(dto);
  }

  @PostMapping("/getBeneficiaryDetails")
  public ResponseEntity<?> getBeneficiaryDetails(@Valid @RequestBody DmtExpressAddKycRequestDto dto) {
    return dmtExpressService.getBeneficiaryDetails(dto);
  }

  @PostMapping("/beneficiary-account-verification")
  public ResponseEntity<?> beneficiaryAccountVerification(@Valid @RequestBody
                                                          DmtExpressBeneficiaryAccountVerificationRequestDto dto) {
    return dmtExpressService.beneficiaryAccountVerification(dto);
  }

  @PostMapping("/add/beneficiary")
  public ResponseEntity<?> addBeneficiary(@Valid @RequestBody DmtExpressAddBeneficiaryRequestDto dto) {
    return dmtExpressService.addBeneficiary(dto);
  }

  @PostMapping("/remove-beneficiary-account")
  public ResponseEntity<?> removeBeneficiaryAccount(@Valid @RequestParam("beneficiaryId")
                                                    @NotBlank(message = "beneficiaryId cannot be blank")
                                                    @NotNull(message = "beneficiaryId cannot be null")
                                                    @NotEmpty(message = "beneficiaryId cannot be empty")
                                                    String beneficiaryId) {
    return dmtExpressService.removeBeneficiaryAccount(beneficiaryId);
  }

  @PostMapping("/send/money")
  public ResponseEntity<?> sendMoney(@Valid @RequestBody DmtExpressSendMoneyRequestDto dto) {
    return dmtExpressService.sendMoney(dto);
  }

  @GetMapping("/status-check")
  public ResponseEntity<?> statusCheck(@Valid @RequestParam("orderId")
                                       @NotNull(message = "orderId cannot be null")
                                       @NotBlank(message = "orderId cannot be blank")
                                       @NotEmpty(message = "orderId cannot be empty") String orderId) {
    return dmtExpressService.statusCheck(orderId);
  }


}
