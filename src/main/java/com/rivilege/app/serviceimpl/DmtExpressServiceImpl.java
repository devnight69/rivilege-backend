package com.rivilege.app.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivilege.app.constant.CyrusApiConstantService;
import com.rivilege.app.dto.cyrus.request.DmtExpressAddKycRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressRegistrationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressVerifyKycRequestDto;
import com.rivilege.app.dto.cyrus.response.DmtExpressBankListResponseDto;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.DmtExpressService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * this is a dmt express service implementation .
 *
 * @author kousik manik
 */
@Service
public class DmtExpressServiceImpl implements DmtExpressService {

  @Value("${cyrus-api-member-id}")
  private String cyrusApiMemberId;

  @Value("${cyrus-dmt-express-api-key}")
  private String cyrusDmtExpressApiKey;

  @Value("${cyrus-recharge-api-endpoint}")
  private String cyrusRechargeApiEndpoint;

  @Autowired
  private BaseResponse baseResponse;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  private static final Logger logger = LoggerFactory.getLogger(DmtExpressServiceImpl.class);

  /**
   * Fetches the list of banks from the Cyrus Recharge API.
   *
   * @return ResponseEntity containing a list of banks if data exists,
   * or an empty list if no data is found. Returns an error response
   * with HTTP status 500 in case of any issues during the process.
   */
  @Override
  public ResponseEntity<?> getBankList() {
    logger.info("Initiating request to fetch bank list from Cyrus Recharge API...");

    try {
      // Prepare the URL
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Create headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Create the request body
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "banklist");

      // Combine headers and body
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Send the request and get the response
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      // Use ObjectMapper to deserialize the JSON response to DmtExpressBankListResponseDto
      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Successfully fetched {} banks from Cyrus Recharge API.", resp.getData().size());
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error occurred while fetching the bank list: {}", e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while processing your request.");
    }
  }

  /**
   * Fetches customer details from the Cyrus Recharge API using the provided mobile number.
   *
   * @param mobileNumber The mobile number of the customer for which details are to be fetched.
   * @return ResponseEntity containing the customer details if found, or an error message if not.
   * Returns an error response with HTTP status 500 in case of any issues during the process.
   */
  @Override
  public ResponseEntity<?> getCustomerDetails(String mobileNumber) {
    logger.info("Fetching customer details for mobile number: {}", mobileNumber);

    try {
      // Prepare the URL for the API request
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Create the request body with necessary parameters
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "getcustomerdetails");
      body.add("MOBILENO", mobileNumber);

      // Combine headers and body into the HttpEntity
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Make the API call using RestTemplate
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Successfully fetched customer details for mobile number: {}", mobileNumber);
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error occurred while fetching customer details for mobile number: {}: {}",
          mobileNumber, e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while processing your request.");
    }
  }

  /**
   * Registers a customer by sending a request to the Cyrus API.
   *
   * @param dto - the DMT Express Registration request DTO containing the customer's details
   * @return a ResponseEntity containing the result of the registration operation
   */
  @Override
  public ResponseEntity<?> customerRegistration(DmtExpressRegistrationRequestDto dto) {
    try {
      // Log the incoming registration request
      logger.info("Initiating customer registration for mobile: {}", dto.getMobileNumber());

      // Define the URL for the DMT Express API
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Create the request body with necessary parameters
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "customerregistration");
      body.add("FNAME", dto.getFirstName());
      body.add("LNAME", dto.getLastName());
      body.add("MOBILENO", dto.getMobileNumber());
      body.add("DOB", dto.getDateOfBirth());
      body.add("PINCODE", dto.getPinCode());
      body.add("ADDRESS", dto.getAddress());
      body.add("Pan", dto.getPanNumber());
      body.add("Aadhar", dto.getAadhaarNumber());

      // Combine headers and body into the HttpEntity
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Make the API call using RestTemplate
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      // Log the successful response
      logger.info("Customer registration response received for mobile: {}", dto.getMobileNumber());

      // Parse the response into the response DTO
      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      // Return a successful response with the data
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      // Log the exception
      logger.error("Error occurred during customer registration for mobile: {}", dto.getMobileNumber(), e);

      // Return an error response with an internal server error status
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred during customer registration.");
    }
  }

  /**
   * Adds KYC (Know Your Customer) details for a customer by sending a request to the Cyrus API.
   * This request includes the mobile number, PAN, and Aadhar details of the customer.
   *
   * @param dto - the DMT Express Add KYC request DTO containing the customer's KYC details
   * @return a ResponseEntity containing the result of the KYC addition operation
   */
  @Override
  public ResponseEntity<?> addKycDetails(DmtExpressAddKycRequestDto dto) {
    try {
      // Log the incoming KYC addition request
      logger.info("Initiating KYC addition for mobile: {}", dto.getMobileNumber());

      // Define the URL for the DMT Express API
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Create the request body with necessary parameters
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "add_kycdetails");
      body.add("MOBILENO", dto.getMobileNumber());
      body.add("Pan", dto.getPanNumber());
      body.add("Aadhar", dto.getAadhaarNumber());

      // Combine headers and body into the HttpEntity
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Make the API call using RestTemplate
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      // Log the successful response
      logger.info("KYC details successfully added for mobile: {}", dto.getMobileNumber());

      // Parse the response into the response DTO
      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      // Return a successful response with the data
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      // Log the error with the exception details
      logger.error("Error occurred while adding KYC details for mobile: {}", dto.getMobileNumber(), e);

      // Return an error response with an internal server error status
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while adding KYC details.");
    }
  }

  /**
   * Verifies KYC (Know Your Customer) details for a customer by sending a request to the Cyrus API.
   * This request includes the mobile number and OTP (One-Time Password) for verification.
   *
   * @param dto - the DMT Express Verify KYC request DTO containing the mobile number and OTP
   * @return a ResponseEntity containing the result of the KYC verification operation
   */
  @Override
  public ResponseEntity<?> verifyKycDetails(DmtExpressVerifyKycRequestDto dto) {
    try {
      // Log the incoming KYC verification request
      logger.info("Initiating KYC verification for mobile: {}", dto.getMobileNumber());

      // Define the URL for the DMT Express API
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Create the request body with necessary parameters
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "verify_kycdetails");
      body.add("MOBILENO", dto.getMobileNumber());
      body.add("otp", dto.getOtp());

      // Combine headers and body into the HttpEntity
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Make the API call using RestTemplate
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      // Log the successful response
      logger.info("KYC verification successful for mobile: {}", dto.getMobileNumber());

      // Parse the response into the response DTO
      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      // Return a successful response with the data
      if (resp.getData().isEmpty()) {
        return baseResponse.successResponse(resp.getStatus());
      }
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      // Log the error with the exception details
      logger.error("Error occurred while verifying KYC details for mobile: {}", dto.getMobileNumber(), e);

      // Return an error response with an internal server error status
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while verifying KYC details.");
    }
  }

  /**
   * Verifies KYC details for a beneficiary based on the provided mobile number, PAN, and Aadhaar.
   * This method initiates a KYC verification request to the DMT Express API and processes the response.
   * It returns the verification result or an error message in case of failure.
   *
   * @param dto The request DTO containing the mobile number, PAN, and Aadhaar number for verification.
   * @return A ResponseEntity containing the verification result or an error response.
   */
  @Override
  public ResponseEntity<?> getBeneficiaryDetails(DmtExpressAddKycRequestDto dto) {
    try {
      // Log the incoming KYC verification request
      logger.info("Initiating KYC verification for mobile: {}", dto.getMobileNumber());

      // Define the URL for the DMT Express API
      String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Create the request body with necessary parameters
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "verify_kycdetails");
      body.add("MOBILENO", dto.getMobileNumber());
      body.add("Pan", dto.getPanNumber());
      body.add("Aadhar", dto.getAadhaarNumber());

      // Combine headers and body into the HttpEntity
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Make the API call using RestTemplate
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      // Log the successful response
      logger.info("KYC verification successful for mobile: {}", dto.getMobileNumber());

      // Parse the response into the response DTO
      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      // Return a successful response with the data
      if (resp.getData().isEmpty()) {
        return baseResponse.successResponse(resp.getStatus());
      }
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      // Log any other exceptions
      logger.error("Unexpected error occurred while verifying KYC details for mobile: {}", dto.getMobileNumber(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while verifying KYC details.");
    }
  }

}
