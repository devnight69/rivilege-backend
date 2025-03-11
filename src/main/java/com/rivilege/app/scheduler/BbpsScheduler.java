package com.rivilege.app.scheduler;

import com.rivilege.app.service.CircleService;
import com.rivilege.app.service.OperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * this is a bbps scheduler class .
 *
 * @author kousik manik
 */
@Component
public class BbpsScheduler {

  @Autowired
  private CircleService circleService;

  @Autowired
  private OperatorService operatorService;

  private static final Logger logger = LoggerFactory.getLogger(BbpsScheduler.class);


  @Scheduled(cron = "0 0 0 * * MON")
  private void fetchOperatorForRecharge() {
    circleService.registerRechargeCircleDetails();
    operatorService.registerRechargeOperatorDetails();

  }

}
