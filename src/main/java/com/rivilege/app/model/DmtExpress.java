package com.rivilege.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 * this is a dmt express details model .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "dmt_express",
    schema = "rivilege",
    indexes = {
        @Index(name = "idx_dmt_exp_member_id", columnList = "memberId"),
    })
public class DmtExpress {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "memberId", nullable = false)
  private String memberId;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(name = "mobile_number", nullable = false, unique = true)
  private String mobileNumber;

  @Column(name = "date_of_birth", nullable = false)
  private String dateOfBirth;

  @Column(name = "pin_code", nullable = false)
  private String pinCode;

  @Column(name = "address")
  private String address;

  @Column(name = "pan_number", nullable = false)
  private String panNumber;

  @Column(name = "aadhaar_number", nullable = false)
  private String aadhaarNumber;

  @Column(name = "register", nullable = false)
  private boolean register;

  @Column(name = "verify_kyc", nullable = false)
  private boolean verifyKyc;

  @Column(name = "cyrus_order_id")
  private String cyrusOrderId;

  @Column(name = "order_id", nullable = false)
  private String orderId;

  @Column(name = "cyrus_id")
  private String cyrusId;

  @Column(name = "remarks")
  private String remarks;

  @Column(name = "bank_ref_no")
  private String bankRefNo;

  @Column(name = "benename")
  private String beneName;

  @Column(name = "locked_amount")
  private double lockedAmount;

  @Column(name = "charged_amount")
  private double chargedAmount;

  @Column(name = "opening_balance")
  private double openingBalance;

  @Column(name = "verification_status")
  private String verificationStatus;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;


  @PrePersist
  private void beforeInsert() {
    this.setCreatedAt(new Date());
    this.setUpdatedAt(new Date());
    this.setVerifyKyc(false);
    this.setRegister(true);
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(new Date());
  }

}
