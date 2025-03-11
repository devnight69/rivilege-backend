package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Data;

/**
 * get balance cyrus response dto .
 *
 * @author kousik manik
 */
@Data
public class GetBalanceCyrusResponseDto {

  @JsonAlias("Status")
  private String status;

  @JsonAlias("SuccessMessage")
  private String successMessage;

  @JsonAlias("data")
  private List<BalanceDto> data;

  /**
   * balance dto .
   */
  @Data
  public static class BalanceDto {
    @JsonAlias("balance")
    private double balance;
  }

}
