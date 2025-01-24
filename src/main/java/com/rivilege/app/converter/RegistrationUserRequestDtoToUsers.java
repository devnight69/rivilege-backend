package com.rivilege.app.converter;

import com.rivilege.app.dto.request.RegistrationUserRequestDto;
import com.rivilege.app.model.Users;
import com.rivilege.app.repository.UsersRepository;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * this is converter class RegistrationUserRequestDto to Users Model .
 *
 * @author kousik manik
 */
@Component
public class RegistrationUserRequestDtoToUsers implements Converter<RegistrationUserRequestDto, Users> {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UsersRepository usersRepository;

  /**
   * this is convert method .
   *
   * @param source @{@link RegistrationUserRequestDto}
   * @return @{@link Users}
   */
  @Override
  public @NonNull Users convert(@NonNull RegistrationUserRequestDto source) {
    Users users = new Users();

    users.setFullName(source.getFullName());
    users.setEmailId(source.getEmailId());
    users.setMobileNumber(source.getMobileNumber());
    users.setUserDesignation(source.getUserDesignation());
    users.setPanNumber(source.getPanNumber());
    users.setAadhaarNumber(source.getAadhaarNumber());
    users.setPassword(passwordEncoder.encode(source.getPassword()));
    users.setMemberId(generateUniqueMemberId());
    return users;
  }

  private String generateUniqueMemberId() {
    String referralCode;
    do {
      referralCode = generateMemberId();
    } while (usersRepository.existsByMemberId(referralCode.trim()));
    return referralCode.trim();
  }

  private static String generateMemberId() {
    String prefix = "R";
    String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder memberId = new StringBuilder(prefix);
    Random random = new Random();

    for (int i = 0; i < 9; i++) {
      int index = random.nextInt(alphanumeric.length());
      memberId.append(alphanumeric.charAt(index));
    }

    return memberId.toString();
  }
}
