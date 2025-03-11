package com.rivilege.app.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivilege.app.constant.CyrusApiConstantService;
import com.rivilege.app.dto.cyrus.request.MobileRechargePlanFetchRequestDto;
import com.rivilege.app.dto.cyrus.request.MobileRechargeRequestDto;
import com.rivilege.app.dto.cyrus.response.GetBalanceCyrusResponseDto;
import com.rivilege.app.dto.cyrus.response.MobileRechargeCyrusResponseDto;
import com.rivilege.app.dto.cyrus.response.MobileRechargeResponseDto;
import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.model.MobileRechargeHistory;
import com.rivilege.app.model.ReferralDetails;
import com.rivilege.app.model.Users;
import com.rivilege.app.repository.MobileRechargeHistoryRepository;
import com.rivilege.app.repository.ReferralDetailsRepository;
import com.rivilege.app.repository.UsersRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.MobileRechargeService;
import com.rivilege.app.utilities.StringUtils;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.client.RestTemplate;
import ulid4j.Ulid;

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
  private UsersRepository usersRepository;

  @Autowired
  private MobileRechargeHistoryRepository mobileRechargeHistoryRepository;

  @Autowired
  private ReferralDetailsRepository referralDetailsRepository;

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

  /**
   * Handles the mobile recharge request by interacting with the Cyrus Recharge API.
   * This method builds the request URL dynamically using the input data provided in the
   * {@link MobileRechargeRequestDto}, sends a GET request to the Cyrus API, and processes
   * the response to return a {@link MobileRechargeResponseDto}.
   *
   * @param dto the mobile recharge request data transfer object containing details such as
   *            mobile number, operator, circle, and amount.
   * @return a {@link ResponseEntity} containing the recharge response if successful, or an
   *           error response in case of failure.
   */
  @Override
  @Transactional
  public ResponseEntity<?> rechargeRequest(MobileRechargeRequestDto dto) {
    try {
      Optional<Users> optionalUsers = usersRepository.findByMemberId(dto.getMemberId());

      if (optionalUsers.isEmpty()) {
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User Not Exist");
      }

      // Generate a unique transaction ID
      final String usertx = new Ulid().next();

      // Build the request URL
      final String url = cyrusRechargeApiEndpoint + CyrusApiConstantService.MOBILE_RECHARGE_REQUEST
          .replace("{memberId}", cyrusApiId)
          .replace("{pin}", cyrusPlanFetchKey)
          .replace("{number}", dto.getMobileNumber())
          .replace("{operator}", dto.getOperator())
          .replace("{circle}", dto.getCircle())
          .replace("{amount}", dto.getAmount())
          .replace("{usertx}", usertx)
          .replace("{mode}", "1");

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      // Combine headers and body
      HttpEntity<?> requestEntity = new HttpEntity<>(headers);

      // Send the request and get the response
      // String response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();

      String response = "{\"ApiTransID\":\"CYA6426CE1AE\",\"Status\":\"Success\",\"ErrorMessage\":\" \","
          + "\"OperatorRef\":\"BR000BPUAGA1\",\"TransactionDate\":\"2/1/2025 10:54:06 AM\"}";

      // Parse the response
      MobileRechargeResponseDto resp = objectMapper.readValue(response, MobileRechargeResponseDto.class);

      updateRechargeCommissionWallet(dto.getMemberId(), dto.getMobileNumber(), Double.parseDouble(dto.getAmount()),
          dto.getOperator(), resp.getApiTransId(), resp.getOperatorRef(), resp.getTransactionDate(), usertx,
          resp.getStatus());

      logger.info("Recharge request successful for mobile: {}. Transaction ID: {}", dto.getMobileNumber(), usertx);
      return baseResponse.successResponse(resp);
    } catch (Exception e) {
      logger.error("Recharge request failed for mobile: {}. Error: {}", dto.getMobileNumber(), e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "Failed to process recharge request. Please try again later.");
    }
  }


  private void updateRechargeCommissionWallet(String memberId, String mobileNumber, double amount,
                                              String operatorCode, String apiTxnId, String operatorRef,
                                              String transactionDate, String usertx, String status) {
    List<MobileRechargeHistory> mobileRechargeHistoryList = new ArrayList<>();

    // Fetch referral details
    ReferralDetails referralDetails = referralDetailsRepository.findByReferredMemberId(memberId)
        .orElseThrow(() -> new RuntimeException("Referral details not found for memberId: " + memberId));
    UserDesignationType designation = referralDetails.getReferredDesignation();

    double commissionPercentage = getCommissionByDesignationAndOperator(referralDetails.getReferredDesignation(),
        operatorCode);
    double commissionBalance = amount * (commissionPercentage / 100);
    mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getReferredMemberId(),
        mobileNumber, amount, commissionBalance, status, apiTxnId, operatorRef, transactionDate, usertx));

    // Process commission based on designation
    switch (designation) {
      case REGIONAL_MANAGER:
        processRegionalManager(mobileRechargeHistoryList, referralDetails, mobileNumber, amount, operatorCode, status,
            apiTxnId, operatorRef, transactionDate, usertx);
        break;
      case SUPER_DISTRIBUTOR:
        processSuperDistributor(mobileRechargeHistoryList, referralDetails, mobileNumber, amount, operatorCode, status,
            apiTxnId, operatorRef, transactionDate, usertx);
        break;
      case DISTRIBUTOR:
        processDistributor(mobileRechargeHistoryList, referralDetails, mobileNumber, amount, operatorCode, status,
            apiTxnId, operatorRef, transactionDate, usertx);
        break;
      case RETAILER:
        processRetailer(mobileRechargeHistoryList, referralDetails, mobileNumber, amount, operatorCode, status,
            apiTxnId, operatorRef, transactionDate, usertx);
        break;
      default:
        throw new RuntimeException("Invalid designation type");
    }
    // Save commission wallet entries
    if (!mobileRechargeHistoryList.isEmpty()) {
      mobileRechargeHistoryRepository.saveAll(mobileRechargeHistoryList);
    }
  }

  private void processRegionalManager(List<MobileRechargeHistory> mobileRechargeHistoryList,
                                      ReferralDetails referralDetails, String mobileNumber, double amount,
                                      String operatorCode, String status, String apiTransId, String operatorRef,
                                      String transactionDate, String usertx) {
    if (referralDetails.getDesignation().equals(UserDesignationType.GENERAL_MANAGER)) {
      double commissionPercentage = getCommissionByDesignationAndOperator(referralDetails.getDesignation(), operatorCode);
      double commissionBalance = amount * (commissionPercentage / 100);
      mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
          mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
    } else {
      throw new RuntimeException("Invalid referral designation for REGIONAL_MANAGER");
    }
  }

  private void processSuperDistributor(List<MobileRechargeHistory> mobileRechargeHistoryList,
                                       ReferralDetails referralDetails, String mobileNumber, double amount,
                                       String operatorCode, String status, String apiTransId, String operatorRef,
                                       String transactionDate, String usertx) {
    if (referralDetails.getDesignation().equals(UserDesignationType.REGIONAL_MANAGER)) {
      double commissionPercentage = getCommissionByDesignationAndOperator(referralDetails.getDesignation(), operatorCode);
      double commissionBalance = amount * (commissionPercentage / 100);
      mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
          mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.GENERAL_MANAGER)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getRegionalManagerId())) {
        double commissionPercentage = getCommissionByDesignationAndOperator(UserDesignationType.REGIONAL_MANAGER,
            operatorCode);
        double commissionBalance = amount * (commissionPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
            mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));

        ReferralDetails regionalManagerDetails = referralDetailsRepository.findByReferredMemberId(
                referralDetails.getRegionalManagerId())
            .orElseThrow(() -> new RuntimeException("Regional Manager details not found"));
        double regionalCommissionPercentage = getCommissionByDesignationAndOperator(
            regionalManagerDetails.getDesignation(), operatorCode);
        double regionalCommission = amount * (regionalCommissionPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(regionalManagerDetails.getMemberId(),
            mobileNumber, amount, regionalCommission, status, apiTransId, operatorRef, transactionDate, usertx));
      } else {
        throw new RuntimeException("Missing Regional Manager ID for Super Distributor");
      }
    }
  }

  private void processDistributor(List<MobileRechargeHistory> mobileRechargeHistoryList,
                                  ReferralDetails referralDetails, String mobileNumber, double amount,
                                  String operatorCode, String status, String apiTransId, String operatorRef,
                                  String transactionDate, String usertx) {
    if (referralDetails.getDesignation().equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
      double commissionPercentage = getCommissionByDesignationAndOperator(referralDetails.getDesignation(), operatorCode);
      double commissionBalance = amount * (commissionPercentage / 100);
      mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
          mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.REGIONAL_MANAGER)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getSuperDistributorId())) {
        double commissionPercentage = getCommissionByDesignationAndOperator(UserDesignationType.DISTRIBUTOR, operatorCode);
        double commissionBalance = amount * (commissionPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
            mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));

        ReferralDetails superDistributorDetails = referralDetailsRepository.findByReferredMemberId(
                referralDetails.getSuperDistributorId())
            .orElseThrow(() -> new RuntimeException("Super Distributor details not found"));

        double sdPercentage = getCommissionByDesignationAndOperator(superDistributorDetails.getDesignation(),
            operatorCode);
        double sdCommissionBalance = amount * (sdPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(superDistributorDetails.getMemberId(),
            mobileNumber, amount, sdCommissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));

        ReferralDetails regionalManagerDetails = referralDetailsRepository.findByReferredMemberId(
                superDistributorDetails.getMemberId())
            .orElseThrow(() -> new RuntimeException("Regional Manager details not found"));

        double regionalManagerCommissionPercentage = getCommissionByDesignationAndOperator(
            regionalManagerDetails.getDesignation(), operatorCode);
        double regionalManagerCommissionBalance = amount * (regionalManagerCommissionPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(regionalManagerDetails.getMemberId(),
            mobileNumber, amount, regionalManagerCommissionBalance, status, apiTransId, operatorRef, transactionDate,
            usertx));
      } else {
        throw new RuntimeException("Missing Super Distributor ID for Distributor");
      }
    }
  }

  private void processRetailer(List<MobileRechargeHistory> mobileRechargeHistoryList, ReferralDetails referralDetails,
                               String mobileNumber, double amount,
                               String operatorCode, String status, String apiTransId, String operatorRef,
                               String transactionDate, String usertx) {
    if (referralDetails.getDesignation().equals(UserDesignationType.DISTRIBUTOR)) {
      double commissionPercentage = getCommissionByDesignationAndOperator(referralDetails.getDesignation(),
          operatorCode);
      double commissionBalance = amount * (commissionPercentage / 100);
      mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
          mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getDistributorId())) {
        double commissionPercentage = getCommissionByDesignationAndOperator(UserDesignationType.DISTRIBUTOR,
            operatorCode);
        double commissionBalance = amount * (commissionPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getDistributorId(),
            mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
      } else {
        throw new RuntimeException("Missing Distributor ID for Retailer");
      }

      double commissionPercentage = getCommissionByDesignationAndOperator(referralDetails.getDesignation(), operatorCode);
      double commissionBalance = amount * (commissionPercentage / 100);
      mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getMemberId(),
          mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.REGIONAL_MANAGER)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getDistributorId())) {
        double commissionPercentage = getCommissionByDesignationAndOperator(UserDesignationType.DISTRIBUTOR, operatorCode);
        double commissionBalance = amount * (commissionPercentage / 100);
        mobileRechargeHistoryList.add(createRechargeCommissionWallet(referralDetails.getDistributorId(),
            mobileNumber, amount, commissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));

        ReferralDetails distributorDetails = referralDetailsRepository.findByReferredMemberId(
                referralDetails.getDistributorId())
            .orElseThrow(() -> new RuntimeException("Distributor details not found"));

        if (distributorDetails.getDesignation().equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
          final double dCommissionPercentage = getCommissionByDesignationAndOperator(distributorDetails.getDesignation(),
              operatorCode);
          final double dCommissionBalance = amount * (dCommissionPercentage / 100);
          mobileRechargeHistoryList.add(createRechargeCommissionWallet(distributorDetails.getMemberId(),
              mobileNumber, amount, dCommissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));

          ReferralDetails regionalManagerDetails = referralDetailsRepository.findByReferredMemberId(
                  distributorDetails.getMemberId())
              .orElseThrow(() -> new RuntimeException("Regional Manager details not found"));

          double rmCommissionPercentage = getCommissionByDesignationAndOperator(regionalManagerDetails.getDesignation(),
              operatorCode);
          double rmCommissionBalance = amount * (rmCommissionPercentage / 100);
          mobileRechargeHistoryList.add(createRechargeCommissionWallet(regionalManagerDetails.getMemberId(),
              mobileNumber, amount, rmCommissionBalance, status, apiTransId, operatorRef, transactionDate, usertx));
        }
      } else {
        throw new RuntimeException("Missing Distributor ID for Retailer under Regional Manager");
      }
    }
  }

  private MobileRechargeHistory createRechargeCommissionWallet(String memberId, String rechargedMobileNumber,
                                                               double amount, double commissionBalance,
                                                               String status, String apiTransId, String operatorRef,
                                                               String transactionDate, String usertx) {
    updateMemberWalletBalance(memberId, commissionBalance);
    MobileRechargeHistory mobileRechargeHistory = new MobileRechargeHistory();
    mobileRechargeHistory.setMemberId(memberId);
    mobileRechargeHistory.setMobileNumber(rechargedMobileNumber);
    mobileRechargeHistory.setAmount(String.valueOf(amount));
    mobileRechargeHistory.setCommission(commissionBalance);
    mobileRechargeHistory.setStatus(status);
    mobileRechargeHistory.setApiTransId(apiTransId);
    mobileRechargeHistory.setOperatorRef(operatorRef);
    mobileRechargeHistory.setTransactionDate(transactionDate);
    mobileRechargeHistory.setUsertx(usertx);

    return mobileRechargeHistory;
  }

  /**
   * Updates the commission wallet balance for a member.
   *
   * @param memberId          The unique ID of the member.
   * @param commissionBalance The amount to add to the commission wallet.
   */
  private void updateMemberWalletBalance(String memberId, double commissionBalance) {
    usersRepository.findByMemberId(memberId)
        .map(user -> {
          logger.info("Updating commission wallet for member ID: {}", memberId);
          user.setRechargeWallet(user.getRechargeWallet() + commissionBalance);
          user.setMainWallet(user.getMainWallet() + commissionBalance);
          return usersRepository.save(user);
        })
        .orElseThrow(() -> {
          logger.warn("User details not found for member ID: {}", memberId);
          return new RuntimeException("User details not found.");
        });

    logger.info("Commission wallet updated successfully for member ID: {}", memberId);
  }

  private double getCommissionByDesignationAndOperator(UserDesignationType designation, String operatorCode) {
    Map<String, double[]> commissionMap = new HashMap<>();

    // Commission structure based on operator
    commissionMap.put("AIRTEL", new double[]{3.00, 0.07, 0.05, 0.03, 0.02});
    commissionMap.put("VI", new double[]{3.50, 0.10, 0.07, 0.05, 0.03});
    commissionMap.put("BSNL", new double[]{4.50, 0.20, 0.15, 0.10, 0.05});
    commissionMap.put("MTNL", new double[]{2.50, 0.20, 0.15, 0.10, 0.05});
    commissionMap.put("JIO", new double[]{1.00, 0.07, 0.05, 0.03, 0.02});

    double[] commissionRates = commissionMap.get(operatorCode.toUpperCase());

    if (commissionRates == null) {
      throw new IllegalArgumentException("Unknown operator code: " + operatorCode);
    }

    return switch (designation) {
      case RETAILER -> commissionRates[0];      // RET
      case DISTRIBUTOR -> commissionRates[1];   // DIS
      case SUPER_DISTRIBUTOR -> commissionRates[2]; // S.DIS
      case REGIONAL_MANAGER -> commissionRates[3]; // RM
      case GENERAL_MANAGER -> commissionRates[4]; // GM
      default -> throw new IllegalArgumentException("Unknown designation: " + designation);
    };
  }

}
