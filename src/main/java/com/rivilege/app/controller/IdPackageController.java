package com.rivilege.app.controller;

import com.rivilege.app.dto.request.IdPackageRegisterRequestDto;
import com.rivilege.app.service.IdPackageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * this is a id package controller class .
 *
 * @author kousik manik
 */
@RestController
@RequestMapping("/api/v1/id/package")
public class IdPackageController {

  @Autowired
  private IdPackageService idPackageService;


  @PostMapping("/register")
  public ResponseEntity<?> registerIdPackage(@Valid @RequestBody IdPackageRegisterRequestDto dto) {
    return idPackageService.registerIdPackage(dto);
  }

}
