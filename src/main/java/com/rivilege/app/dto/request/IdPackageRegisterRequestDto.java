package com.rivilege.app.dto.request;

import com.rivilege.app.enums.UserDesignationType;
import lombok.Data;

/**
 * this is a id package register request dto .
 *
 * @author kousik manik
 */
@Data
public class IdPackageRegisterRequestDto {

  private UserDesignationType userDesignation;

  private double joiningAmount;

  private double joiningGstAmount;

  private Integer gstPercentage;

}
