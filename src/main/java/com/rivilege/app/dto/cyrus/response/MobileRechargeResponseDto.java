package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * this is a mobile recharge response dto .
 *
 * @author kousik manik
 */
@Data
public class MobileRechargeResponseDto {

  @JsonAlias("ApiTransID")
  private String apiTransId;

  @JsonAlias("Status")
  private String status;

  @JsonAlias("ErrorMessage")
  private String errorMessage;

  @JsonAlias("OperatorRef")
  private String operatorRef;

  @JsonAlias("TransactionDate")
  private String transactionDate;

}
