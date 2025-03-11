package com.rivilege.app.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivilege.app.constant.CyrusApiConstantService;
import com.rivilege.app.dto.cyrus.request.DmtExpressAddBeneficiaryRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressAddKycRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressBeneficiaryAccountVerificationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressRegistrationRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressSendMoneyRequestDto;
import com.rivilege.app.dto.cyrus.request.DmtExpressVerifyKycRequestDto;
import com.rivilege.app.dto.cyrus.response.DmtExpressAddKycResponseDto;
import com.rivilege.app.dto.cyrus.response.DmtExpressBankListResponseDto;
import com.rivilege.app.model.DmtExpress;
import com.rivilege.app.repository.DmtExpressRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.DmtExpressService;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
  private DmtExpressRepository dmtExpressRepository;

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
   *         or an empty list if no data is found. Returns an error response
   *         with HTTP status 500 in case of any issues during the process.
   */
  @Override
  public ResponseEntity<?> getBankList() {
    logger.info("Initiating request to fetch bank list from Cyrus Recharge API...");

    try {
      // Prepare the URL
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

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
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while processing your request.");
    }
  }

  /**
   * Fetches customer details from the Cyrus Recharge API using the provided mobile number.
   *
   * @param mobileNumber The mobile number of the customer for which details are to be fetched.
   * @return ResponseEntity containing the customer details if found, or an error message if not.
   *         Returns an error response with HTTP status 500 in case of any issues during the process .
   */
  @Override
  public ResponseEntity<?> getCustomerDetails(String mobileNumber) {
    logger.info("Fetching customer details for mobile number: {}", mobileNumber);

    try {
      // Prepare the URL for the API request
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

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
  @Transactional
  public ResponseEntity<?> customerRegistration(DmtExpressRegistrationRequestDto dto) {
    try {
      // Log the incoming registration request
      logger.info("Initiating customer registration for mobile: {}", dto.getMobileNumber());

      Optional<DmtExpress> optionalDmtExpress = dmtExpressRepository.findByMemberId(dto.getMemberId());

      if (optionalDmtExpress.isPresent()) {
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "you Are Already Register With Dmt");
      }

      // Define the URL for the DMT Express API
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

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
      DmtExpressAddKycResponseDto resp = objectMapper.readValue(response, DmtExpressAddKycResponseDto.class);

      if (resp.getData() != null && !resp.getData().isEmpty() && "001".equals(resp.getData().getFirst().getStsCode())) {
        registerDmtCustomer(dto);
      }

      // Return a successful response with the data
      return (resp.getData() == null || resp.getData().isEmpty())
          ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      // Log the exception
      logger.error("Error occurred during customer registration for mobile: {}", dto.getMobileNumber(), e);

      // Return an error response with an internal server error status
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred during customer registration.");
    }
  }

  private void registerDmtCustomer(DmtExpressRegistrationRequestDto dto) {
    DmtExpress dmtExpress = new DmtExpress();
    dmtExpress.setMemberId(dto.getMemberId());
    dmtExpress.setFirstName(dto.getFirstName());
    dmtExpress.setLastName(dto.getLastName());
    dmtExpress.setMobileNumber(dto.getMobileNumber());
    dmtExpress.setDateOfBirth(dto.getDateOfBirth());
    dmtExpress.setPinCode(dto.getPinCode());
    dmtExpress.setAddress(dto.getAddress());
    dmtExpress.setPanNumber(dto.getPanNumber());
    dmtExpress.setAadhaarNumber(dto.getAadhaarNumber());
    dmtExpressRepository.saveAndFlush(dmtExpress);
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

      Optional<DmtExpress> optionalDmtExpress = dmtExpressRepository.findByMemberId(dto.getMemberId());

      if (optionalDmtExpress.isEmpty()) {
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Register A Member First");
      }

      // Define the URL for the DMT Express API
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

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
      DmtExpressAddKycResponseDto resp = objectMapper.readValue(response, DmtExpressAddKycResponseDto.class);

      // Return a successful response with the data
      return (resp.getData() == null || resp.getData().isEmpty())
          ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

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

      Optional<DmtExpress> optionalDmtExpress = dmtExpressRepository.findByMemberId(dto.getMemberId());

      if (optionalDmtExpress.isEmpty()) {
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Register A Member First");
      }

      DmtExpress dmtExpress = optionalDmtExpress.get();

      // Define the URL for the DMT Express API
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

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
      DmtExpressAddKycResponseDto resp = objectMapper.readValue(response, DmtExpressAddKycResponseDto.class);

      if (resp.getData() != null && !resp.getData().isEmpty() && "001".equals(resp.getData().getFirst().getStsCode())) {
        dmtExpress.setVerifyKyc(true);
        dmtExpressRepository.saveAndFlush(dmtExpress);
      }

      // Return a successful response with the data
      if (resp.getData() == null || resp.getData().isEmpty()) {
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
   * Retrieves beneficiary details for KYC verification using the DMT Express API.
   * This method sends a request with the mobile number, PAN, and Aadhaar details
   * and returns the verification result.
   *
   * @param dto The request object containing the mobile number, PAN, and Aadhaar.
   * @return A ResponseEntity with the verification result or an error response.
   */
  @Override
  public ResponseEntity<?> getBeneficiaryDetails(DmtExpressAddKycRequestDto dto) {
    try {
      // Log the start of the beneficiary details request
      logger.info("Fetching beneficiary details for mobile: {}", dto.getMobileNumber());

      // Define the API endpoint URL
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      // Set up HTTP headers
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      // Prepare the request body with required parameters
      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "getbeneficiarydetails");
      body.add("MOBILENO", dto.getMobileNumber());
      body.add("Pan", dto.getPanNumber());
      body.add("Aadhar", dto.getAadhaarNumber());

      // Create HttpEntity with headers and body
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

      // Make the API call using RestTemplate
      var response = restTemplate.exchange(
          url,
          HttpMethod.POST,
          requestEntity,
          String.class
      ).getBody();

      // Log the API response
      logger.info("Successfully retrieved beneficiary details for mobile: {}", dto.getMobileNumber());

      // Parse the response
      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      // Return a response based on the API result
      if (resp.getData().isEmpty()) {
        return baseResponse.successResponse(resp.getStatus());
      }
      return baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      // Log any unexpected errors
      logger.error("Error fetching beneficiary details for mobile: {}", dto.getMobileNumber(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while fetching beneficiary details.");
    }
  }

  /**
   * Verifies the beneficiary's bank account using DMT Express API.
   *
   * @param dto Request DTO containing mobile number, account number, IFSC code, and order ID.
   * @return ResponseEntity with verification details or an error message.
   */
  @Override
  public ResponseEntity<?> beneficiaryAccountVerification(DmtExpressBeneficiaryAccountVerificationRequestDto dto) {
    logger.info("Starting beneficiary account verification for mobile: {}", dto.getMobileNumber());

    try {

      Optional<DmtExpress> optionalDmtExpress = dmtExpressRepository.findByMemberId(dto.getMemberId());

      if (optionalDmtExpress.isEmpty()) {
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Register A Member First");
      }

      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "beneficiaryaccount_verification");
      body.add("CustomerMobileNo", dto.getMobileNumber());
      body.add("beneficiaryAccount", dto.getAccountNumber());
      body.add("beneficiaryIFSC", dto.getIfscCode());
      body.add("orderId", dto.getOrderId());

      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
      logger.info("Sending request to {} with payload: {}", url, body);

      var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
      logger.info("API response received: {}", response);

      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Beneficiary details retrieved successfully for mobile: {}", dto.getMobileNumber());

      return resp.getData().isEmpty() ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error verifying beneficiary account for mobile: {}", dto.getMobileNumber(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while verifying beneficiary details.");
    }
  }

  /**
   * Adds a new beneficiary using the DMT Express API.
   *
   * @param dto Request DTO containing beneficiary details such as mobile number, bank ID, and account details.
   * @return ResponseEntity with success or error response.
   */
  @Override
  public ResponseEntity<?> addBeneficiary(DmtExpressAddBeneficiaryRequestDto dto) {
    logger.info("Starting beneficiary addition for mobile: {}", dto.getMobileNumber());

    try {
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "addbeneficiary");
      body.add("MobileNo", dto.getMobileNumber());
      body.add("CustomerMobileNo", dto.getCustomerMobileNumber());
      body.add("BankId", dto.getBankId());
      body.add("AccountNo", dto.getAccountNumber());
      body.add("IFSC", dto.getIfscCode());
      body.add("Name", dto.getCustomerName());

      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
      logger.info("Sending request to {} with payload: {}", url, body);

      var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
      logger.info("API response received: {}", response);

      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Beneficiary added successfully for mobile: {}", dto.getMobileNumber());

      return resp.getData().isEmpty() ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error adding beneficiary for mobile: {}", dto.getMobileNumber(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while adding the beneficiary.");
    }
  }

  /**
   * Removes a beneficiary account using the DMT Express API.
   *
   * @param beneficiaryId The ID of the beneficiary to be removed.
   * @return ResponseEntity with success or error response.
   */
  @Override
  public ResponseEntity<?> removeBeneficiaryAccount(String beneficiaryId) {
    logger.info("Starting removal of beneficiary with ID: {}", beneficiaryId);

    try {
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "removebeneficiaryaccount");
      body.add("BENEFICIARYID", beneficiaryId);

      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
      logger.info("Sending request to {} with payload: {}", url, body);

      var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
      logger.info("API response received: {}", response);

      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Successfully removed beneficiary with ID: {}", beneficiaryId);

      return resp.getData().isEmpty() ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error removing beneficiary with ID: {}", beneficiaryId, e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while removing the beneficiary.");
    }
  }

  /**
   * Sends money to a beneficiary using the DMT Express API.
   *
   * @param dto The request DTO containing transaction details such as customer mobile number,
   *            beneficiary account details, order ID, amount, and comments.
   * @return ResponseEntity containing the success or error response.
   */
  @Override
  public ResponseEntity<?> sendMoney(DmtExpressSendMoneyRequestDto dto) {
    logger.info("Initiating money transfer. Order ID: {}, Customer Mobile: {}, Beneficiary Account: {}",
        dto.getOrderId(), dto.getCustomerMobileNumber(), dto.getAccountNumber());

    try {
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "sendmoney");
      body.add("CustomerMobile", dto.getCustomerMobileNumber());
      body.add("beneficiaryAccount", dto.getAccountNumber());
      body.add("beneficiaryIFSC", dto.getIfscCode());
      body.add("orderId", dto.getOrderId());
      body.add("amount", String.valueOf(dto.getAmount()));
      body.add("comments", dto.getComments());

      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
      logger.info("Sending request to {} with payload: {}", url, body);

      var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
      logger.info("Received API response: {}", response);

      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Money transfer successful. Order ID: {}, Status: {}", dto.getOrderId(), resp.getStatus());

      return resp.getData().isEmpty() ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error during money transfer. Order ID: {}, Customer Mobile: {}",
          dto.getOrderId(), dto.getCustomerMobileNumber(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while processing the transaction.");
    }
  }

  /**
   * Checks the status of a transaction using the provided order ID.
   *
   * @param orderId The unique identifier for the transaction whose status needs to be checked.
   * @return ResponseEntity containing the transaction status or an error response.
   */
  @Override
  public ResponseEntity<?> statusCheck(String orderId) {
    logger.info("Initiating status check for Order ID: {}", orderId);

    try {
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.GET_DMT_EXPRESS;

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("MerchantID", cyrusApiMemberId);
      body.add("MerchantKey", cyrusDmtExpressApiKey);
      body.add("MethodName", "checkstatus");
      body.add("orderId", orderId);

      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
      logger.info("Sending status check request to {} with payload: {}", url, body);

      var response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
      logger.info("API response received for Order ID {}: {}", orderId, response);

      DmtExpressBankListResponseDto resp = objectMapper.readValue(response, DmtExpressBankListResponseDto.class);

      logger.info("Transaction status check successful for Order ID: {}, Status: {}", orderId, resp.getStatus());

      return resp.getData().isEmpty() ? baseResponse.successResponse(resp.getStatus())
          : baseResponse.successResponse(resp.getData());

    } catch (Exception e) {
      logger.error("Error occurred while checking transaction status for Order ID: {}", orderId, e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while checking the transaction status.");
    }
  }


}
