package com.rivilege.app.serviceimpl;

import com.rivilege.app.service.HealthCheckupService;
import org.springframework.stereotype.Service;

/**
 * this is a health checkup implementation class for server .
 *
 * @author kousik manik
 */
@Service
public class HealthCheckupServiceImpl implements HealthCheckupService {


  /**
   * this is health checkup method .
   *
   * @return @{@link String}
   */
  @Override
  public String healthCheckup() {

    return "RIVILEGE-APP-SERVICE";

  }
}
