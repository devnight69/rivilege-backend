package com.rivilege.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;


/**
 * this is a dmt charges details model .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "dmt_charges_details",
    schema = "master")
public class DmtChargesDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "min_amount", nullable = false)
  private Integer minAmount;

  @Column(name = "max_amount", nullable = false)
  private Integer maxAmount;

  @Column(name = "base_charge", nullable = false)
  private Double baseCharge;

  @Column(name = "gst", nullable = false)
  private Double gst;

  @Column(name = "total_charge", nullable = false)
  private Double totalCharge;

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
