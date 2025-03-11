package com.rivilege.app.dto.cyrus.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;
import lombok.Data;

/**
 * this is a service type response dto .
 *
 * @author kousik manik
 */
@Data
public class ServiceTypeNameResponseDto {

  @JsonAlias("ServiceTypeName")
  private String serviceTypeName;

  private List<OperatorDto> data;

}
