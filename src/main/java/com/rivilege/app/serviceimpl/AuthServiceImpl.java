package com.rivilege.app.serviceimpl;

import com.rivilege.app.converter.RegistrationUserRequestDtoToUsers;
import com.rivilege.app.converter.UsersToLogInResponseDto;
import com.rivilege.app.dto.request.LogInRequestDto;
import com.rivilege.app.dto.request.RegistrationUserRequestDto;
import com.rivilege.app.dto.response.LogInResponseDto;
import com.rivilege.app.dto.response.ReferralUserDetailsResponseDto;
import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.model.CommissionWallet;
import com.rivilege.app.model.IdPackages;
import com.rivilege.app.model.ReferralDetails;
import com.rivilege.app.model.Users;
import com.rivilege.app.repository.CommissionWalletRepository;
import com.rivilege.app.repository.IdPackagesRepository;
import com.rivilege.app.repository.ReferralDetailsRepository;
import com.rivilege.app.repository.UsersRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.AuthService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * this is a auth service implementation class .
 *
 * @author kousik manik
 */
@Service
public class AuthServiceImpl implements AuthService {


  @Autowired
  private BaseResponse baseResponse;

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private IdPackagesRepository idPackagesRepository;

  @Autowired
  private CommissionWalletRepository commissionWalletRepository;

  @Autowired
  private ReferralDetailsRepository referralDetailsRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private RegistrationUserRequestDtoToUsers registrationUserRequestDtoToUsers;

  @Autowired
  private UsersToLogInResponseDto usersToLogInResponseDto;

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


  /**
   * this is a register new member method .
   *
   * @param dto @{@link RegistrationUserRequestDto}
   * @return @{@link ResponseEntity}
   */
  @Override
  @Transactional
  public ResponseEntity<?> registerNewMember(RegistrationUserRequestDto dto) {
    logger.info("Starting registration process for Mobile: {}, Email: {}", dto.getMobileNumber(), dto.getEmailId());

    try {
      // Check if referee exists
      if (!usersRepository.existsByMemberId(dto.getRefereeMemberId())) {
        String errorMessage = String.format("Referee with Member ID: %s does not exist. Mobile: %s",
            dto.getRefereeMemberId(), dto.getMobileNumber());
        logger.warn("Registration failed: {}", errorMessage);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, errorMessage);
      }

      // Check if user already exists by mobile or email
      if (usersRepository.existsByMobileNumberOrEmailId(dto.getMobileNumber(), dto.getEmailId())) {
        String errorMessage = String.format("User already exists with Mobile: %s or Email: %s",
            dto.getMobileNumber(), dto.getEmailId());
        logger.warn("Registration failed: {}", errorMessage);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, errorMessage);
      }

