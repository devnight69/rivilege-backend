package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * this is a response dto for account verification .
 *
 * @author kousik manik .
 */
@Data
public class BeneficiaryAccountVerificationResponseDto {


  @JsonAlias("statuscode")
  private String statusCode;
  @JsonAlias("status")
  private String status;
  @JsonAlias("data")
  private TransactionData data;

  /**
   * transaction dto .
   */
  @Data
  public static class TransactionData {
    @JsonAlias("cyrusOrderId")
    private String cyrusOrderId;
    @JsonAlias("orderId")
    private String orderId;
    @JsonAlias("cyrus_id")
    private String cyrusId;
    @JsonAlias("remarks")
    private String remarks;
    @JsonAlias("bankrefno")
    private String bankRefNo;
    @JsonAlias("benename")
    private String beneName;
    @JsonAlias("locked_amt")
    private String lockedAmt;
    @JsonAlias("charged_amt")
    private String chargedAmt;
    @JsonAlias("opening_bal")
    private String openingBal;
    @JsonAlias("verification_status")
    private String verificationStatus;
  }

}
