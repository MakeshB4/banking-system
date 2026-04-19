package com.banking.notifications.entity;

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
    @Column(name = "updation_time")
    private LocalDateTime updationTime;

    @Column(name = "modification_time")
    private LocalDateTime modificationTime;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @Column(name = "del_flg", nullable = false)
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