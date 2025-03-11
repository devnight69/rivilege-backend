package com.rivilege.app.service;

import com.rivilege.app.dto.cyrus.request.MobileRechargePlanFetchRequestDto;
import com.rivilege.app.dto.cyrus.request.MobileRechargeRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a mobile recharge service class .
 *
 * @author kousik manik
 */
public interface MobileRechargeService {

  public ResponseEntity<?> getPlan(MobileRechargePlanFetchRequestDto dto);

  public ResponseEntity<?> rechargeRequest(MobileRechargeRequestDto dto);

}
