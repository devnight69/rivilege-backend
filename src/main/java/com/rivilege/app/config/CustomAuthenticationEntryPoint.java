package com.rivilege.app.config;

import com.rivilege.app.dto.error.CustomErrorResponse;
import com.rivilege.app.response.ResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * this is a custom error message for unAuthorize entry point .
 *
 * @author kousik mnaik
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
                       AuthenticationException authException) throws IOException {

    CustomErrorResponse customErrorResponse = new CustomErrorResponse("RIVILEGE401",
        "Unauthorized: The provided token is invalid or expired. Please log in again.");
    ResponseDto responseDto = new ResponseDto();
    responseDto.setResponse(false);
    responseDto.setStatus(HttpStatus.UNAUTHORIZED);
    responseDto.setData(customErrorResponse);
    responseDto.setMessage("You need to be authenticated to access this resource.");

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(responseDto.toJson());
  }
}
