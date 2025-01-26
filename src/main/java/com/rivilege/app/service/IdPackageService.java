package com.rivilege.app.service;

import com.rivilege.app.dto.request.IdPackageRegisterRequestDto;
import org.springframework.http.ResponseEntity;

/**
 * this is a Id Package service class .
 *
 * @author kousik manik
 */
public interface IdPackageService {

  public ResponseEntity<?> registerIdPackage(IdPackageRegisterRequestDto dto);

}
