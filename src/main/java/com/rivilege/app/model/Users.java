package com.rivilege.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rivilege.app.enums.UserDesignationType;
import com.rivilege.app.enums.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import ulid4j.Ulid;

/**
 * Users database model class.
 *
 * @author kousik
 */
@Entity
@Getter
@Setter
@Table(
    name = "users",
    schema = "rivilege",
    indexes = {
        @Index(name = "idx_users_ulid", columnList = "ulid"),
        @Index(name = "idx_users_member_id", columnList = "member_id"),
        @Index(name = "idx_users_email_id", columnList = "email_id"),
        @Index(name = "idx_users_mobile_number", columnList = "mobile_number"),
        @Index(name = "idx_users_user_designation", columnList = "user_designation"),
        @Index(name = "idx_users_active_block_user", columnList = "active, block_user")
    }
)
public class Users {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "ulid", nullable = false, unique = true)
  private String ulId;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "member_id", nullable = false, unique = true)
  private String memberId;

  @Column(name = "email_id", nullable = false)
  private String emailId;

  @Column(name = "mobile_number", nullable = false, unique = true)
  private String mobileNumber;

  @Column(name = "user_designation", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserDesignationType userDesignation;

  @Column(name = "user_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserType userType;

  @Column(name = "pan_number", nullable = false, unique = true)
  private String panNumber;

  @Column(name = "aadhaar_number", nullable = false, unique = true)
  private String aadhaarNumber;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "main_wallet", nullable = false)
  private double mainWallet;

  @Column(name = "recharge_wallet", nullable = false)
  private double rechargeWallet;

  @Column(name = "commission_wallet", nullable = false)
  private double commissionWallet;

  @Column(name = "banking_wallet", nullable = false)
  private double bankingWallet;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "block_user", nullable = false)
  private boolean blockUser;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;


  @PrePersist
  private void beforeInsert() {
    this.setUlId(new Ulid().next());
    this.setActive(false);
    this.setBlockUser(false);
    this.setMainWallet(0.0);
    this.setRechargeWallet(0.0);
    this.setCommissionWallet(0.0);
    this.setBankingWallet(0.0);
    this.setCreatedAt(new Date());
    this.setUpdatedAt(new Date());
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(new Date());
  }

}
