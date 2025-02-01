package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Data;

/**
 * this is a mobile recharge related cyrus response dto .
 *
 * @author kousik manik
 */
@Data
public class MobileRechargeCyrusResponseDto {

  @JsonAlias("Status")  // Match exact case from JSON
  private String status;

  @JsonAlias("PlanDescription")  // Match exact case from JSON
  private List<Object> planDescription;
}
