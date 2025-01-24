package com.rivilege.app.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * this is custom error response class for jwt related errors .
 *
 * @author kousik manik
 */
@Data
@AllArgsConstructor
public class CustomErrorResponse {
  private String errorCode;
  private String errorMessage;
}


