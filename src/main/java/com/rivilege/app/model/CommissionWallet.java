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
 * this is commission wallet database schema .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "commission_wallet",
    schema = "rivilege",
    indexes = {
        @Index(name = "idx_users_member_id", columnList = "member_id"),
        @Index(name = "idx_users_mobile_number", columnList = "mobile_number"),
        @Index(name = "idx_users_referred_member_id", columnList = "referred_member_id"),
        @Index(name = "idx_users_referred_mobile_number", columnList = "referred_mobile_number")
    }
)
public class CommissionWallet {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "member_id", nullable = false)
  private String memberId;

  @Column(name = "mobile_number", nullable = false)
  private String mobileNumber;

  @Column(name = "referred_member_id", nullable = false)
  private String referredMemberId;

  @Column(name = "referred_mobile_number", nullable = false)
  private String referredMobileNumber;

  @Column(name = "commission_balance", nullable = false)
  private double commissionBalance;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;


  @PrePersist
  private void beforeInsert() {
    this.setCreatedAt(new Date());
    this.setUpdatedAt(new Date());
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(new Date());
  }

}
