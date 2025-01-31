package com.rivilege.app.serviceimpl;

import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.model.CommissionWallet;
import com.rivilege.app.model.IdPackages;
import com.rivilege.app.model.ReferralDetails;
import com.rivilege.app.model.Users;
import com.rivilege.app.repository.CommissionWalletRepository;
import com.rivilege.app.repository.IdPackagesRepository;
import com.rivilege.app.repository.ReferralDetailsRepository;
import com.rivilege.app.repository.UsersRepository;
import com.rivilege.app.response.BaseResponse;
import com.rivilege.app.service.AdminMemberIdActiveService;
import com.rivilege.app.utilities.StringUtils;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * this is a admin member id active service implementation class .
 *
 * @author kousik manik
 */
@Service
public class AdminMemberIdActiveServiceImpl implements AdminMemberIdActiveService {

  @Autowired
  private UsersRepository usersRepository;

  @Autowired
  private ReferralDetailsRepository referralDetailsRepository;

  @Autowired
  private IdPackagesRepository idPackagesRepository;

  @Autowired
  private CommissionWalletRepository commissionWalletRepository;

  @Autowired
  private BaseResponse baseResponse;

  private static final Logger logger = LoggerFactory.getLogger(AdminMemberIdActiveServiceImpl.class);

  /**
   * Activates a member by their member ID.
   *
   * @param memberId The unique ID of the member.
   * @return ResponseEntity indicating success or failure.
   */
  @Override
  @Transactional
  public ResponseEntity<?> memberIdActivation(String memberId) {
    try {
      logger.info("Activating member with ID: {}", memberId);

      Optional<Users> optionalUsers = usersRepository.findByMemberId(memberId);

      if (optionalUsers.isEmpty()) {
        logger.warn("Member ID not found: {}", memberId);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "Member not found.");
      }

      Users users = optionalUsers.get();

      if (users.isActive()) {
        logger.warn("Member ID Is Already Activated: {}", memberId);
        return baseResponse.errorResponse(HttpStatus.BAD_REQUEST, "User is Already Activated");
      }

      updateCommissionWallet(users.getMemberId(), users.getMobileNumber());

      users.setActive(true);
      usersRepository.save(users);

      logger.info("Member activated successfully: {}", memberId);
      return baseResponse.successResponse("Member activated successfully.");

    } catch (Exception e) {
      logger.error("Error activating member ID: {}", memberId, e);
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return baseResponse.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
          "An error occurred while activating the member.");
    }
  }

  private void updateCommissionWallet(String memberId, String mobileNumber) {
    List<CommissionWallet> commissionWalletList = new ArrayList<>();

    // Fetch referral details
    ReferralDetails referralDetails = referralDetailsRepository.findByReferredMemberId(memberId)
        .orElseThrow(() -> new RuntimeException("Referral details not found for memberId: " + memberId));
    UserDesignationType designation = referralDetails.getReferredDesignation();
    IdPackages idPackages = packageDetails(designation);

    // Process commission based on designation
    switch (designation) {
      case REGIONAL_MANAGER:
        processRegionalManager(commissionWalletList, referralDetails, memberId, mobileNumber, idPackages);
        break;
      case SUPER_DISTRIBUTOR:
        processSuperDistributor(commissionWalletList, referralDetails, memberId, mobileNumber, idPackages);
        break;
      case DISTRIBUTOR:
        processDistributor(commissionWalletList, referralDetails, memberId, mobileNumber, idPackages);
        break;
      case RETAILER:
        processRetailer(commissionWalletList, referralDetails, memberId, mobileNumber, idPackages);
        break;
      default:
        throw new RuntimeException("Invalid designation type");
    }
    // Save commission wallet entries
    if (!commissionWalletList.isEmpty()) {
      commissionWalletRepository.saveAll(commissionWalletList);
    }
  }

  private void processRegionalManager(List<CommissionWallet> commissionWalletList, ReferralDetails referralDetails,
                                      String memberId, String mobileNumber, IdPackages idPackages) {
    if (referralDetails.getDesignation().equals(UserDesignationType.GENERAL_MANAGER)) {
      double commissionBalance = idPackages.getJoiningAmount() * 0.30;
      commissionWalletList.add(createCommissionWallet(referralDetails, memberId, mobileNumber, commissionBalance));
    } else {
      throw new RuntimeException("Invalid referral designation for REGIONAL_MANAGER");
    }
  }

  private void processSuperDistributor(List<CommissionWallet> commissionWalletList, ReferralDetails referralDetails,
                                       String memberId, String mobileNumber, IdPackages idPackages) {
    if (referralDetails.getDesignation().equals(UserDesignationType.REGIONAL_MANAGER)) {
      double commissionBalance = idPackages.getJoiningAmount() * 0.30;
      commissionWalletList.add(createCommissionWallet(referralDetails, memberId, mobileNumber, commissionBalance));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.GENERAL_MANAGER)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getRegionalManagerId())) {
        double commissionBalance = idPackages.getJoiningAmount() * 0.30;
        commissionWalletList.add(createCommissionWallet(referralDetails.getRegionalManagerId(), memberId,
            mobileNumber, commissionBalance));

        ReferralDetails regionalManagerDetails = referralDetailsRepository.findByReferredMemberId(
                referralDetails.getRegionalManagerId())
            .orElseThrow(() -> new RuntimeException("Regional Manager details not found"));

        double regionalCommission = idPackages.getJoiningAmount() * 0.20;
        commissionWalletList.add(createCommissionWallet(regionalManagerDetails, memberId, mobileNumber,
            regionalCommission));
      } else {
        throw new RuntimeException("Missing Regional Manager ID for Super Distributor");
      }
    }
  }

  private void processDistributor(List<CommissionWallet> commissionWalletList, ReferralDetails referralDetails,
                                  String memberId, String mobileNumber, IdPackages idPackages) {
    if (referralDetails.getDesignation().equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
      double commissionBalance = idPackages.getJoiningAmount() * 0.30;
      commissionWalletList.add(createCommissionWallet(referralDetails, memberId, mobileNumber, commissionBalance));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.REGIONAL_MANAGER)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getSuperDistributorId())) {
        double commissionBalance = idPackages.getJoiningAmount() * 0.30;
        commissionWalletList.add(createCommissionWallet(referralDetails.getSuperDistributorId(), memberId,
            mobileNumber, commissionBalance));

        ReferralDetails superDistributorDetails = referralDetailsRepository.findByReferredMemberId(
                referralDetails.getSuperDistributorId())
            .orElseThrow(() -> new RuntimeException("Super Distributor details not found"));

        double commissionBalance1 = idPackages.getJoiningAmount() * 0.20;
        commissionWalletList.add(createCommissionWallet(superDistributorDetails, memberId, mobileNumber,
            commissionBalance1));

        ReferralDetails regionalManagerDetails = referralDetailsRepository.findByReferredMemberId(
                superDistributorDetails.getMemberId())
            .orElseThrow(() -> new RuntimeException("Regional Manager details not found"));

        double commissionBalance2 = idPackages.getJoiningAmount() * 0.10;
        commissionWalletList.add(createCommissionWallet(regionalManagerDetails, memberId,
            mobileNumber, commissionBalance2));
      } else {
        throw new RuntimeException("Missing Super Distributor ID for Distributor");
      }
    }
  }

  private void processRetailer(List<CommissionWallet> commissionWalletList, ReferralDetails referralDetails,
                               String memberId, String mobileNumber, IdPackages idPackages) {
    if (referralDetails.getDesignation().equals(UserDesignationType.DISTRIBUTOR)) {
      double commissionBalance = idPackages.getJoiningAmount() * 0.30;
      commissionWalletList.add(createCommissionWallet(referralDetails, memberId, mobileNumber, commissionBalance));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getDistributorId())) {
        double commissionBalance = idPackages.getJoiningAmount() * 0.30;
        commissionWalletList.add(createCommissionWallet(referralDetails.getDistributorId(), memberId, mobileNumber,
            commissionBalance));
      } else {
        throw new RuntimeException("Missing Distributor ID for Retailer");
      }

      double commissionBalance = idPackages.getJoiningAmount() * 0.20;
      commissionWalletList.add(createCommissionWallet(referralDetails, memberId, mobileNumber, commissionBalance));
    } else if (referralDetails.getDesignation().equals(UserDesignationType.REGIONAL_MANAGER)) {
      if (StringUtils.isNotNullAndNotEmpty(referralDetails.getDistributorId())) {
        double commissionBalance = idPackages.getJoiningAmount() * 0.30;
        commissionWalletList.add(createCommissionWallet(referralDetails.getDistributorId(), memberId, mobileNumber,
            commissionBalance));

        ReferralDetails distributorDetails = referralDetailsRepository.findByReferredMemberId(
                referralDetails.getDistributorId())
            .orElseThrow(() -> new RuntimeException("Distributor details not found"));

        if (distributorDetails.getDesignation().equals(UserDesignationType.SUPER_DISTRIBUTOR)) {
          double commissionBalance1 = idPackages.getJoiningAmount() * 0.20;
          commissionWalletList.add(createCommissionWallet(distributorDetails, memberId, mobileNumber,
              commissionBalance1));

          ReferralDetails regionalManagerDetails = referralDetailsRepository.findByReferredMemberId(
                  distributorDetails.getMemberId())
              .orElseThrow(() -> new RuntimeException("Regional Manager details not found"));

          double commissionBalance2 = idPackages.getJoiningAmount() * 0.10;
          commissionWalletList.add(createCommissionWallet(regionalManagerDetails, memberId, mobileNumber,
              commissionBalance2));
        }
      } else {
        throw new RuntimeException("Missing Distributor ID for Retailer under Regional Manager");
      }
    }
  }

  private CommissionWallet createCommissionWallet(ReferralDetails referralDetails, String memberId,
                                                  String mobileNumber, double commissionBalance) {
    return createCommissionWallet(referralDetails.getMemberId(), memberId, mobileNumber, commissionBalance);
  }

  private CommissionWallet createCommissionWallet(String memberId, String referredMemberId,
                                                  String referredMobileNumber, double commissionBalance) {
    updateMemberWalletBalance(memberId, commissionBalance);
    CommissionWallet commissionWallet = new CommissionWallet();
    commissionWallet.setMemberId(memberId);
    commissionWallet.setReferredMemberId(referredMemberId);
    commissionWallet.setReferredMobileNumber(referredMobileNumber);
    commissionWallet.setCommissionBalance(commissionBalance);
    return commissionWallet;
  }

  /**
   * Updates the commission wallet balance for a member.
   *
   * @param memberId          The unique ID of the member.
   * @param commissionBalance The amount to add to the commission wallet.
   */
  private void updateMemberWalletBalance(String memberId, double commissionBalance) {
    usersRepository.findByMemberId(memberId)
        .map(user -> {
          logger.info("Updating commission wallet for member ID: {}", memberId);
          user.setCommissionWallet(user.getCommissionWallet() + commissionBalance);
          return usersRepository.save(user);
        })
        .orElseThrow(() -> {
          logger.warn("User details not found for member ID: {}", memberId);
          return new RuntimeException("User details not found.");
        });

    logger.info("Commission wallet updated successfully for member ID: {}", memberId);
  }

  private IdPackages packageDetails(UserDesignationType userDesignationType) {
    Optional<IdPackages> optionalIdPackages = idPackagesRepository.findByUserDesignation(userDesignationType);
    if (optionalIdPackages.isEmpty()) {
      throw new RuntimeException("");
    }
    return optionalIdPackages.get();
  }

}
