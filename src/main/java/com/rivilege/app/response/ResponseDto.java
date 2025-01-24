package com.rivilege.app.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * this is a response dto .
 *
 * @author kousik manik
 */
@Data
public class ResponseDto {

  boolean response;
  String message;
  Object data;
  HttpStatus status;
  LocalDateTime timestamp;

  /**
   * this is a to json method .
   *
   * @return @{@link String}
   */
  public String toJson() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(this);
    } catch (Exception e) {
      return "{}"; // Handle serialization exception
    }
  }

}

