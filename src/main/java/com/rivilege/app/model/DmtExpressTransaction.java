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
 * dmt express transaction details .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "dmt_express_transaction",
    schema = "rivilege",
    indexes = {
        @Index(name = "idx_dmt_exp_tn_member_id", columnList = "memberId"),
        @Index(name = "idx_dmt_exp_tn_order_id", columnList = "orderId"),
        @Index(name = "idx_dmt_exp_tn_transaction_status", columnList = "transaction_status")
    })
public class DmtExpressTransaction {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "memberId", nullable = false)
  private String memberId;

  @Column(name = "order_id", nullable = false)
  private String orderId;

  @Column(name = "cyrus_order_id")
  private String cyrusOrderId;

  @Column(name = "cyrus_id", nullable = false)
  private String cyrusId;

  @Column(name = "opening_balance", nullable = false)
  private double openingBalance;

  @Column(name = "locked_amount", nullable = false)
  private double lockedAmount;

  @Column(name = "cyrus_charged_amount", nullable = false)
  private double cyrusChargedAmount;

  @Column(name = "charged_amount", nullable = false)
  private double chargedAmount;

  @Column(name = "rrn")
  private String rrn;

  @Column(name = "transaction_status", nullable = false)
  private String transactionStatus;

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
