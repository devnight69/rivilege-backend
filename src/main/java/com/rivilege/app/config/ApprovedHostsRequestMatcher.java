package com.rivilege.app.config;


import com.rivilege.app.utilities.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Request Host validation.
 *
 * @author Kousik Manik.
 */
@Slf4j
public class ApprovedHostsRequestMatcher implements RequestMatcher {
  private final Pattern allowedHostPattern = Pattern.compile(
      "^(localhost:9030|10\\.100\\.0\\.8:9030|192\\.168\\.29\\.179:9030|20\\.193\\.140\\.74:9030"
          + "|20\\.197\\.12\\.45(:9002)?|www\\.ambula\\.tech(:9002)?|20\\.193\\.140\\.79(:9002)?"
          + "|www\\.ambula\\.co\\.in(:9002)?|20\\.40\\.43\\.64(:9002)?)$",
      Pattern.CASE_INSENSITIVE
  );

  @Override
  public boolean matches(HttpServletRequest request) {
    String host = request.getHeader("Host").trim();
    if (!StringUtils.isNotNullAndNotEmpty(host)) {
      log.error("Host header is empty or null");
      return false;
    }
    boolean isHostAllowed = allowedHostPattern.matcher(host).matches();
    if (!isHostAllowed) {
      log.error("host=[{}] is not allowed", host);
    }
    return isHostAllowed;
  }
}