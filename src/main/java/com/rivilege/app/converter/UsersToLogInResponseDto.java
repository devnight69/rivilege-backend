package com.rivilege.app.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rivilege.app.dto.jwt.JwtPayloadDto;
import com.rivilege.app.dto.response.LogInResponseDto;
import com.rivilege.app.dto.response.UserDetailsResponseDto;
import com.rivilege.app.enums.JwtTokenType;
import com.rivilege.app.model.Users;
import com.rivilege.app.utilities.JwtAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * this is a users to users details response dto .
 *
 * @author kousik manik
 */
@Component
public class UsersToLogInResponseDto implements Converter<Users, LogInResponseDto> {

  @Autowired
  private JwtAuthUtils jwtAuthUtils;

  /**
   * this is a converter method .
   *
   * @param source @{@link Users}
   * @return @{@link UserDetailsResponseDto}
   */
  @Override
  public @NonNull LogInResponseDto convert(@NonNull Users source) {
    UserDetailsResponseDto dto = new UserDetailsResponseDto();
    dto.setUlId(source.getUlId());
    dto.setFullName(source.getFullName());
    dto.setMemberId(source.getMemberId());
    dto.setEmailId(source.getEmailId());
    dto.setMobileNumber(source.getMobileNumber());
    dto.setUserDesignation(source.getUserDesignation());
    dto.setUserType(source.getUserType());
    dto.setPanNumber(source.getPanNumber());
    dto.setAadhaarNumber(source.getAadhaarNumber());
    dto.setMainWallet(source.getMainWallet());
    dto.setRechargeWallet(source.getRechargeWallet());
    dto.setCommissionWallet(source.getCommissionWallet());
    dto.setBankingWallet(source.getBankingWallet());
    dto.setActive(source.isActive());
    dto.setBlockUser(source.isBlockUser());
    dto.setCreatedAt(source.getCreatedAt());
    dto.setUpdatedAt(source.getUpdatedAt());

    try {
      return new LogInResponseDto(
          generateJwtToken(makeJwtPayLoadDto(dto)),
          generateJwtRefreshToken(makeJwtPayLoadDto(dto)),
          dto
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

  }


  /**
   * this is a jwtPayload dto method .
   *
   * @param user @{@link UserDetailsResponseDto}
   * @return @{@link JwtPayloadDto}
   */
  private JwtPayloadDto makeJwtPayLoadDto(UserDetailsResponseDto user) {
    JwtPayloadDto jwtPayloadDto = new JwtPayloadDto();
    jwtPayloadDto.setFullName(user.getFullName());
    jwtPayloadDto.setMobileNumber(user.getMobileNumber());
    jwtPayloadDto.setUserUlid(user.getUlId());
    jwtPayloadDto.setMemberId(user.getMemberId());
    jwtPayloadDto.setUserDesignation(user.getUserDesignation().name());
    jwtPayloadDto.setActive(user.isActive());
    jwtPayloadDto.setUserType(user.getUserType().name());

    return jwtPayloadDto;
  }


  /**
   * this is a generate jwt token method .
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   */
  private String generateJwtToken(JwtPayloadDto jwtPayloadDto) throws JsonProcessingException {

    jwtPayloadDto.setTokenType(JwtTokenType.AT.name());
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
    jwtPayloadDto.setTokenType(JwtTokenType.RT.name());
    UsernamePasswordAuthenticationToken authenticationToken;
    authenticationToken = new UsernamePasswordAuthenticationToken(
        jwtPayloadDto.getMobileNumber(),
        null
    );
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    return jwtAuthUtils.generateRefreshToken(authenticationToken, jwtPayloadDto);
  }


}
