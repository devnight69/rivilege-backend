package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Data;

/**
 * dmt express add kyc response dto .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressAddKycResponseDto {

  @JsonAlias("status")  // Match exact case from JSON
  private String status;

  @JsonAlias("statuscode")  // Match exact case from JSON
  private String statusCode;

  @JsonAlias("data")  // Match exact case from JSON
  private List<AddKycDto> data;

  /**
   * add kyc dto .
   */
  @Data
  public static class AddKycDto {

    @JsonAlias("STS_CODE")
    private String stsCode;

    @JsonAlias("MESSAGE")
    private String message;
  }

}
