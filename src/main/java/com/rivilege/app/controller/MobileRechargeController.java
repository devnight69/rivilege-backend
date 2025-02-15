package com.rivilege.app.controller;

import com.rivilege.app.dto.cyrus.request.MobileRechargePlanFetchRequestDto;
import com.rivilege.app.dto.cyrus.request.MobileRechargeRequestDto;
import com.rivilege.app.service.MobileRechargeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a mobile recharge controller .
 *
 * @author kousik mnaik
 */
@RestController
@RequestMapping("/api/v1/recharge")
public class MobileRechargeController {

  @Autowired
  private MobileRechargeService mobileRechargeService;


  @PostMapping("/getPlan")
  public ResponseEntity<?> getPlan(@Valid @RequestBody MobileRechargePlanFetchRequestDto dto) {
    return mobileRechargeService.getPlan(dto);
  }

  @PostMapping("/request")
  public ResponseEntity<?> rechargeRequest(@Valid @RequestBody MobileRechargeRequestDto dto) {
    return mobileRechargeService.rechargeRequest(dto);
  }


}
