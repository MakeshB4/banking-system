package com.banking.notifications.repository;

import com.banking.notifications.entity.Notification;
import com.banking.notifications.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all unsent notifications by UserId (excluding soft-deleted records)
     */
    List<Notification> findByUserIdAndSentFalseAndDelFlgFalse(Long userId);

 }