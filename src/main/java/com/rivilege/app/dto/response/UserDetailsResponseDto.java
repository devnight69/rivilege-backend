package com.rivilege.app.dto.response;

import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.enums.UserType;
import java.util.Date;
import lombok.Data;

/**
 * this is a user details response dto .
 *
 * @author kousik manik
 */
@Data
public class UserDetailsResponseDto {

  private String ulId;

  private String fullName;

  private String memberId;

  private String emailId;

  private String mobileNumber;

  private UserDesignationType userDesignation;

  private UserType userType;

  private String panNumber;

  private String aadhaarNumber;

  private double mainWallet;

  private double rechargeWallet;

  private double commissionWallet;

  private double bankingWallet;

  private boolean active;

  private boolean blockUser;

  private Date createdAt;

  private Date updatedAt;

}
