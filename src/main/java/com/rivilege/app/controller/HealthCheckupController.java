package com.rivilege.app.controller;

import com.rivilege.app.service.HealthCheckupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * health check controller for cloud .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/health-check")
public class HealthCheckupController {

  @Autowired
  private HealthCheckupService healthCheckupService;

  @GetMapping("/status")
  public String healthCheck() {
    return healthCheckupService.healthCheckup();
  }

}
