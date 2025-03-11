package com.rivilege.app.service;

import org.springframework.http.ResponseEntity;

/**
 * circle service class .
 *
 * @author kousik manik
 */
public interface CircleService {

  public void registerRechargeCircleDetails();

  public ResponseEntity<?> getRechargeCircle();

}