      // Check if user already exists by PAN
      if (usersRepository.existsByPanNumber(dto.getPanNumber())) {
        String errorMessage = String.format("User already exists with PAN: %s", dto.getPanNumber());
        logger.warn("Registration failed: {}", errorMessage);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, errorMessage);
      }

      // Check if user already exists by Aadhaar
      if (usersRepository.existsByAadhaarNumber(dto.getAadhaarNumber())) {
        String errorMessage = String.format("User already exists with Aadhaar: %s", dto.getAadhaarNumber());
        logger.warn("Registration failed: {}", errorMessage);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, errorMessage);
      }

      // Process referral and handle errors
      ResponseEntity<?> referralResponse = processReferral(dto.getReferredUserDesignation(), dto.getUserDesignation(), dto);
      if (!referralResponse.getStatusCode().is2xxSuccessful()) {
        return referralResponse; // Return the error response from processReferral
      }
      // Convert DTO to entity and save the user
      Users user = registrationUserRequestDtoToUsers.convert(dto);
      user = usersRepository.saveAndFlush(user);

      // Update referral details
      updateReferrerDetails(
          dto.getRefereeMemberId(), dto.getRefereeMobileNumber(), dto.getReferredUserDesignation(),
          user.getMemberId(), user.getMobileNumber(), user.getUserDesignation(), user.getFullName(),
          dto.getRmMemberId(), dto.getSdMemberId(), dto.getDistributorMemberId()
      );

      // Success response
      Map<String, String> response = new HashMap<>();
      response.put("memberId", user.getMemberId());
      logger.info("User registered successfully with Member ID: {}", user.getMemberId());

      return baseResponse.successResponse("Member registration successful.", response);

    } catch (Exception e) {
      String errorMessage = String.format("Unexpected error during registration. Mobile: %s, Email: %s",
          dto.getMobileNumber(), dto.getEmailId());
      logger.error("{} Error: {}", errorMessage, e.getMessage(), e);
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An internal error occurred. Please try again later.");
    }
  }

  private ResponseEntity<?> processReferral(UserDesignationType referredUserDesignationType,
                                            UserDesignationType userDesignation,
                                            RegistrationUserRequestDto dto) {

    if (referredUserDesignationType == null || userDesignation == null) {
      logger.error("Invalid designation types provided: referredUserDesignationType={}, userDesignation={}",
          referredUserDesignationType, userDesignation);
      return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Invalid Designation Type");
    }

    logger.info("Processing referral: referredUserDesignationType={}, userDesignation={}",
        referredUserDesignationType, userDesignation);

    switch (referredUserDesignationType) {
      case GENERAL_MANAGER:
        if (userDesignation.equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
          if (!StringUtils.isNotNullAndNotEmpty(dto.getRmMemberId())) {
            logger.warn("Regional Manager ID missing for SUPER_DISTRIBUTOR referral");
            return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Add A Regional Manager Id");
          }
        } else if (userDesignation.equals(UserDesignationType.DISTRIBUTOR)) {
          if (!StringUtils.isNotNullAndNotEmpty(dto.getSdMemberId())) {
            logger.warn("Super Distributor ID missing for DISTRIBUTOR referral");
            return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Add A Super Distributor Id");
          }
        } else if (userDesignation.equals(UserDesignationType.RETAILER)) {
          if (!StringUtils.isNotNullAndNotEmpty(dto.getDistributorMemberId())) {
            logger.warn("Distributor ID missing for RETAILER referral");
            return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Add A Distributor Id");
          }
        }
        break;

      case REGIONAL_MANAGER:
        if (userDesignation.equals(UserDesignationType.GENERAL_MANAGER)) {
          logger.error("Invalid referral: Regional Manager cannot register General Manager");
          return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Regional Manager Cannot Register General Manager");
        }
        if (userDesignation.equals(UserDesignationType.DISTRIBUTOR)) {
          if (!StringUtils.isNotNullAndNotEmpty(dto.getSdMemberId())) {
            logger.warn("Super Distributor ID missing for DISTRIBUTOR referral");
            return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Add A Super Distributor Id");
          }
        } else if (userDesignation.equals(UserDesignationType.RETAILER)) {
          if (!StringUtils.isNotNullAndNotEmpty(dto.getDistributorMemberId())) {
            logger.warn("Distributor ID missing for RETAILER referral");
            return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Add A Distributor Id");
          }
        }
        break;

      case SUPER_DISTRIBUTOR:
        if (userDesignation.equals(UserDesignationType.GENERAL_MANAGER)
            || userDesignation.equals(UserDesignationType.REGIONAL_MANAGER)) {
          logger.error("Invalid referral: Super Distributor cannot register General Manager or Regional Manager");
          return baseResponse.errorResponse(HttpStatus.BAD_REQUEST,
              "Super Distributor Cannot Register General Manager And Regional Manager");
        }
        if (userDesignation.equals(UserDesignationType.RETAILER)) {
          if (!StringUtils.isNotNullAndNotEmpty(dto.getDistributorMemberId())) {
            logger.warn("Distributor ID missing for RETAILER referral");
            return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Please Add A Distributor Id");
          }
        }
        break;

      case DISTRIBUTOR:
        if (userDesignation.equals(UserDesignationType.GENERAL_MANAGER)
            || userDesignation.equals(UserDesignationType.REGIONAL_MANAGER)
            || userDesignation.equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
          logger.error("Invalid referral: Distributor cannot register higher-level designations");
          return baseResponse.errorResponse(HttpStatus.BAD_REQUEST,
              "Distributor Cannot Register General Manager, Regional Manager, or Super Distributor");
        }
        break;

      case RETAILER:
        logger.error("Invalid referral: Retailer cannot register any other ID");
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Retailer Cannot Register Any Other Id");

      default:
        logger.error("Unhandled referredUserDesignationType: {}", referredUserDesignationType);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Invalid Operation");
    }

    logger.info("Referral operation completed successfully for referredUserDesignationType={}, userDesignation={}",
        referredUserDesignationType, userDesignation);
    return baseResponse.successResponse("Operation Successful");
  }

  private void updateReferrerDetails(
      String memberId, String mobileNumber, UserDesignationType userDesignation,
      String referredMemberId, String referredMobileNumber, UserDesignationType referredUserDesignationType,
      String referredUserFullName,
      String rmMemberId, String sdMemberId, String distributorMemberId
  ) {
    logger.info("Updating referral details for Member ID: {}, Designation: {}", memberId, userDesignation);

    ReferralDetails referralDetails = new ReferralDetails();
    referralDetails.setMemberId(memberId);
    referralDetails.setMobileNumber(mobileNumber);
    referralDetails.setReferredMemberId(referredMemberId);
    referralDetails.setReferredMobileNumber(referredMobileNumber);
    referralDetails.setReferredMemberName(referredUserFullName);
    referralDetails.setReferredDesignation(referredUserDesignationType);
    referralDetails.setDesignation(userDesignation);

    try {
      // Hierarchical relationship logic
      switch (userDesignation) {
        case GENERAL_MANAGER:
          setGeneralManagerHierarchy(referralDetails, referredUserDesignationType, referredMemberId, rmMemberId,
              sdMemberId, distributorMemberId);
          break;
        case REGIONAL_MANAGER:
          setRegionalManagerHierarchy(referralDetails, referredUserDesignationType, referredMemberId, sdMemberId,
              distributorMemberId);
          break;
        case SUPER_DISTRIBUTOR:
          setSuperDistributorHierarchy(referralDetails, referredUserDesignationType, referredMemberId,
              distributorMemberId);
          break;
        case DISTRIBUTOR:
          if (referredUserDesignationType == UserDesignationType.RETAILER) {
            referralDetails.setRetailerId(referredMemberId);
          }
          break;
        default:
          logger.warn("No hierarchical relationships to set for Designation: {}", userDesignation);
          break;
      }

      referralDetailsRepository.saveAndFlush(referralDetails);
      logger.info("Referral details updated successfully for Member ID: {}", memberId);

    } catch (Exception e) {
      logger.error("Error while updating referral details for Member ID: {}. Error: {}", memberId, e.getMessage(), e);
      throw new RuntimeException("Failed to update referral details.");
    }
  }

  private void setGeneralManagerHierarchy(ReferralDetails referralDetails, UserDesignationType referredDesignation,
                                          String referredMemberId, String rmMemberId, String sdMemberId,
                                          String distributorMemberId) {
    switch (referredDesignation) {
      case REGIONAL_MANAGER -> referralDetails.setRegionalManagerId(referredMemberId);
      case SUPER_DISTRIBUTOR -> {
        referralDetails.setRegionalManagerId(rmMemberId);
        referralDetails.setSuperDistributorId(referredMemberId);
      }
      case DISTRIBUTOR -> {
        referralDetails.setRegionalManagerId(getParentId(sdMemberId, UserDesignationType.SUPER_DISTRIBUTOR));
        referralDetails.setSuperDistributorId(sdMemberId);
        referralDetails.setDistributorId(referredMemberId);
      }
      case RETAILER -> {
        referralDetails.setRetailerId(referredMemberId);
        referralDetails.setDistributorId(distributorMemberId);
        referralDetails.setSuperDistributorId(getParentId(distributorMemberId, UserDesignationType.DISTRIBUTOR));
        referralDetails.setRegionalManagerId(getParentId(referralDetails.getSuperDistributorId(),
            UserDesignationType.SUPER_DISTRIBUTOR));
      }
      default -> {
        throw new RuntimeException("Invalid Designation");
      }
    }
  }

  private void setRegionalManagerHierarchy(ReferralDetails referralDetails, UserDesignationType referredDesignation,
                                           String referredMemberId, String sdMemberId, String distributorMemberId) {
    switch (referredDesignation) {
      case SUPER_DISTRIBUTOR -> referralDetails.setSuperDistributorId(referredMemberId);
      case DISTRIBUTOR -> {
        referralDetails.setSuperDistributorId(sdMemberId);
        referralDetails.setDistributorId(referredMemberId);
      }
      case RETAILER -> {
        referralDetails.setRetailerId(referredMemberId);
        referralDetails.setDistributorId(distributorMemberId);
        referralDetails.setSuperDistributorId(getParentId(distributorMemberId, UserDesignationType.DISTRIBUTOR));
      }
      default -> {
        throw new RuntimeException("Invalid Designation");
      }
    }
  }

  private void setSuperDistributorHierarchy(ReferralDetails referralDetails, UserDesignationType referredDesignation,
                                            String referredMemberId, String distributorMemberId) {
    if (referredDesignation == UserDesignationType.DISTRIBUTOR) {
      referralDetails.setDistributorId(referredMemberId);
    } else if (referredDesignation == UserDesignationType.RETAILER) {
      referralDetails.setRetailerId(referredMemberId);
      referralDetails.setDistributorId(distributorMemberId);
    }
  }

  private String getParentId(String memberId, UserDesignationType designation) {
    try {
      return switch (designation) {
        case SUPER_DISTRIBUTOR -> referralDetailsRepository.findRegionalManagerIdByMemberIdAndDesignation(memberId,
            designation);
        case DISTRIBUTOR -> referralDetailsRepository.findSuperDistributorIdByMemberIdAndDesignation(memberId,
            designation);
        case RETAILER -> referralDetailsRepository.findDistributorIdByMemberIdAndDesignation(memberId, designation);
        default -> throw new IllegalArgumentException("Invalid designation provided: " + designation);
      };
    } catch (Exception e) {
      logger.error("Failed to fetch parent ID for Member ID: {} and Designation: {}. Error: {}", memberId,
          designation, e.getMessage(), e);
      throw new RuntimeException("Failed to fetch parent ID.");
    }
  }

  /**
   * Retrieves the referral user details for the given memberId.
   *
   * @param memberId the unique member ID of the user.
   * @return a {@link ResponseEntity} containing the user details if found, otherwise an error message.
   */
  @Override
  public ResponseEntity<?> getReferralUserDetails(String memberId) {
    try {
      logger.info("Fetching referral details for memberId: {}", memberId);

      // Find user by memberId
      Optional<Users> optionalUsers = usersRepository.findByMemberId(memberId.trim());

      if (optionalUsers.isEmpty()) {
        logger.warn("User with memberId: {} not found.", memberId);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Member ID not found.");
      }

      // Map user details to DTO
      Users users = optionalUsers.get();
      ReferralUserDetailsResponseDto dto = new ReferralUserDetailsResponseDto(
          users.getFullName(),
          users.getMemberId(),
          users.getMobileNumber(),
          users.getUserDesignation()
      );

      logger.info("Successfully fetched user details for memberId: {}", memberId);

      return baseResponse.successResponse("User details fetched successfully.", dto);

    } catch (Exception e) {
      logger.error("Error occurred while fetching user details for memberId: {}", memberId, e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while fetching user details. Please try again later.");
    }
  }

  /**
   * Handles the user login process by validating the member ID and password.
   *
   * @param dto the login request data transfer object containing the member ID and password.
   * @return a {@link ResponseEntity} with the login success or failure message.
   */
  @Override
  public ResponseEntity<?> loginUser(LogInRequestDto dto) {

    try {
      logger.info("Login attempt for memberId: {}", dto.getMemberId());

      // Find user by memberId
      Optional<Users> optionalUsers = usersRepository.findByMemberId(dto.getMemberId());

      if (optionalUsers.isEmpty()) {
        logger.warn("Login failed: MemberId {} not found", dto.getMemberId());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Member ID not found.");
      }

      Users users = optionalUsers.get();

      // Check if password matches
      if (!passwordEncoder.matches(dto.getPassword(), users.getPassword())) {
        logger.warn("Login failed: Incorrect password for memberId {}", dto.getMemberId());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Incorrect member ID or password.");
      }

      // Convert user to login response DTO
      LogInResponseDto logInResponseDto = usersToLogInResponseDto.convert(users);

      logger.info("Login successful for memberId: {}", dto.getMemberId());

      return baseResponse.successResponse("Login successful.", logInResponseDto);

    } catch (Exception e) {
      logger.error("Error occurred while logging in for memberId: {}", dto.getMemberId(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while logging in. Please try again later.");
    }
  }

}
