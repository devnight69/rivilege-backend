package com.rivilege.app.service;

import com.rivilege.app.dto.cyrus.request.MobileRechargePlanFetchRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a mobile recharge service class .
 *
 * @author kousik manik
 */
public interface MobileRechargeService {

  public ResponseEntity<?> getPlan(MobileRechargePlanFetchRequestDto dto);

}
