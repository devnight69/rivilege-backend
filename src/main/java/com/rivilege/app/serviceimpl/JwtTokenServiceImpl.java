package com.rivilege.app.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rivilege.app.dto.jwt.JwtPayloadDto;
import com.rivilege.app.dto.request.TokenUsingRefreshTokenRequestDto;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.JwtTokenService;
import com.rivilege.app.utilities.JwtAuthUtils;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * this is a jwt token service implementation class .
 *
 * @author kousik manik
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

  @Autowired
  private JwtAuthUtils jwtAuthUtils;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenServiceImpl.class);

  /**
   * Generates a new access token using a valid refresh token.
   *
   * @param dto The request containing the refresh token and member ID.
   * @return ResponseEntity containing the new access and refresh tokens, or an error response.
   */
  @Override
  public ResponseEntity<?> generateTokenFromRefreshToken(TokenUsingRefreshTokenRequestDto dto) {
    try {
      logger.info("Received request to generate token from refresh token for memberId: {}", dto.getMemberId());

      boolean tokenValid = jwtAuthUtils.validateToken(dto.getToken());
      if (!tokenValid) {
        logger.warn("Invalid refresh token provided for memberId: {}", dto.getMemberId());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Invalid refresh token. Please provide a valid token.");
      }

      JwtPayloadDto jwtPayloadDto = jwtAuthUtils.decodeToken(dto.getToken());
      if (!"RT".equals(jwtPayloadDto.getTokenType())) {
        logger.warn("Provided token is not a refresh token for memberId: {}", dto.getMemberId());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Invalid token type. Please provide a valid refresh token.");
      }

      if (!jwtPayloadDto.getMemberId().equals(dto.getMemberId())) {
        logger.warn("Mismatch between token memberId and provided memberId: {}", dto.getMemberId());
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Token mismatch. Please provide a valid refresh token.");
      }

      Map<String, String> response = new HashMap<>();
      response.put("activeToken", generateJwtToken(jwtPayloadDto));
      response.put("refreshToken", generateJwtRefreshToken(jwtPayloadDto));

      logger.info("Successfully generated new tokens for memberId: {}", dto.getMemberId());
      return baseResponse.successResponse(response);
    } catch (Exception e) {
      logger.error("Error while generating token from refresh token for memberId: {}", dto.getMemberId(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
    }
  }

  /**
   * this is a generate jwt token method .
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   */
  private String generateJwtToken(JwtPayloadDto jwtPayloadDto) throws JsonProcessingException {

    UsernamePasswordAuthenticationToken authenticationToken;
    authenticationToken = new UsernamePasswordAuthenticationToken(
        jwtPayloadDto.getMobileNumber(),
        null
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    return jwtAuthUtils.generateToken(authenticationToken, jwtPayloadDto);
  }

  /**
   * this is a generate jwt token method .
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   */
  private String generateJwtRefreshToken(JwtPayloadDto jwtPayloadDto) throws JsonProcessingException {

    UsernamePasswordAuthenticationToken authenticationToken;
    authenticationToken = new UsernamePasswordAuthenticationToken(
        jwtPayloadDto.getMobileNumber(),
        null
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    return jwtAuthUtils.generateRefreshToken(authenticationToken, jwtPayloadDto);
  }


}
