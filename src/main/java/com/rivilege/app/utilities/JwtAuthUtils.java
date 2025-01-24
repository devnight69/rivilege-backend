package com.rivilege.app.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rivilege.app.dto.jwt.JwtPayloadDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

/**
 * Jwt utilities for create or validate and decode token.
 *
 * @author Praveen
 */
@Component
public class JwtAuthUtils {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthUtils.class);

  private static final String AUTHORITIES_KEY = "RivilegeAuth";
  @Value("${jwt.expiration-hours}")
  private Long expireHours;

  @Value("${jwt.expiration-refresh-hours}")
  private Long refreshExpireHours;

  @Value("${jwt.secret-key}")
  private String plainSecret;

  private Key encodedSecret;

  @Autowired
  private ObjectMapper objectMapper;


  @PostConstruct
  protected void init() {
    this.encodedSecret = generateEncodedSecret(this.plainSecret);
  }

  protected Date getExpirationTime() {
    Date now = new Date();
    long expireInMilis = TimeUnit.HOURS.toMillis(expireHours);
    return new Date(expireInMilis + now.getTime());
  }

  protected Date getRefreshExpirationTime() {
    Date now = new Date();
    long expireInMilis = TimeUnit.HOURS.toMillis(refreshExpireHours);
    return new Date(expireInMilis + now.getTime());
  }


  protected Key generateEncodedSecret(String plainSecret) {
    if (StringUtils.isEmpty(plainSecret)) {
      throw new IllegalArgumentException("JWT secret cannot be null or empty.");
    }
    byte[] keyBytes = Decoders.BASE64.decode(plainSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * Generate JWT Token.
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   * @throws JsonProcessingException If Json processing fails.
   */
  public String generateToken(Authentication authentication, JwtPayloadDto jwtPayloadDto) throws JsonProcessingException {
    Map<String, Object> payload = new HashMap<>();
    payload.put("mobileNumber", jwtPayloadDto.getMobileNumber());
    payload.put("fullName", jwtPayloadDto.getFullName());
    payload.put("userUlid", jwtPayloadDto.getUserUlid());
    payload.put("userId", jwtPayloadDto.getUserId());
    payload.put("userDesignation", jwtPayloadDto.getUserDesignation());
    payload.put("active", jwtPayloadDto.getActive());
    payload.put("userType", jwtPayloadDto.getUserType());
    payload.put("tokenType", jwtPayloadDto.getTokenType());

    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    try {

      return Jwts.builder()
          .id(jwtPayloadDto.getUserId())
          .claim(AUTHORITIES_KEY, authorities)
          .subject(authentication.getName())
          .claims(payload)
          .issuedAt(new Date())
          .expiration(getExpirationTime())
          .signWith(encodedSecret)
          .compact();
    } catch (Exception ex) {
      logger.error("Getting error at generating token = {}", ex.getMessage());
      throw ex;
    }
  }

  /**
   * Generate JWT Token.
   *
   * @param jwtPayloadDto @{@link JwtPayloadDto}
   * @return @{@link String}
   * @throws JsonProcessingException If Json processing fails.
   */
  public String generateRefreshToken(Authentication authentication, JwtPayloadDto jwtPayloadDto)
      throws JsonProcessingException {
    Map<String, Object> payload = new HashMap<>();
    payload.put("mobileNumber", jwtPayloadDto.getMobileNumber());
    payload.put("fullName", jwtPayloadDto.getFullName());
    payload.put("userUlid", jwtPayloadDto.getUserUlid());
    payload.put("userId", jwtPayloadDto.getUserId());
    payload.put("userDesignation", jwtPayloadDto.getUserDesignation());
    payload.put("active", jwtPayloadDto.getActive());
    payload.put("userType", jwtPayloadDto.getUserType());
    payload.put("tokenType", jwtPayloadDto.getTokenType());

    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    try {

      return Jwts.builder()
          .id(jwtPayloadDto.getUserId())
          .claim(AUTHORITIES_KEY, authorities)
          .subject(authentication.getName())
          .claims(payload)
          .issuedAt(new Date())
          .expiration(getRefreshExpirationTime())
          .signWith(encodedSecret)
          .compact();
    } catch (Exception ex) {
      logger.error("Getting error at generating refresh token = {}", ex.getMessage());
      throw ex;
    }
  }


  /**
   * this class for validate ambula token .
   *
   * @param authToken @{@link String}
   * @return @{@link Boolean}
   */
  public boolean validateToken(String authToken) {
    Jwts.parser().verifyWith((SecretKey) encodedSecret).build().parseSignedClaims(authToken);
    return true;
  }

  /**
   * Decode JWT token to its original payload data.
   *
   * @param authToken @{@link String}
   * @return @JwtPayloadDto
   */
  public JwtPayloadDto decodeToken(String authToken) {
    Claims claims = Jwts.parser().verifyWith((SecretKey) encodedSecret).build()
        .parseSignedClaims(authToken).getPayload();
    JwtPayloadDto dto = new JwtPayloadDto();
    dto.setMobileNumber((String) claims.get("mobileNumber"));
    dto.setFullName((String) claims.get("fullName"));
    dto.setUserUlid((String) claims.get("userUlid"));
    dto.setUserId((String) claims.get("userId"));
    dto.setUserDesignation((String) claims.get("userDesignation"));
    dto.setActive((Boolean) claims.get("active"));
    dto.setUserType((String) claims.get("userType"));
    dto.setTokenType((String) claims.get("tokenType"));
    return dto;
  }


}
