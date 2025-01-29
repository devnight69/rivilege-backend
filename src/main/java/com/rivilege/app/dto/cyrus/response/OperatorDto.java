package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * this is a operator dto .
 *
 * @author kousik manik
 */
@Data
public class OperatorDto {

  @JsonAlias("OperatorCode")
  private String operatorCode;

  @JsonAlias("OperatorName")
  private String operatorName;

}
