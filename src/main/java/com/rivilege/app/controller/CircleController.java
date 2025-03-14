package com.rivilege.app.controller;

import com.rivilege.app.service.CircleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a circle controller class .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/circle")
public class CircleController {

  @Autowired
  private CircleService circleService;

  @GetMapping("/recharge/details")
  public ResponseEntity<?> getRechargeCircle() {
    return circleService.getRechargeCircle();
  }

}
