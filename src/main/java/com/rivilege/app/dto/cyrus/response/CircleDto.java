package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * this is a circle dto .
 *
 * @author kousik manik
 */
@Data
public class CircleDto {
  @JsonProperty("circlecode")
  private String circleCode;

  @JsonProperty("circlename")
  private String circleName;
}
