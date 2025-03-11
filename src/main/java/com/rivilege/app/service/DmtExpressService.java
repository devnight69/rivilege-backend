package com.rivilege.app.service;

import com.rivilege.app.dto.cyrus.request.DmtExpressAddBeneficiaryRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressAddKycRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressBeneficiaryAccountVerificationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressRegistrationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressSendMoneyRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressVerifyKycRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a dmt express service .
 *
 * @author kousik manik
 */
public interface DmtExpressService {

  public ResponseEntity<?> getBankList();

  public ResponseEntity<?> getCustomerDetails(String mobileNumber);

  public ResponseEntity<?> customerRegistration(DmtExpressRegistrationRequestDto dto);

  public ResponseEntity<?> addKycDetails(DmtExpressAddKycRequestDto dto);

  public ResponseEntity<?> verifyKycDetails(DmtExpressVerifyKycRequestDto dto);

  public ResponseEntity<?> getBeneficiaryDetails(DmtExpressAddKycRequestDto dto);

  public ResponseEntity<?> beneficiaryAccountVerification(DmtExpressBeneficiaryAccountVerificationRequestDto dto);

  public ResponseEntity<?> addBeneficiary(DmtExpressAddBeneficiaryRequestDto dto);

  public ResponseEntity<?> removeBeneficiaryAccount(String beneficiaryId);

  public ResponseEntity<?> sendMoney(DmtExpressSendMoneyRequestDto dto);

  public ResponseEntity<?> statusCheck(String orderId);


}
