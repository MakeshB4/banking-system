classDiagram
%% Enums
class NotificationType {
<<enumeration>>
EMAIL
SMS
}

    %% Main Entities
    class Notification {
        -Long notificationId
        -Long userId
        -NotificationType type
        -String recipient
        -String subject
        -String message
        -Boolean sent
        -LocalDateTime sentTime
        -Boolean delFlg
        -LocalDateTime creationTime
        -String createdBy
        +send() void
        +retry() void
    }

    %% Service Interfaces
    class INotificationService {
        <<interface>>
        +sendNotification(NotificationDTO) void
        +sendEmailNotification(Long, String, String) void
        +sendSMSNotification(Long, String) void
        +resendNotification(Long) void
        +getNotificationsByUser(Long) List~Notification~
    }

    %% Repository Interfaces
    class INotificationRepository {
        <<interface>>
        +save(Notification) Notification
        +findById(Long) Optional~Notification~
        +findByUserIdAndDelFlgFalse(Long) List~Notification~
        +findBySentFalse() List~Notification~
    }

    %% Relationships - Service to Repository
    INotificationService ..> INotificationRepository : uses
    
    %% Enum relationships
    Notification --> NotificationType : uses