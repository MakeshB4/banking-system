package com.banking.useraccounts.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "creation_time", nullable = false, updatable = false)
    private LocalDateTime creationTime;

    @LastModifiedDate
    private LocalDateTime updationTime;

    private LocalDateTime modificationTime;

    private String createdBy;

    private String modifiedBy;

    private Boolean delFlg = false;

    @PrePersist
    protected void onCreate() {
        if (this.delFlg == null) {
            this.delFlg = false;
        }

        if (this.creationTime == null) {
            this.creationTime = LocalDateTime.now();
        }
        this.modificationTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modificationTime = LocalDateTime.now();
    }
}
