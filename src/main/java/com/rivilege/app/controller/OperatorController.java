package com.rivilege.app.controller;

import com.rivilege.app.service.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a operator controller class .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/operator")
public class OperatorController {

  @Autowired
  private OperatorService operatorService;


  @GetMapping("/mobile-prepaid")
  public ResponseEntity<?> getRechargeOperatorForPrepaid() {
    return operatorService.getRechargeOperatorForPrepaid();
  }

  @GetMapping("/mobile-postpaid")
  public ResponseEntity<?> getRechargeOperatorForPostpaid() {
    return operatorService.getRechargeOperatorForPostpaid();
  }

  @GetMapping("/electricity")
  public ResponseEntity<?> getElectricityOperator() {
    return operatorService.getElectricityOperator();
  }

  @GetMapping("/dth")
  public ResponseEntity<?> getDthOperator() {
    return operatorService.getDthOperator();
  }

}
