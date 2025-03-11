package com.rivilege.app.serviceimpl;

import com.rivilege.app.constant.CyrusApiConstantService;
import com.rivilege.app.constant.RivilegeConstantService;
import com.rivilege.app.dto.cyrus.response.OperatorDto;
import com.rivilege.app.dto.cyrus.response.OperatorResponseDto;
import com.rivilege.app.model.OperatorDetails;
import com.rivilege.app.repository.OperatorDetailsRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.OperatorService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * this is a OperatorService implementation class .
 *
 * @author kousik manik
 */
@Service
public class OperatorServiceImpl implements OperatorService {

  @Value("${cyrus-api-member-id}")
  private String cyrusApiMemberId;

  @Value("${cyrus-recharge-api-key}")
  private String cyrusRechargeApiKey;

  @Value("${cyrus-recharge-api-endpoint}")
  private String cyrusRechargeApiEndpoint;

  @Autowired
  private OperatorDetailsRepository operatorDetailsRepository;

  @Autowired
  private BaseResponse baseResponse;

  @Autowired
  private RestTemplate restTemplate;

  private static final Logger logger = LoggerFactory.getLogger(OperatorServiceImpl.class);

  /**
   * Service to handle registration of recharge operator details.
   */
  @Override
  public void registerRechargeOperatorDetails() {
    logger.info("Starting the process to register recharge operator details.");

    try {
      // Construct the API URL with placeholders replaced
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_OPERATOR_API
          .replace("{memberId}", cyrusApiMemberId)
          .replace("{pin}", cyrusRechargeApiKey);

      logger.info("Constructed API URL: {}", url);

      // Set up HTTP headers for the request
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

      HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

      // Make the API call
      logger.info("Sending request to fetch operator details...");
      List<OperatorResponseDto> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          entity,
          new ParameterizedTypeReference<List<OperatorResponseDto>>() {
          }
      ).getBody();

      if (response.isEmpty()) {
        logger.warn("API response is null or empty. No operator details to process.");
        return;
      }

      logger.info("Successfully fetched operator details from the API.");

      // Transform the API response into OperatorDetails entities

      List<OperatorDetails> operatorDetailsList = new ArrayList<>();

      response.getFirst().getData().forEach(data -> {
        if (data == null || data.getData() == null) {
          logger.warn("Encountered null data or operator list. Skipping...");
          return;
        }

        data.getData().forEach(operator -> {
          if (operator == null) {
            logger.warn("Encountered a null operator entry. Skipping...");
            return;
          }

          OperatorDetails operatorDetails = new OperatorDetails();
          operatorDetails.setOperatorCode(operator.getOperatorCode());
          operatorDetails.setOperatorName(operator.getOperatorName());
          operatorDetails.setServiceTypeName(data.getServiceTypeName());
          operatorDetailsList.add(operatorDetails);
        });
      });


      logger.info("Total operator details prepared for database insertion: {}", operatorDetailsList.size());

      // Delete existing records and save the new ones
      logger.info("Clearing existing operator details from the database...");
      operatorDetailsRepository.deleteAll();

      logger.info("Saving {} operator details to the database...", operatorDetailsList.size());
      operatorDetailsRepository.saveAllAndFlush(operatorDetailsList);

      logger.info("Successfully registered {} operator details.", operatorDetailsList.size());
    } catch (Exception e) {
      logger.error("An error occurred while registering recharge operator details: {}", e.getMessage(), e);
    }
  }

  /**
   * Generic method to fetch and process operator details based on service type.
   *
   * @param serviceType the service type name (e.g., prepaid, postpaid, DTH, electricity).
   * @param serviceName the name of the service for logging (e.g., "prepaid", "postpaid").
   * @return ResponseEntity containing a list of OperatorDto objects or an empty list if no operators are found.
   */
  private ResponseEntity<?> fetchOperators(String serviceType, String serviceName) {
    logger.info("Fetching {} operators...", serviceName);
    try {
      // Fetch operator details based on service type
      List<OperatorDetails> operatorDetailsList = operatorDetailsRepository.findByServiceTypeName(serviceType);

      if (operatorDetailsList.isEmpty()) {
        logger.warn("No {} operators found.", serviceName);
        return baseResponse.successResponse(List.of());
      }

      // Convert OperatorDetails to OperatorDto using Stream API
      List<OperatorDto> operatorDtoList = operatorDetailsList.stream()
          .map(operatorDetails -> {
            OperatorDto operatorDto = new OperatorDto();
            operatorDto.setOperatorCode(operatorDetails.getOperatorCode());
            operatorDto.setOperatorName(operatorDetails.getOperatorName());
            return operatorDto;
          })
          .toList();

      logger.info("Successfully fetched {} {} operators.", operatorDtoList.size(), serviceName);
      return baseResponse.successResponse(operatorDtoList);

    } catch (Exception e) {
      logger.error("Error occurred while fetching {} operators: {}", serviceName, e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while processing your request.");
    }
  }

  /**
   * Fetches the list of recharge operators for prepaid services.
   */
  @Override
  public ResponseEntity<?> getRechargeOperatorForPrepaid() {
    return fetchOperators(RivilegeConstantService.BBPS_RECHARGE_TYPE_PREPAID, "prepaid");
  }

  /**
   * Fetches the list of recharge operators for postpaid services.
   */
  @Override
  public ResponseEntity<?> getRechargeOperatorForPostpaid() {
    return fetchOperators(RivilegeConstantService.BBPS_RECHARGE_TYPE_POSTPAID, "postpaid");
  }

  /**
   * Fetches the list of electricity operators.
   */
  @Override
  public ResponseEntity<?> getElectricityOperator() {
    return fetchOperators(RivilegeConstantService.BBPS_RECHARGE_TYPE_ELECTRICITY, "electricity");
  }

  /**
   * Fetches the list of DTH operators.
   */
  @Override
  public ResponseEntity<?> getDthOperator() {
    return fetchOperators(RivilegeConstantService.BBPS_RECHARGE_TYPE_DTH, "DTH");
  }



}
