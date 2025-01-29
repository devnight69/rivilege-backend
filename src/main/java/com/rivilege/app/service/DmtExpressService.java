package com.rivilege.app.service;

import com.rivilege.app.dto.cyrus.request.DmtExpressAddKycRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressRegistrationRequestDto;
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


}
