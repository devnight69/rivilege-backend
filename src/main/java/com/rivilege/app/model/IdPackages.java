package com.rivilege.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rivilege.app.enums.UserDesignationType;
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
 * this is a id packages details database schema .
 *
 * @author kousik manik
 */
@Entity
@Getter
@Setter
@Table(
    name = "idPackages",
    schema = "master",
    indexes = {
        @Index(name = "idx_users_user_designation", columnList = "user_designation")
    }
)
public class IdPackages {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JsonIgnore
  private Long id;

  @Column(name = "ulid", nullable = false, unique = true)
  private String ulId;

  @Column(name = "user_designation", nullable = false)
  @Enumerated(EnumType.STRING)
  private UserDesignationType userDesignation;

  @Column(name = "joining_amount", nullable = false)
  private double joiningAmount;

  @Column(name = "joining_gst_amount", nullable = false)
  private double joiningGstAmount;

  @Column(name = "gst_percentage", nullable = false)
  private Integer gstPercentage;

  @Column(name = "created_at", nullable = false)
  private Date createdAt;

  @Column(name = "updated_at", nullable = false)
  private Date updatedAt;


  @PrePersist
  private void beforeInsert() {
    this.setUlId(new Ulid().next());
    this.setCreatedAt(new Date());
    this.setUpdatedAt(new Date());
  }

  @PreUpdate
  private void beforeUpdate() {
    this.setUpdatedAt(new Date());
  }


}
