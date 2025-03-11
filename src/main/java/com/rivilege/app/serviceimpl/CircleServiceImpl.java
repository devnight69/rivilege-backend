package com.rivilege.app.serviceimpl;

import com.rivilege.app.constant.CyrusApiConstantService;
import com.rivilege.app.constant.RivilegeConstantService;
import com.rivilege.app.dto.cyrus.response.CircleDto;
import com.rivilege.app.dto.cyrus.response.CircleResponseDto;
import com.rivilege.app.model.BbpsCircleDetails;
import com.rivilege.app.repository.BbpsCircleDetailsRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.CircleService;
import java.util.List;
import java.util.stream.Collectors;
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
 * this is a circle service implementation class .
 *
 * @author kousik manik
 */
@Service
public class CircleServiceImpl implements CircleService {

  @Value("${cyrus-api-member-id}")
  private String cyrusApiMemberId;

  @Value("${cyrus-recharge-api-key}")
  private String cyrusRechargeApiKey;

  @Value("${cyrus-recharge-api-endpoint}")
  private String cyrusRechargeApiEndpoint;

  @Autowired
  private BbpsCircleDetailsRepository bbpsCircleDetailsRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(CircleServiceImpl.class);


  /**
   * This method registers recharge circle details by fetching data from the Cyrus Recharge API.
   */
  @Override
  public void registerRechargeCircleDetails() {

    try {
      // Replace placeholders with actual values
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_CIRCLE_API
          .replace("{memberId}", cyrusApiMemberId)
          .replace("{pin}", cyrusRechargeApiKey);

      // Log the request URL
      logger.info("Fetching recharge circle details from URL: {}", url);

      // Set up HTTP headers
      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

      // Prepare the HTTP entity
      HttpEntity<?> entity = new HttpEntity<>(httpHeaders);

      // Make the API call
      var response = restTemplate.exchange(url, HttpMethod.GET, entity,
          new ParameterizedTypeReference<List<CircleResponseDto>>() {
          }).getBody();

      if (response.isEmpty()) {
        logger.warn("Received empty or null response from the API.");
        return;
      }

      // Convert response data to entity list
      List<BbpsCircleDetails> bbpsCircleDetailsList = response.getFirst().getData().stream()
          .map(circleDto -> {
            BbpsCircleDetails bbpsCircleDetails = new BbpsCircleDetails();
            bbpsCircleDetails.setCircleCode(circleDto.getCircleCode());
            bbpsCircleDetails.setCircleName(circleDto.getCircleName());
            bbpsCircleDetails.setBbpsType(RivilegeConstantService.BBPS_RECHARGE_TYPE);
            return bbpsCircleDetails;
          })
          .toList();

      // Log the number of records to be saved
      logger.info("Number of recharge circle details fetched: {}", bbpsCircleDetailsList.size());

      // Delete existing records and save new ones
      bbpsCircleDetailsRepository.deleteAll();
      logger.info("Cleared existing circle details from the database.");

      bbpsCircleDetailsRepository.saveAllAndFlush(bbpsCircleDetailsList);
      logger.info("Saved {} recharge circle details to the database.", bbpsCircleDetailsList.size());

    } catch (Exception e) {
      // Log the exception with stack trace
      logger.error("Error occurred while registering recharge circle details: {}", e.getMessage(), e);
    }
  }

  /**
   * Fetches recharge circles based on BBPS type.
   *
   * @return ResponseEntity containing a list of CircleDto or an error response.
   */
  @Override
  public ResponseEntity<?> getRechargeCircle() {
    logger.info("Fetching recharge circle details.");

    try {
      List<BbpsCircleDetails> bbpsCircleDetailsList = bbpsCircleDetailsRepository.findByBbpsType(
          RivilegeConstantService.BBPS_RECHARGE_TYPE
      );

      if (bbpsCircleDetailsList.isEmpty()) {
        logger.info("No recharge circles found.");
        return baseResponse.successResponse(List.of());
      }

      List<CircleDto> circleDtoList = bbpsCircleDetailsList.stream()
          .map(bbpsCircleDetails -> {
            CircleDto circleDto = new CircleDto();
            circleDto.setCircleCode(bbpsCircleDetails.getCircleCode());
            circleDto.setCircleName(bbpsCircleDetails.getCircleName());
            return circleDto;
          })
          .collect(Collectors.toList());

      logger.info("Successfully fetched {} recharge circles.", circleDtoList.size());
      return baseResponse.successResponse(circleDtoList);

    } catch (Exception e) {
      logger.error("Error while fetching recharge circle details: {}", e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while fetching data.");
    }
  }

}
