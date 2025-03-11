package com.rivilege.app.serviceimpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivilege.app.converter.UsersToLogInResponseDto;
import com.rivilege.app.dto.response.UserDetailsResponseDto;
import com.rivilege.app.model.ReferralDetails;
import com.rivilege.app.model.Users;
import com.rivilege.app.repository.ReferralDetailsRepository;
import com.rivilege.app.repository.UsersRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.UserService;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * this is a user service implementation class .
 *
 * @author kousik manik
 */
@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private ReferralDetailsRepository referralDetailsRepository;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  /**
   * Retrieves user details based on the provided member ID.
   *
   * @param memberId The unique identifier of the user.
   * @return ResponseEntity containing user details if found, otherwise an error message.
   */
  @Override
  public ResponseEntity<?> getUserDetails(String memberId) {
    logger.info("Fetching user details for memberId: {}", memberId);

    try {
      Optional<Users> optionalUser = usersRepository.findByMemberId(memberId);

      if (optionalUser.isPresent()) {
        logger.info("User found for memberId: {}", memberId);
        return baseResponse.successResponse(objectMapper.convertValue(optionalUser.get(),
            UserDetailsResponseDto.class));
      } else {
        logger.warn("User not found for memberId: {}", memberId);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Member Not Found");
      }
    } catch (Exception e) {
      logger.error("Error while fetching user details for memberId: {}", memberId, e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
  }


  /**
   * Retrieves referral details for a given member ID.
   *
   * @param memberId The unique identifier of the member.
   * @return ResponseEntity containing a list of referral details if found, or an error response if an exception occurs.
   */
  @Override
  public ResponseEntity<?> getReferralDetails(String memberId) {
    logger.info("Fetching referral details for memberId: {}", memberId);

    try {
      List<ReferralDetails> referralDetailsList = referralDetailsRepository.findByMemberId(memberId);
      logger.info("Referral details retrieved successfully for memberId: {}", memberId);
      return baseResponse.successResponse(referralDetailsList);

    } catch (Exception e) {
      logger.error("Error while fetching referral details for memberId: {}", memberId, e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
  }
}
