package com.rivilege.app.constant;

/**
 * this is a cyrus api constant service .
 *
 * @author kousik manik
 */
public class CyrusApiConstantService {

  public static final String GET_CIRCLE_API = "/api/GetOperator.aspx?memberid={memberId}&pin={pin}&Method=getcircle";

  public static final String GET_OPERATOR_API = "/api/GetOperator.aspx?memberid={memberId}&pin={pin}&Method=getoperator";

  public static final String GET_BALANCE_API = "/api/GetOperator.aspx?memberid={memberId}&pin={pin}&Method=getbalance";

  // DMT EXPRESS RELATED API

  public static final String GET_DMT_EXPRESS = "/services_cyapi/DMT2_cyapi.aspx";


  // Mobile Recharge Related Api

  public static final String MOBILE_PLAN_FETCH_API = "/API/CyrusPlanFatchAPI.aspx?APIID"
      + "={apiId}&PASSWORD={password}&Operator_Code={opCode}&Circle_Code={cCode}&MobileNumber={mobile}&data=ALL";


}
