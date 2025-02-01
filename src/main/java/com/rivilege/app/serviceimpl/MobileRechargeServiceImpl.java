package com.rivilege.app.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivilege.app.constant.CyrusApiConstantService;
import com.rivilege.app.dto.cyrus.request.MobileRechargePlanFetchRequestDto;
import com.rivilege.app.dto.cyrus.response.GetBalanceCyrusResponseDto;
import com.rivilege.app.dto.cyrus.response.MobileRechargeCyrusResponseDto;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.MobileRechargeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * this is a Mobile Recharge Service Implementation class .
 *
 * @author kousik manik
 */
@Service
public class MobileRechargeServiceImpl implements MobileRechargeService {


  @Value("${cyrus-api-member-id}")
  private String cyrusApiId;

  @Value("${cyrus-plan-fetch-api-key}")
  private String cyrusPlanFetchKey;

  @Value("${cyrus-recharge-api-endpoint}")
  private String cyrusRechargeApiEndpoint;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(MobileRechargeServiceImpl.class);

  /**
   * Retrieves the balance from the Cyrus Recharge API.
   *
   * @return The balance amount.
   * @throws RuntimeException if the API request fails or returns invalid data.
   */
  private double getBalance() {
    Logger logger = LoggerFactory.getLogger(getClass());

    try {
      logger.info("Fetching balance from Cyrus Recharge API");

      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_BALANCE_API
          .replace("{memberId}", cyrusApiId)
          .replace("{pin}", cyrusPlanFetchKey);

      // Create headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Combine headers and body
      HttpEntity<?> requestEntity = new HttpEntity<>(headers);

      // Send the request and get the response
      var response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();

      GetBalanceCyrusResponseDto resp = objectMapper.readValue(response, GetBalanceCyrusResponseDto.class);

      if (resp == null || resp.getData() == null || resp.getData().isEmpty()) {
        logger.warn("Balance data is missing in the response");
        throw new RuntimeException("Balance data is unavailable. Please try again later.");
      }

      double balance = resp.getData().getFirst().getBalance();
      logger.info("Balance retrieved successfully: {}", balance);

      return balance;

    } catch (Exception e) {
      logger.error("Error fetching balance from Cyrus API", e);
      throw new RuntimeException("Failed to retrieve balance. Please try again later.");
    }
  }

  /**
   * Fetches the mobile recharge plan using the Cyrus Recharge API.
   *
   * @param dto The request DTO containing operator code, circle code, and mobile number.
   * @return ResponseEntity with the recharge plan details on success,
   *         or an error message if the request fails.
   */
  @Override
  public ResponseEntity<?> getPlan(MobileRechargePlanFetchRequestDto dto) {
    try {
      logger.info("Fetching mobile recharge plan for mobile: {}, operator: {}, circle: {}",
          dto.getMobileNumber(), dto.getOperatorCode(), dto.getCircleCode());

      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.MOBILE_PLAN_FETCH_API
          .replace("{apiId}", cyrusApiId)
          .replace("{password}", cyrusPlanFetchKey)
          .replace("{opCode}", dto.getOperatorCode())
          .replace("{cCode}", dto.getCircleCode())
          .replace("{mobile}", dto.getMobileNumber());

      logger.info("Recharge Plan Fetch Url: {}", url);

      // Create headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Combine headers and body
      HttpEntity<?> requestEntity = new HttpEntity<>(headers);

      // Send the request and get the response
      var response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();

      MobileRechargeCyrusResponseDto resp = objectMapper.readValue(response, MobileRechargeCyrusResponseDto.class);

      logger.info("Successfully fetched recharge plan for mobile: {}", dto.getMobileNumber());
      return baseResponse.successResponse(resp);

    } catch (Exception e) {
      logger.error("Error fetching recharge plan for mobile: {}", dto.getMobileNumber(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to fetch recharge plan. Please try again later.");
    }
  }


}
