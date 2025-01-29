package com.rivilege.app.constant;


/**
 * this is a Rivilege constant service .
 *
 * @author kousik manik
 */
public class RivilegeConstantService {

  public static final String MOBILE_NUMBER_REGEX = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}$";

  public static final String EMAIL_REGEX = "^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

  public static final String PAN_REGEX = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$";

  public static final String AADHAAR_REGEX = "^[0-9]{12}$";

  public static final String PASSWORD_REGEX =
      "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!])[A-Za-z\\d@#$%^&+=!]{4,8}$";

  public static final String MEMBER_ID_REGEX = "^R[a-zA-Z0-9]{9}$";


  public static final String BBPS_RECHARGE_TYPE = "RECHARGE";

  public static final String BBPS_RECHARGE_TYPE_POSTPAID = "Mobile Postpaid";

  public static final String BBPS_RECHARGE_TYPE_PREPAID = "Prepaid-Mobile";

  public static final String BBPS_RECHARGE_TYPE_DTH = "DTH";

  public static final String BBPS_RECHARGE_TYPE_ELECTRICITY = "Electricity";

}
