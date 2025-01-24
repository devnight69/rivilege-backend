package com.rivilege.app.serviceimpl;

import com.rivilege.app.converter.RegistrationUserRequestDtoToUsers;
import com.rivilege.app.converter.UsersToLogInResponseDto;
import com.rivilege.app.dto.request.LogInRequestDto;
import com.rivilege.app.dto.request.RegistrationUserRequestDto;
import com.rivilege.app.dto.response.LogInResponseDto;
import com.rivilege.app.dto.response.ReferralUserDetailsResponseDto;
import com.rivilege.app.model.Users;
import com.rivilege.app.repository.UsersRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.AuthService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
  public ResponseEntity<?> registerNewMember(RegistrationUserRequestDto dto) {
    try {
      logger.info("Attempting to register new member with mobileNumber: {} and emailId: {}",
          dto.getMobileNumber(), dto.getEmailId());

      // Check if the user already exists with the given mobile number or email id
      boolean userExist = usersRepository.existsByMobileNumberOrEmailId(dto.getMobileNumber(), dto.getEmailId());

      if (userExist) {
        logger.warn("User with mobile number {} or email {} already exists.", dto.getMobileNumber(), dto.getEmailId());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST,
            "User with provided mobile number or email already exists.");
      }

      // Convert DTO to Users entity and save it
      Users users = registrationUserRequestDtoToUsers.convert(dto);
      users = usersRepository.saveAndFlush(users);

      // Prepare success response
      Map<String, String> response = new HashMap<>();
      response.put("memberId", users.getMemberId());

      logger.info("User registered successfully with memberId: {}", users.getMemberId());

      return baseResponse.successResponse("Member registration successful.", response);

    } catch (Exception e) {
      logger.error("Error occurred during user registration: {}", e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while processing your registration. Please try again later.");
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
    // Logger initialization
    Logger logger = LoggerFactory.getLogger(getClass());

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
