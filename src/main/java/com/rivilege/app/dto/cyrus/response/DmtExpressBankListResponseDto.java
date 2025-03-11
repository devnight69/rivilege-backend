package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Data;

/**
 * this is a dmt express bank list response dto .
 *
 * @author kousik manik
 */
@Data
public class DmtExpressBankListResponseDto {

  @JsonAlias("status")  // Match exact case from JSON
  private String status;

  @JsonAlias("statuscode")  // Match exact case from JSON
  private String statusCode;

  @JsonAlias("data")  // Match exact case from JSON
  private List<Object> data;


}
