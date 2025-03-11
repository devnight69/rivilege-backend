package com.rivilege.app.service;

import org.springframework.http.ResponseEntity;

/**
 * this is a operator service class .
 *
 * @author kousik manik
 */
public interface OperatorService {

  public void registerRechargeOperatorDetails();

  public ResponseEntity<?> getRechargeOperatorForPrepaid();

  public ResponseEntity<?> getRechargeOperatorForPostpaid();

  public ResponseEntity<?> getElectricityOperator();

  public ResponseEntity<?> getDthOperator();

}
