package com.rivilege.app.serviceimpl;

import com.rivilege.app.dto.request.IdPackageRegisterRequestDto;
import com.rivilege.app.model.IdPackages;
import com.rivilege.app.repository.IdPackagesRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.IdPackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * This is the implementation class for IdPackageService.
 * It handles the business logic for registering ID packages.
 *
 * @author kousik manik
 */
@Service
public class IdPackageServiceImpl implements IdPackageService {

  @Autowired
  private IdPackagesRepository idPackagesRepository;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(IdPackageServiceImpl.class);

  /**
   * Registers a new ID package based on the provided DTO.
   *
   * @param dto The request DTO containing the ID package details.
   * @return ResponseEntity with success or error response.
   */
  @Override
  public ResponseEntity<?> registerIdPackage(IdPackageRegisterRequestDto dto) {
    try {
      logger.info("Starting ID package registration for user designation: {}", dto.getUserDesignation());

      IdPackages idPackages = new IdPackages();
      idPackages.setUserDesignation(dto.getUserDesignation());
      idPackages.setGstPercentage(dto.getGstPercentage());
      idPackages.setJoiningAmount(dto.getJoiningAmount());
      idPackages.setJoiningGstAmount(dto.getJoiningGstAmount());

      idPackages = idPackagesRepository.saveAndFlush(idPackages);
      logger.info("ID package registered successfully with ID: {}", idPackages.getId());

      return baseResponse.successResponse(idPackages);
    } catch (Exception e) {
      logger.error("Error occurred while registering ID package: {}", e.getMessage(), e);
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while registering the ID package.");
    }
  }
}
