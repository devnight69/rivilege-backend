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
 * this is a mobile recharge history model .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "mobile_recharge_history",
    schema = "rivilege",
    indexes = {
        @Index(name = "idx_mobile_recharge_history_member_id", columnList = "memberId"),
    })
public class MobileRechargeHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "memberId", nullable = false)
  private String memberId;

  @Column(name = "usertx", nullable = false)
  private String usertx;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "mobile_number", nullable = false)
  private String mobileNumber;

  @Column(name = "amount", nullable = false)
  private String amount;

  @Column(name = "api_trans_id")
  private String apiTransId;

  @Column(name = "operator_ref")
  private String operatorRef;

  @Column(name = "transaction_date")
  private String transactionDate;

  @Column(name = "commission", nullable = false)
  private double commission;

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
