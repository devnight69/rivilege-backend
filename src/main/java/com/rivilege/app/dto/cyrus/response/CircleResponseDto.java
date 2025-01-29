package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * this is a circle response dto .
 *
 * @author kousik manik
 */
@Data
public class CircleResponseDto {
  @JsonProperty("Status")  // Match exact case from JSON
  private String status;

  @JsonProperty("SuccessMessage")  // Match exact case from JSON
  private String successMessage;

  @JsonProperty("data")  // Match exact case from JSON
  private List<CircleDto> data;
}
