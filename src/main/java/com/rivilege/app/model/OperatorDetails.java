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
 * this is a operator details table .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "operator_details",
    schema = "master",
    indexes = {
        @Index(name = "idx_operator_service_type_name", columnList = "service_type_name"),
    })
public class OperatorDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "service_type_name", nullable = false)
  private String serviceTypeName;

  @Column(name = "operator_code", nullable = false)
  private String operatorCode;

  @Column(name = "operator_name", nullable = false)
  private String operatorName;

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
